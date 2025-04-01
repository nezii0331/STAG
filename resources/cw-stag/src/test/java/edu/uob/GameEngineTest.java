package edu.uob;

import static org.junit.jupiter.api.Assertions.*;

import edu.uob.entities.Furniture;
import edu.uob.entities.GameCharacter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.uob.actions.CustomAction;
import edu.uob.entities.Artefact;
import edu.uob.entities.Location;
import edu.uob.games.GameController;
import edu.uob.games.GameState;
import edu.uob.games.GameWorld;

public class GameEngineTest {

    private GameWorld gameWorld;
    private GameState gameState;
    private GameController gameController;

    @BeforeEach
    void setup() {
        // Create a simple game world for testing
        gameWorld = new GameWorld();
        gameState = new GameState();

        // Create locations
        Location cabin = new Location("cabin", "A cozy wooden cabin in the woods");
        Location forest = new Location("forest", "A dense, dark forest with tall trees");
        Location cave = new Location("cave", "A mysterious cave with strange markings");
        Location storeroom = new Location("storeroom", "A place to store entities not in the game yet");
        Location cellar = new Location("cellar", "A dark, musty cellar beneath the cabin");

        // Add paths between locations
        cabin.addPath("forest");
        cabin.addPath("cellar");
        forest.addPath("cabin");
        forest.addPath("cave");
        cave.addPath("forest");
        cellar.addPath("cabin");

        // Create artefacts
        Artefact axe = new Artefact("axe", "A sharp axe for chopping wood");
        Artefact key = new Artefact("key", "A rusty old key");
        Artefact log = new Artefact("log", "A wooden log");
        Artefact potion = new Artefact("potion", "A magic healing potion");

        // Create characters
        GameCharacter elf = new GameCharacter("elf", "An angry forest elf");

        // Add artefacts to locations
        cabin.addArtefact(axe);
        cabin.addArtefact(potion);
        forest.addArtefact(key);
        storeroom.addArtefact(log);

        // Add characters to locations
        cellar.addEntity(elf);

        // Add entities to locations
        Furniture tree = new Furniture("tree", "A tall oak tree");
        forest.addEntity(tree);

        // Add locations to world
        gameWorld.addLocation(cabin);
        gameWorld.addLocation(forest);
        gameWorld.addLocation(cave);
        gameWorld.addLocation(storeroom);
        gameWorld.addLocation(cellar);

        // Create a custom action (e.g., chop tree with axe)
        CustomAction chopAction = new CustomAction();
        chopAction.addTriggers("chop");
        chopAction.addTriggers("cut");
        chopAction.addSubjects("axe");
        chopAction.addSubjects("tree");
        chopAction.addConsumed("tree");
        chopAction.addProduced("log");
        chopAction.addNarration("You chopped down the tree with your axe!");

        // Add action to the world
        gameWorld.addAction(chopAction);

        // Initialize controller
        gameController = new GameController(gameWorld, gameState);
    }

    @Test
    void testCommandFormat() {
        // Test invalid command format
        String result = gameController.handleCommand("invalidcommand");
        assertEquals("Your command is invalid, please use like [player : command].", result);

        // Test valid command format
        result = gameController.handleCommand("player: look");
        assertTrue(result.contains("You are at cabin now."));
    }

    @Test
    void testLookCommand() {
        String result = gameController.handleCommand("player: look");
        assertTrue(result.contains("You are at cabin now."));
        assertTrue(result.contains("A cozy wooden cabin in the woods"));
        assertTrue(result.contains("axe"));
    }

    @Test
    void testInventoryCommand() {
        // Initially inventory should be empty
        String result = gameController.handleCommand("player: inventory");
        assertEquals("You are carrying nothing.", result);

        // Get an item
        gameController.handleCommand("player: get axe");

        // Check inventory again
        result = gameController.handleCommand("player: inv");
        assertTrue(result.contains("axe"));
    }

    @Test
    void testGetCommand() {
        // Get an item
        String result = gameController.handleCommand("player: get axe");
        assertEquals("You picked up the axe.", result);

        // Try to get an item that doesn't exist
        result = gameController.handleCommand("player: get banana");
        assertEquals("There is no such item to pick up.", result);
    }

    @Test
    void testDropCommand() {
        // Get an item first
        gameController.handleCommand("player: get axe");

        // Drop the item
        String result = gameController.handleCommand("player: drop axe");
        assertEquals("You dropped the axe.", result);

        // Try to drop an item that's not in inventory
        result = gameController.handleCommand("player: drop axe");
        assertEquals("You don't have that item to drop.", result);
    }

    @Test
    void testGotoCommand() {
        // Go to a connected location
        String result = gameController.handleCommand("player: goto forest");
        assertEquals("You moved to forest.", result);

        // Check current location with look
        result = gameController.handleCommand("player: look");
        assertTrue(result.contains("You are at forest now."));

        // Try to go to a non-connected location
        result = gameController.handleCommand("player: goto cave");
        assertEquals("You moved to cave.", result);

        // Try to go to a non-existent location
        result = gameController.handleCommand("player: goto mountain");
        assertEquals("You can't go there from here.", result);
    }


// -----------------------------
// Health System Tests
// -----------------------------

    @Test
    void testInitialHealthIsThree() {
        String result = gameController.handleCommand("alice: health");
        assertTrue(result.contains("3"), "Initial health should be 3");
    }

    @Test
    void testDrinkPotionIncreasesHealth() {
        gameController.handleCommand("alice: get potion");   // pick up potion
        gameController.handleCommand("alice: drink potion");
        String result = gameController.handleCommand("alice: health");
        assertTrue(result.contains("3"), "Health should be capped at 3");
    }

    @Test
    void testAttackDecreasesHealth() {
        gameController.handleCommand("alice: goto cellar");
        gameController.handleCommand("alice: fight elf");
        String result = gameController.handleCommand("alice: health");
        assertTrue(result.contains("2"), "Health should decrease by 1");
    }

    @Test
    void testDieWhenHealthZero() {
        gameController.handleCommand("alice: goto cellar");
        gameController.handleCommand("alice: fight elf"); // health = 2
        gameController.handleCommand("alice: fight elf"); // health = 1
        gameController.handleCommand("alice: fight elf"); // health = 0
        String result = gameController.handleCommand("alice: health");
        assertTrue(result.contains("3"), "Health should reset to 3 after death");

        String look = gameController.handleCommand("alice: look");
        assertTrue(look.contains("axe") || look.contains("potion"), "Player items should be dropped on death");
    }

    @Test
    void testCustomActionExecution() {
        // Get the axe
        gameController.handleCommand("player: get axe");
        System.out.println("Debug: Player got axe");

        // Go to the forest
        gameController.handleCommand("player: goto forest");
        System.out.println("Debug: Player moved to forest");

        // 检查树是否在森林中
        String lookBeforeChop = gameController.handleCommand("player: look");
        System.out.println("Debug - Before chopping, look result: " + lookBeforeChop);
        assertTrue(lookBeforeChop.contains("tree"), "Tree should be in the forest before chopping");

        // Execute custom action
        String result = gameController.handleCommand("player: chop tree with axe");
        System.out.println("Debug - Chop command result: " + result);
        assertEquals("You chopped down the tree with your axe!", result);

        // 等待一下，确保所有操作已完成
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check that tree is gone and log is produced
        String lookResult = gameController.handleCommand("player: look");
        System.out.println("Debug - After chopping, look result: " + lookResult);

        // 更详细的断言
        assertFalse(lookResult.contains("There is a tree here"), "Tree should be gone after chopping");
        assertTrue(lookResult.contains("log") || lookResult.contains("There is a log here"),
                "Log should be produced after chopping tree. Look result: " + lookResult);
    }

    @Test
    void testCustomActionWithoutRequiredItems() {
        // Go to the forest without the axe
        gameController.handleCommand("player: goto forest");

        // Try to execute custom action without required item
        String result = gameController.handleCommand("player: chop tree");
        assertEquals("You're missing something required to perform this action.", result);
    }

    @Test
    void testMultiplePlayersInSameLocation() {
        // Setup player1
        gameController.handleCommand("player1: look");

        // Setup player2
        gameController.handleCommand("player2: look");

        // Check if players can see each other
        String result = gameController.handleCommand("player1: look");
        assertTrue(result.contains("Player player2 is here as well."));
    }

    @Test
    void testAlternativeTriggerWords() {
        // Get the axe
        gameController.handleCommand("player: get axe");

        // Go to the forest
        gameController.handleCommand("player: goto forest");

        // Use alternative trigger word
        String result = gameController.handleCommand("player: cut tree with axe");
        assertEquals("You chopped down the tree with your axe!", result);
    }
}
