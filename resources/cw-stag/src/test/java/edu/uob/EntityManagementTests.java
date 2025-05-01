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
 * Tests for entity management functionality
 * Covers scenarios like entity consumption, production, and path creation
 */
public class EntityManagementTests {

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
        Location treasure = new Location("treasure", "A treasure room with gold");

        // Add paths between locations
        cabin.addPath("forest");
        cabin.addPath("cellar");
        forest.addPath("cabin");
        forest.addPath("cave");
        cave.addPath("forest");
        cellar.addPath("cabin");
        // Note: The path to treasure room will be created during tests

        // Create artefacts
        Artefact axe = new Artefact("axe", "A sharp axe for chopping wood");
        Artefact key = new Artefact("key", "A rusty old key");
        Artefact log = new Artefact("log", "A wooden log");
        Artefact potion = new Artefact("potion", "A magic healing potion");
        Artefact coin = new Artefact("coin", "A shiny gold coin");
        Artefact shovel = new Artefact("shovel", "A sturdy shovel for digging");
        Artefact sword = new Artefact("sword", "A sharp sword for cutting");
        Artefact gold = new Artefact("gold", "Valuable gold nuggets");

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
        forest.addEntity(pixie);
        cellar.addEntity(door);
        cellar.addEntity(elf);
        cellar.addEntity(coin);
        cellar.addEntity(ground);
        cave.addEntity(vines);
        cave.addEntity(sword);
        cave.addEntity(key);
        treasure.addEntity(gold);

        // Add locations to world
        gameWorld.addLocation(cabin);
        gameWorld.addLocation(forest);
        gameWorld.addLocation(cave);
        gameWorld.addLocation(cellar);
        gameWorld.addLocation(treasure);

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
        unlockAction.addConsumed("door");
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

    /* Test Case 2.1: Entity Consumption */
    @Test
    void testEntityConsumption() {
        // Test that entities are properly consumed during actions
        
        // First get the axe
        gameController.handleCommand("player: get axe");
        
        // Move to the forest
        gameController.handleCommand("player: goto forest");
        
        // Check that the tree is in the forest
        String beforeResult = gameController.handleCommand("player: look");
        assertTrue(beforeResult.contains("tree"), "The tree should be in the forest before chopping");
        
        // Perform the action
        gameController.handleCommand("player: chop tree with axe");
        
        // Verify the tree was consumed/removed
        String afterResult = gameController.handleCommand("player: look");
        assertFalse(afterResult.contains("tree"), "The tree should be consumed/removed after chopping");
    }

    /* Test Case 2.2: Entity Production */
    @Test
    void testEntityProduction() {
        // Test that new entities are properly created during actions
        
        // First get the axe
        gameController.handleCommand("player: get axe");
        
        // Move to the forest
        gameController.handleCommand("player: goto forest");
        
        // Check that the log is not in the forest initially
        String beforeResult = gameController.handleCommand("player: look");
        assertFalse(beforeResult.contains("log"), "The log should not be in the forest before chopping");
        
        // Perform the action
        gameController.handleCommand("player: chop tree with axe");
        
        // Verify the log was produced
        String afterResult = gameController.handleCommand("player: look");
        assertTrue(afterResult.contains("log"), "A log should be produced after chopping the tree");
    }

    /* Test Case 2.3: Location as Subject */
    @Test
    void testLocationAsSubject() {
        // Test actions that use the current location as a subject
        
        // First get the coin
        gameController.handleCommand("player: goto cellar");
        gameController.handleCommand("player: get coin");
        
        // Pay the elf to get the shovel
        gameController.handleCommand("player: pay elf with coin");
        
        // Verify we have the shovel
        String inventoryResult = gameController.handleCommand("player: inventory");
        assertTrue(inventoryResult.contains("shovel"), "Should have received a shovel from the elf");
        
        // Dig in the ground
        String digResult = gameController.handleCommand("player: dig ground with shovel");
        
        // Verify gold was found
        assertTrue(digResult.contains("gold") || digResult.contains("Gold"), "Should find gold when digging");
        
        String lookResult = gameController.handleCommand("player: look");
        assertTrue(lookResult.contains("gold") || lookResult.contains("Gold"), "Gold should appear in the location");
    }

    /* Test Case 3.1: Path Creation */
    @Test
    void testPathCreation() {
        // Test that new paths are created after certain actions
        
        // Get the key
        gameController.handleCommand("player: goto cave");
        gameController.handleCommand("player: get key");
        
        // Go to cellar
        gameController.handleCommand("player: goto forest");
        gameController.handleCommand("player: goto cabin");
        gameController.handleCommand("player: goto cellar");
        
        // Check that we can't go to the cave directly from cellar initially
        String beforeResult = gameController.handleCommand("player: goto cave");
        assertTrue(beforeResult.contains("can't go") || beforeResult.contains("can't go"), 
            "Should not be able to go to cave from cellar initially");
        
        // Unlock the door
        gameController.handleCommand("player: unlock door with key");
        
        // Now we should be able to go to the cave
        String gotoResult = gameController.handleCommand("player: goto cave");
        assertTrue(gotoResult.contains("moved to cave") || gotoResult.contains("moved to cave"), 
            "Should be able to go to cave after unlocking the door");
        
        // Similarly, test the vines path creation
        gameController.handleCommand("player: get sword");
        gameController.handleCommand("player: cut vines with sword");
        
        // Now we should be able to go to the treasure room
        String treasureResult = gameController.handleCommand("player: goto treasure");
        assertTrue(treasureResult.contains("moved to treasure") || treasureResult.contains("moved to treasure"), 
            "Should be able to go to treasure room after cutting the vines");
    }
} 