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
 * Tests for health and inventory management functionality
 * Covers scenarios like health reduction in combat and inventory loss on death
 */
public class HealthInventoryTests {

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
        Artefact sword = new Artefact("sword", "A shiny sword");
        Artefact shield = new Artefact("shield", "A sturdy shield");
        Artefact helmet = new Artefact("helmet", "A protective helmet");

        // Create characters
        GameCharacter elf = new GameCharacter("elf", "A forest elf");
        GameCharacter pixie = new GameCharacter("pixie", "A mischievous pixie");
        GameCharacter troll = new GameCharacter("troll", "A dangerous troll");

        // Add entities to locations
        cabin.addEntity(axe);
        cabin.addEntity(potion);
        cabin.addEntity(shield);
        cabin.addEntity(helmet);
        forest.addEntity(pixie);
        forest.addEntity(sword);
        cellar.addEntity(elf);
        cave.addEntity(troll);
        cave.addEntity(key);

        // Add locations to world
        gameWorld.addLocation(cabin);
        gameWorld.addLocation(forest);
        gameWorld.addLocation(cave);
        gameWorld.addLocation(cellar);

        // Initialize controller
        gameController = new GameController(gameWorld, gameState);
    }

    /* Test Case 4.1: Health Reduction */
    @Test
    void testHealthReduction() {
        // Test that health decreases after combat
        
        // Check initial health
        String initialHealth = gameController.handleCommand("player: health");
        assertTrue(initialHealth.contains("3"), "Initial health should be 3");
        
        // Go to the forest and fight the pixie
        gameController.handleCommand("player: goto forest");
        String fightResult = gameController.handleCommand("player: fight pixie");
        
        // Verify health reduction
        assertTrue(fightResult.contains("fought") || fightResult.contains("damage"), 
            "Fight command should indicate combat occurred");
        
        // Check health after fighting
        String healthAfterFight = gameController.handleCommand("player: health");
        assertTrue(healthAfterFight.contains("2"), "Health should be reduced to 2 after fighting");
        
        // Fight again
        gameController.handleCommand("player: fight pixie");
        
        // Check health after second fight
        String healthAfterSecondFight = gameController.handleCommand("player: health");
        assertTrue(healthAfterSecondFight.contains("1"), "Health should be reduced to 1 after fighting twice");
    }

    /* Test Case 4.2: Inventory Loss on Death */
    @Test
    void testInventoryLossOnDeath() {
        // Test that all inventory items are lost when health reaches zero
        
        // Collect several items
        gameController.handleCommand("player: get axe");
        gameController.handleCommand("player: get shield");
        gameController.handleCommand("player: get helmet");
        
        // Verify items in inventory
        String initialInventory = gameController.handleCommand("player: inventory");
        assertTrue(initialInventory.contains("axe"), "Inventory should contain axe");
        assertTrue(initialInventory.contains("shield"), "Inventory should contain shield");
        assertTrue(initialInventory.contains("helmet"), "Inventory should contain helmet");
        
        // Go to the cave and fight the troll until death
        gameController.handleCommand("player: goto forest");
        gameController.handleCommand("player: goto cave");
        
        // Fight three times to reduce health to zero
        gameController.handleCommand("player: fight troll"); // Health: 2
        gameController.handleCommand("player: fight troll"); // Health: 1
        String deathResult = gameController.handleCommand("player: fight troll"); // Health: 0, death
        
        // Verify death message
        assertTrue(deathResult.contains("died") || deathResult.contains("resurrected"), 
            "Death message should indicate player died and was resurrected");
        
        // Check inventory after death - should be empty
        String inventoryAfterDeath = gameController.handleCommand("player: inventory");
        assertTrue(inventoryAfterDeath.contains("nothing") || inventoryAfterDeath.contains("empty"), 
            "Inventory should be empty after death");
        
        // Verify the player is back at the cabin
        String locationAfterDeath = gameController.handleCommand("player: look");
        assertTrue(locationAfterDeath.contains("cabin"), "Player should be at the cabin after death");
        
        // Verify health is reset
        String healthAfterDeath = gameController.handleCommand("player: health");
        assertTrue(healthAfterDeath.contains("3"), "Health should be reset to 3 after death");
    }

    /* Test: Healing with Potion */
    @Test
    void testHealingWithPotion() {
        // Test that potions restore health
        
        // Reduce health by fighting
        gameController.handleCommand("player: goto forest");
        gameController.handleCommand("player: fight pixie");
        
        // Verify health reduction
        String healthAfterFight = gameController.handleCommand("player: health");
        assertTrue(healthAfterFight.contains("2"), "Health should be reduced to 2 after fighting");
        
        // Return to cabin and get potion
        gameController.handleCommand("player: goto cabin");
        gameController.handleCommand("player: get potion");
        
        // Drink the potion
        String drinkResult = gameController.handleCommand("player: drink potion");
        
        // Verify healing message
        assertTrue(drinkResult.contains("better") || drinkResult.contains("health"), 
            "Drinking message should indicate healing");
        
        // Check health after drinking
        String healthAfterDrink = gameController.handleCommand("player: health");
        assertTrue(healthAfterDrink.contains("3"), "Health should be restored to 3 after drinking potion");
    }

    /* Test: Multiple Character Combat */
    @Test
    void testMultipleCharacterCombat() {
        // Test fighting different characters affects health correctly
        
        // First fight with pixie
        gameController.handleCommand("player: goto forest");
        gameController.handleCommand("player: fight pixie");
        
        // Verify health reduction
        String healthAfterPixie = gameController.handleCommand("player: health");
        assertTrue(healthAfterPixie.contains("2"), "Health should be reduced to 2 after fighting pixie");
        
        // Go to cellar and fight elf
        gameController.handleCommand("player: goto cabin");
        gameController.handleCommand("player: goto cellar");
        gameController.handleCommand("player: fight elf");
        
        // Verify further health reduction
        String healthAfterElf = gameController.handleCommand("player: health");
        assertTrue(healthAfterElf.contains("1"), "Health should be reduced to 1 after fighting elf");
    }
} 