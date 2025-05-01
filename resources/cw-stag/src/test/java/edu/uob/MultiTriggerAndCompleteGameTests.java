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
 * Tests for multiple trigger actions and complete game flow
 */
public class MultiTriggerAndCompleteGameTests {

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
        // Note: The path to treasure will be created during tests

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
        Furniture chest = new Furniture("chest", "A wooden chest"); // can be opened like door
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
        cellar.addEntity(chest);
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
        
        // 2. Unlock door action (has multiple triggers)
        CustomAction unlockDoorAction = new CustomAction();
        unlockDoorAction.addTriggers("unlock");
        unlockDoorAction.addTriggers("open");
        unlockDoorAction.addSubjects("key");
        unlockDoorAction.addSubjects("door");
        unlockDoorAction.addConsumed("door");
        unlockDoorAction.addProduced("cellar->cave");
        unlockDoorAction.addNarration("You unlocked the door with the key!");
        
        // 3. Open chest action (shares trigger with door)
        CustomAction openChestAction = new CustomAction();
        openChestAction.addTriggers("open");
        openChestAction.addTriggers("unlock");
        openChestAction.addSubjects("key");
        openChestAction.addSubjects("chest");
        openChestAction.addConsumed("chest");
        openChestAction.addProduced("key"); // produces another key (e.g., for a different lock)
        openChestAction.addNarration("You opened the chest and found another key!");
        
        // 4. Pay elf action
        CustomAction payAction = new CustomAction();
        payAction.addTriggers("pay");
        payAction.addTriggers("give");
        payAction.addSubjects("coin");
        payAction.addSubjects("elf");
        payAction.addConsumed("coin");
        payAction.addProduced("shovel");
        payAction.addNarration("The elf takes your coin and gives you a shovel!");
        
        // 5. Dig ground action
        CustomAction digAction = new CustomAction();
        digAction.addTriggers("dig");
        digAction.addSubjects("shovel");
        digAction.addSubjects("ground");
        digAction.addProduced("gold");
        digAction.addNarration("You dig in the ground and find gold!");
        
        // 6. Cut vines action
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
        gameWorld.addAction(unlockDoorAction);
        gameWorld.addAction(openChestAction);
        gameWorld.addAction(payAction);
        gameWorld.addAction(digAction);
        gameWorld.addAction(cutVinesAction);

        // Initialize controller
        gameController = new GameController(gameWorld, gameState);
    }

    /* Test Case 5.1: Multiple Trigger Actions */
    @Test
    void testMultipleTriggerActions() {
        // Test actions with multiple possible triggers
        
        // Get the key and go to cellar
        gameController.handleCommand("player: goto cave");
        gameController.handleCommand("player: get key");
        gameController.handleCommand("player: goto forest");
        gameController.handleCommand("player: goto cabin");
        gameController.handleCommand("player: goto cellar");
        
        // Test first trigger - "unlock"
        String unlockResult = gameController.handleCommand("player: unlock door with key");
        assertTrue(unlockResult.contains("unlocked") || unlockResult.contains("door"), 
            "Unlock command should work with the unlock trigger");
        
        // Reset the game world for the second test
        setup();
        
        // Get the key and go to cellar again
        gameController.handleCommand("player: goto cave");
        gameController.handleCommand("player: get key");
        gameController.handleCommand("player: goto forest");
        gameController.handleCommand("player: goto cabin");
        gameController.handleCommand("player: goto cellar");
        
        // Test second trigger - "open"
        String openResult = gameController.handleCommand("player: open door with key");
        assertTrue(openResult.contains("unlocked") || openResult.contains("door"), 
            "Open command should work with the open trigger");
    }

    /* Test Case 5.2: Ambiguous Actions */
    @Test
    void testAmbiguousActions() {
        // Test handling of ambiguous commands
        
        // Get the key and go to cellar
        gameController.handleCommand("player: goto cave");
        gameController.handleCommand("player: get key");
        gameController.handleCommand("player: goto forest");
        gameController.handleCommand("player: goto cabin");
        gameController.handleCommand("player: goto cellar");
        
        // Test ambiguous command (both door and chest can be opened)
        String ambiguousResult = gameController.handleCommand("player: open with key");
        assertTrue(ambiguousResult.contains("ambiguous") || ambiguousResult.contains("specific"), 
            "System should indicate ambiguity when the command is unclear");
        
        // Use specific command to resolve ambiguity
        String doorResult = gameController.handleCommand("player: open door with key");
        assertTrue(doorResult.contains("unlocked") || doorResult.contains("door"), 
            "Specific command should resolve ambiguity correctly for door");
        
        // Setup again to test chest
        setup();
        gameController.handleCommand("player: goto cave");
        gameController.handleCommand("player: get key");
        gameController.handleCommand("player: goto forest");
        gameController.handleCommand("player: goto cabin");
        gameController.handleCommand("player: goto cellar");
        
        // Test specific command for chest
        String chestResult = gameController.handleCommand("player: open chest with key");
        assertTrue(chestResult.contains("chest") || chestResult.contains("found"), 
            "Specific command should resolve ambiguity correctly for chest");
    }

    /* Test Case 7.1: Game Completion */
    @Test
    void testGameCompletion() {
        // Test a complete playthrough of the game
        
        /* Stage 1: Get axe from cabin */
        gameController.handleCommand("player: get axe");
        
        /* Stage 2: Go to forest and chop tree */
        gameController.handleCommand("player: goto forest");
        gameController.handleCommand("player: chop tree with axe");
        String lookForestResult = gameController.handleCommand("player: look");
        assertTrue(lookForestResult.contains("log"), "Log should be produced after chopping tree");
        
        /* Stage 3: Get the log and go to cave */
        gameController.handleCommand("player: get log");
        gameController.handleCommand("player: goto cave");
        
        /* Stage 4: Get sword and key */
        gameController.handleCommand("player: get sword");
        gameController.handleCommand("player: get key");
        
        /* Stage 5: Return to cabin and go to cellar */
        gameController.handleCommand("player: goto forest");
        gameController.handleCommand("player: goto cabin");
        gameController.handleCommand("player: goto cellar");
        
        /* Stage 6: Get coin and pay elf to get shovel */
        gameController.handleCommand("player: get coin");
        gameController.handleCommand("player: pay elf with coin");
        String invAfterPay = gameController.handleCommand("player: inventory");
        assertTrue(invAfterPay.contains("shovel"), "Shovel should be in inventory after paying elf");
        
        /* Stage 7: Dig in ground to find gold */
        gameController.handleCommand("player: dig ground with shovel");
        String lookCellarResult = gameController.handleCommand("player: look");
        assertTrue(lookCellarResult.contains("gold"), "Gold should be produced after digging");
        
        /* Stage 8: Unlock door to create path */
        gameController.handleCommand("player: unlock door with key");
        String gotoResult = gameController.handleCommand("player: goto cave");
        assertTrue(gotoResult.contains("moved to cave"), "Should be able to go directly to cave after unlocking door");
        
        /* Stage 9: Cut vines to reveal treasure room */
        gameController.handleCommand("player: cut vines with sword");
        String treasureResult = gameController.handleCommand("player: goto treasure");
        assertTrue(treasureResult.contains("moved to treasure"), "Should be able to go to treasure room after cutting vines");
        
        /* Stage 10: Find gold in treasure room */
        String finalResult = gameController.handleCommand("player: look");
        assertTrue(finalResult.contains("gold") || finalResult.contains("Gold"), 
            "Gold should be in the treasure room at the end of the game");
    }

    /* Test Case 8.1: Multiple Similar Entities */
    @Test
    void testMultipleSimilarEntities() {
        // Test commands with multiple similar entities in the same location
        
        // Setup a scenario with multiple trees
        setup();
        Location forest = gameWorld.getLocation("forest");
        Furniture secondTree = new Furniture("tree", "Another oak tree"); // Second tree with same name
        forest.addEntity(secondTree);
        
        // Try to chop tree without specifying which one
        gameController.handleCommand("player: get axe");
        gameController.handleCommand("player: goto forest");
        
        String ambiguousResult = gameController.handleCommand("player: chop tree");
        assertTrue(ambiguousResult.contains("ambiguous") || ambiguousResult.contains("Which tree"), 
            "System should indicate ambiguity when multiple similar entities exist");
    }
} 