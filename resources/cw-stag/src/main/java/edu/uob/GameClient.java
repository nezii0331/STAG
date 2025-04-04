package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
* This is the sample client for you to connect to your game server.
*
* <p>Input are taken from stdin and output goes to stdout.
*/
public final class GameClient {

    private static final char END_OF_TRANSMISSION = 4;

    public static void main(String[] args) throws IOException {
        String username = args[0];
        while (!Thread.interrupted()) GameClient.handleNextCommand(username);
    }

    private static void handleNextCommand(String username) throws IOException {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(username);
        promptBuilder.append(":> ");
        System.out.print(promptBuilder.toString());
        BufferedReader commandLine = new BufferedReader(new InputStreamReader(System.in));
        String command = commandLine.readLine();
        try (var socket = new Socket("localhost", 8888);
        var socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        var socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            StringBuilder commandBuilder = new StringBuilder();
            commandBuilder.append(username);
            commandBuilder.append(": ");
            commandBuilder.append(command);
            commandBuilder.append("\n");
            socketWriter.write(commandBuilder.toString());
            socketWriter.flush();
            String incomingMessage = socketReader.readLine();
            if (incomingMessage == null) {
                throw new IOException("Server disconnected (end-of-stream)");
            }
            StringBuilder eotBuilder = new StringBuilder();
            eotBuilder.append("");
            eotBuilder.append(END_OF_TRANSMISSION);
            eotBuilder.append("");
            String eotString = eotBuilder.toString();
            while (incomingMessage != null && !incomingMessage.contains(eotString)) {
                System.out.println(incomingMessage);
                incomingMessage = socketReader.readLine();
            }
        }
    }
}
