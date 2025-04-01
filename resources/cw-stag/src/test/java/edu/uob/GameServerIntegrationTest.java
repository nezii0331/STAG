package edu.uob;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Disabled;
@Disabled
public class GameServerIntegrationTest {

    @TempDir
    Path tempDir;
    
    private GameServer server;
    private File entitiesFile;
    private File actionsFile;
    private Thread serverThread;
    
    @BeforeEach
    void setup() throws IOException {
        // Create test entity and action files
        createTestFiles();
        
        // Start the server in a separate thread
        server = new GameServer(entitiesFile, actionsFile);
        serverThread = new Thread(() -> {
            try {
                server.blockingListenOn(8888);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
        
        // Give the server time to start
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @AfterEach
    void tearDown() {
        // Shutdown the server
        if (server != null) {
            try {
                server.blockingListenOn(8888);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // Interrupt the server thread
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
            try {
                serverThread.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Test
    @Timeout(5)
    void testBasicCommands() throws IOException {
        // Test look command
        String response = sendCommand("alice: look");
        assertTrue(response.contains("cabin"));
        assertTrue(response.contains("axe"));
        
        // Test get command
        response = sendCommand("alice: get axe");
        assertTrue(response.contains("picked up the axe"));
        
        // Test inventory command
        response = sendCommand("alice: inventory");
        assertTrue(response.contains("axe"));
        
        // Test goto command
        response = sendCommand("alice: goto forest");
        assertTrue(response.contains("moved to forest"));
        
        // Test look after moving
        response = sendCommand("alice: look");
        assertTrue(response.contains("forest"));
        assertTrue(response.contains("tree"));
    }
    
    @Test
    @Timeout(5)
    void testCustomAction() throws IOException {
        // First get the axe and go to forest
        sendCommand("bob: get axe");
        sendCommand("bob: goto forest");
        
        // Test chop tree action
        String response = sendCommand("bob: chop tree with axe");
        assertTrue(response.contains("chopped down the tree"));
        
        // Check that tree is gone and log is produced
        response = sendCommand("bob: look");
        assertFalse(response.contains("tree"));
        assertTrue(response.contains("log"));
    }
    
    @Test
    @Timeout(5)
    void testMultiplePlayersInteraction() throws IOException {
        // First player moves to forest
        sendCommand("player1: get axe");
        sendCommand("player1: goto forest");
        
        // Second player looks to see if they are alone
        String response = sendCommand("player2: look");
        assertFalse(response.contains("player1"));
        
        // Second player joins first player in forest
        sendCommand("player2: goto forest");
        
        // First player should now see second player
        response = sendCommand("player1: look");
        assertTrue(response.contains("player2"));
        
        // Second player should see first player
        response = sendCommand("player2: look");
        assertTrue(response.contains("player1"));
    }
    
    @Test
    @Timeout(5)
    void testCommandFlexibility() throws IOException {
        // Test case insensitivity
        String response = sendCommand("alice: GET axe");
        assertTrue(response.contains("picked up the axe"));
        
        // Test decorated command
        response = sendCommand("alice: please goto forest");
        assertTrue(response.contains("moved to forest"));
        
        // Test alternative phrasing
        sendCommand("alice: look");  // First look to verify there's a tree
        response = sendCommand("alice: cut down the tree with my axe");
        assertTrue(response.contains("chopped down the tree"));
    }
    
    @Test
    @Timeout(5)
    void testInvalidCommands() throws IOException {
        // Test invalid command format
        String response = sendCommand("get axe");
        assertTrue(response.contains("invalid"));
        
        // Test nonexistent command
        response = sendCommand("alice: dance");
        assertTrue(response.contains("don't understand"));
        
        // Test going to nonexistent location
        response = sendCommand("alice: goto mountain");
        assertTrue(response.contains("can't go there"));
        
        // Test getting nonexistent item
        response = sendCommand("alice: get banana");
        assertTrue(response.contains("no such item"));
    }
    
    /**
     * Creates test files needed for the game server
     */
    private void createTestFiles() throws IOException {
        // Create entities.dot file
        entitiesFile = tempDir.resolve("entities.dot").toFile();
        String entitiesContent = 
            "digraph game_graph {\n" +
            "  # Locations\n" +
            "  cabin [label=\"cabin\\nA cozy log cabin\"];\n" +
            "  forest [label=\"forest\\nA dense, dark forest\"];\n" +
            "  cave [label=\"cave\\nA mysterious cave\"];\n" +
            "  \n" +
            "  # Paths\n" +
            "  cabin -> forest;\n" +
            "  forest -> cabin;\n" +
            "  forest -> cave;\n" +
            "  cave -> forest;\n" +
            "  \n" +
            "  # Artefacts\n" +
            "  axe [label=\"axe\\nA sharp axe\", shape=polygon, sides=4];\n" +
            "  key [label=\"key\\nA rusty key\", shape=polygon, sides=4];\n" +
            "  \n" +
            "  # Location of artefacts\n" +
            "  cabin -> axe;\n" +
            "  forest -> key;\n" +
            "  \n" +
            "  # Furniture\n" +
            "  tree [label=\"tree\\nA tall oak tree\", shape=polygon, sides=6];\n" +
            "  door [label=\"door\\nA wooden door\", shape=polygon, sides=6];\n" +
            "  \n" +
            "  # Location of furniture\n" +
            "  forest -> tree;\n" +
            "  cave -> door;\n" +
            "  \n" +
            "  # Characters\n" +
            "  elf [label=\"elf\\nA forest elf\", shape=ellipse];\n" +
            "  \n" +
            "  # Location of characters\n" +
            "  forest -> elf;\n" +
            "}";
        Files.writeString(entitiesFile.toPath(), entitiesContent);
        
        // Create actions.xml file
        actionsFile = tempDir.resolve("actions.xml").toFile();
        String actionsContent = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<actions>\n" +
            "  <action>\n" +
            "    <triggers>\n" +
            "      <keyphrase>chop</keyphrase>\n" +
            "      <keyphrase>cut</keyphrase>\n" +
            "      <keyphrase>cut down</keyphrase>\n" +
            "    </triggers>\n" +
            "    <subjects>\n" +
            "      <entity>axe</entity>\n" +
            "      <entity>tree</entity>\n" +
            "    </subjects>\n" +
            "    <consumed>\n" +
            "      <entity>tree</entity>\n" +
            "    </consumed>\n" +
            "    <produced>\n" +
            "      <entity>log</entity>\n" +
            "    </produced>\n" +
            "    <narration>You chopped down the tree with your axe!</narration>\n" +
            "  </action>\n" +
            "  <action>\n" +
            "    <triggers>\n" +
            "      <keyphrase>unlock</keyphrase>\n" +
            "      <keyphrase>open</keyphrase>\n" +
            "    </triggers>\n" +
            "    <subjects>\n" +
            "      <entity>key</entity>\n" +
            "      <entity>door</entity>\n" +
            "    </subjects>\n" +
            "    <consumed>\n" +
            "    </consumed>\n" +
            "    <produced>\n" +
            "    </produced>\n" +
            "    <narration>You unlocked the door with the key!</narration>\n" +
            "  </action>\n" +
            "</actions>";
        Files.writeString(actionsFile.toPath(), actionsContent);
    }
    
    /**
     * Helper method to send a command to the server and get the response
     */
    private String sendCommand(String command) throws IOException {
        try (Socket socket = new Socket("localhost", 8888);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            // Send the command
            out.println(command);
            
            // Read the response
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            
            return response.toString();
        }
    }
}
