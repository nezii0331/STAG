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
        File entitiesFile = Paths.get("resources" + File.separator + "cw-stag" + File.separator + "config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("resources" + File.separator + "cw-stag" + File.separator + "config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
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
                throw new IllegalArgumentException("Entity file does not exist: " + entitiesFile.getAbsolutePath());
            }
            if (!actionsFile.exists()) {
                throw new IllegalArgumentException("Action file does not exist: " + actionsFile.getAbsolutePath());
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
            System.err.println("Error initializing GameServer: " + e.getMessage());
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
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
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
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
