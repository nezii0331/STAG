package edu.uob;

import edu.uob.actions.CustomAction;
import edu.uob.actions.GameAction;
import edu.uob.games.GameController;
import edu.uob.games.GameState;
import edu.uob.games.GameWorld;
import edu.uob.parsers.ActionParser;
import edu.uob.parsers.EntityParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Set;

public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;

    public static void main(String[] args) throws IOException {
        StringBuilder entitiesPathBuilder = new StringBuilder();
        entitiesPathBuilder.append("resources");
        entitiesPathBuilder.append(File.separator);
        entitiesPathBuilder.append("cw-stag");
        entitiesPathBuilder.append(File.separator);
        entitiesPathBuilder.append("config");
        entitiesPathBuilder.append(File.separator);
        entitiesPathBuilder.append("basic-entities.dot");
        File entitiesFile = Paths.get(entitiesPathBuilder.toString()).toAbsolutePath().toFile();
        
        StringBuilder actionsPathBuilder = new StringBuilder();
        actionsPathBuilder.append("resources");
        actionsPathBuilder.append(File.separator);
        actionsPathBuilder.append("cw-stag");
        actionsPathBuilder.append(File.separator);
        actionsPathBuilder.append("config");
        actionsPathBuilder.append(File.separator);
        actionsPathBuilder.append("basic-actions.xml");
        File actionsFile = Paths.get(actionsPathBuilder.toString()).toAbsolutePath().toFile();
        
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    private GameWorld world;
    private GameState state;
    private GameController controller;

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Instanciates a new server instance, specifying a game with some configuration files
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    */
    public GameServer(File entitiesFile, File actionsFile) {
        // TODO implement your server logic here
        try {
            if (entitiesFile == null || actionsFile == null) {
                throw new IllegalArgumentException("Entity or action file cannot be null");
            }
            if (!entitiesFile.exists()) {
                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append("Entity file does not exist: ");
                errorMsg.append(entitiesFile.getAbsolutePath());
                throw new IllegalArgumentException(errorMsg.toString());
            }
            if (!actionsFile.exists()) {
                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append("Action file does not exist: ");
                errorMsg.append(actionsFile.getAbsolutePath());
                throw new IllegalArgumentException(errorMsg.toString());
            }
            EntityParser entityparser = new EntityParser();
            ActionParser actionparser = new ActionParser();
            this.world = entityparser.parseEntities(entitiesFile);
            if (this.world == null) {
                throw new IllegalStateException("EntityParser returned null GameWorld!");
            }

            Set<CustomAction> actions = actionparser.parseAction(actionsFile);
            for (GameAction action : actions) {
                this.world.addAction(action);
            }
            this.state = new GameState();
            this.controller = new GameController(world, state);
            if (this.controller == null) {
                throw new IllegalStateException("GameController is null!");
            }

        } catch (Exception e) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("Error initializing GameServer: ");
            errorMsg.append(e.getMessage());
            System.err.println(errorMsg.toString());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize GameServer", e);
            // Rethrow to prevent server from starting with invalid state
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * This method handles all incoming game commands and carries out the corresponding actions.</p>
    *
    * @param command The incoming command to be processed
    */
    public String handleCommand(String command) {
        // TODO implement your server logic here
        return controller.handleCommand(command);
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Starts a *blocking* socket server listening for new connections.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            StringBuilder portMsg = new StringBuilder();
            portMsg.append("Server listening on port ");
            portMsg.append(portNumber);
            System.out.println(portMsg.toString());
            while (!Thread.interrupted()) {
                try {
                    this.blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Handles an incoming connection from the socket server.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                StringBuilder msgBuilder = new StringBuilder();
                msgBuilder.append("Received message from ");
                msgBuilder.append(incomingCommand);
                System.out.println(msgBuilder.toString());
                String result = this.handleCommand(incomingCommand);
                writer.write(result);
                StringBuilder endBuilder = new StringBuilder();
                endBuilder.append("\n");
                endBuilder.append(END_OF_TRANSMISSION);
                endBuilder.append("\n");
                writer.write(endBuilder.toString());
                writer.flush();
            }
        }
    }
}
