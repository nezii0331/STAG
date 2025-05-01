package edu.uob;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.uob.actions.CustomAction;
import edu.uob.entities.Artefact;
import edu.uob.entities.Furniture;
import edu.uob.entities.GameCharacter;
import edu.uob.entities.Location;
import edu.uob.games.GameController;
import edu.uob.games.GameState;
import edu.uob.games.GameWorld;

/**
 * Tests for command parsing functionality
 * Covers scenarios like partial commands, additional words, and changed word order
 */
public class CommandParsingTests {

    private GameWorld gameWorld;
    private GameState gameState;
    private GameController gameController;

    @BeforeEach
    void setup() {
        // Create a game world for testing
        gameWorld = new GameWorld();
        gameState = new GameState();

        // Create locations
        Location cabin = new Location("cabin", "A cozy wooden cabin in the woods");
        Location forest = new Location("forest", "A dense, dark forest with tall trees");
        Location cave = new Location("cave", "A mysterious cave with strange markings");
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
        Artefact coin = new Artefact("coin", "A shiny gold coin");
        Artefact shovel = new Artefact("shovel", "A sturdy shovel for digging");
        Artefact sword = new Artefact("sword", "A sharp sword for cutting");

        // Create furniture and characters
        Furniture tree = new Furniture("tree", "A tall oak tree");
        Furniture door = new Furniture("door", "A locked wooden door");
        Furniture ground = new Furniture("ground", "The dirt ground");
        Furniture vines = new Furniture("vines", "Thick vines blocking a path");
        GameCharacter elf = new GameCharacter("elf", "A forest elf");
        GameCharacter pixie = new GameCharacter("pixie", "A mischievous pixie");

        // Add entities to locations
        cabin.addEntity(axe);
        cabin.addEntity(potion);
        forest.addEntity(tree);
        forest.addEntity(key);
        forest.addEntity(pixie);
        cellar.addEntity(door);
        cellar.addEntity(elf);
        cellar.addEntity(coin);
        cave.addEntity(vines);
        cave.addEntity(sword);

        // Add locations to world
        gameWorld.addLocation(cabin);
        gameWorld.addLocation(forest);
        gameWorld.addLocation(cave);
        gameWorld.addLocation(cellar);

        // Create custom actions
        
        // 1. Chop tree action
        CustomAction chopAction = new CustomAction();
        chopAction.addTriggers("chop");
        chopAction.addTriggers("cut");
        chopAction.addSubjects("axe");
        chopAction.addSubjects("tree");
        chopAction.addConsumed("tree");
        chopAction.addProduced("log");
        chopAction.addNarration("You chopped down the tree with your axe!");
        
        // 2. Unlock door action
        CustomAction unlockAction = new CustomAction();
        unlockAction.addTriggers("unlock");
        unlockAction.addTriggers("open");
        unlockAction.addSubjects("key");
        unlockAction.addSubjects("door");
        unlockAction.addProduced("cellar->cave");
        unlockAction.addNarration("You unlocked the door with the key!");
        
        // 3. Pay elf action
        CustomAction payAction = new CustomAction();
        payAction.addTriggers("pay");
        payAction.addTriggers("give");
        payAction.addSubjects("coin");
        payAction.addSubjects("elf");
        payAction.addConsumed("coin");
        payAction.addProduced("shovel");
        payAction.addNarration("The elf takes your coin and gives you a shovel!");
        
        // 4. Dig ground action
        CustomAction digAction = new CustomAction();
        digAction.addTriggers("dig");
        digAction.addSubjects("shovel");
        digAction.addSubjects("ground");
        digAction.addProduced("gold");
        digAction.addNarration("You dig in the ground and find gold!");
        
        // 5. Cut vines action
        CustomAction cutVinesAction = new CustomAction();
        cutVinesAction.addTriggers("cut");
        cutVinesAction.addTriggers("slash");
        cutVinesAction.addSubjects("sword");
        cutVinesAction.addSubjects("vines");
        cutVinesAction.addConsumed("vines");
        cutVinesAction.addProduced("cave->treasure");
        cutVinesAction.addNarration("You cut through the vines, revealing a path!");

        // Add actions to the world
        gameWorld.addAction(chopAction);
        gameWorld.addAction(unlockAction);
        gameWorld.addAction(payAction);
        gameWorld.addAction(digAction);
        gameWorld.addAction(cutVinesAction);

        // Initialize controller
        gameController = new GameController(gameWorld, gameState);
    }

    /* Test Case 1.1: Partial Commands */
    @Test
    void testPartialCommands() {
        // Player should be able to use 'chop tree' without explicitly mentioning the axe
        // First get the axe
        gameController.handleCommand("player: get axe");
        // Move to the forest
        gameController.handleCommand("player: goto forest");
        // Chop tree without mentioning axe
        String result = gameController.handleCommand("player: chop tree");
        
        assertTrue(result.contains("chopped") || result.contains("Chopped"), 
            "The system should handle the command 'chop tree' without explicit mention of the axe");
        
        // Verify the tree was consumed and log was produced
        String lookResult = gameController.handleCommand("player: look");
        assertFalse(lookResult.contains("tree"), "The tree should be consumed after chopping");
        assertTrue(lookResult.contains("log"), "A log should be produced after chopping the tree");
    }

    /* Test Case 1.2: Additional Words in Commands */
    @Test
    void testAdditionalWords() {
        // Player should be able to use verbose command with extra words
        // First get the axe
        gameController.handleCommand("player: get axe");
        // Move to the forest
        gameController.handleCommand("player: goto forest");
        // Use verbose command with extra words
        String result = gameController.handleCommand("player: could you please chop down the tree with the axe");
        
        assertTrue(result.contains("chopped") || result.contains("Chopped"), 
            "The system should handle verbose commands with extra words");
        
        // Verify the tree was consumed and log was produced
        String lookResult = gameController.handleCommand("player: look");
        assertFalse(lookResult.contains("tree"), "The tree should be consumed after chopping");
        assertTrue(lookResult.contains("log"), "A log should be produced after chopping the tree");
    }

    /* Test Case 1.3: Changed Word Order */
    @Test
    void testChangedWordOrder() {
        // Player should be able to use commands with different word orders
        // First get the axe
        gameController.handleCommand("player: get axe");
        // Move to the forest
        gameController.handleCommand("player: goto forest");
        
        // Reset the world for this test
        setup();
        gameController.handleCommand("player: get axe");
        gameController.handleCommand("player: goto forest");
        
        // Use command with different word order
        String result = gameController.handleCommand("player: use axe to chop tree");
        
        assertTrue(result.contains("chopped") || result.contains("Chopped"), 
            "The system should handle commands with different word orders");
        
        // Verify the tree was consumed and log was produced
        String lookResult = gameController.handleCommand("player: look");
        assertFalse(lookResult.contains("tree"), "The tree should be consumed after chopping");
        assertTrue(lookResult.contains("log"), "A log should be produced after chopping the tree");
    }

    /* Test Case 6.1: Invalid Usernames */
    @Test
    void testInvalidUsernames() {
        // Commands from users with invalid names should be rejected
        String result = gameController.handleCommand("123!@#: look");
        
        assertTrue(result.contains("Invalid player name") || result.contains("invalid"), 
            "The system should reject commands from users with invalid names");
        
        // Try another invalid username
        result = gameController.handleCommand("player@name: look");
        
        assertTrue(result.contains("Invalid player name") || result.contains("invalid"), 
            "The system should reject commands from users with invalid names");
    }
} 