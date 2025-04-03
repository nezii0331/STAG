package edu.uob;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.uob.entities.Furniture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.uob.actions.CustomAction;
import edu.uob.actions.CustomActionExecutor;
import edu.uob.entities.Artefact;
import edu.uob.entities.GameEntity;
import edu.uob.entities.Location;
import edu.uob.games.GameState;
import edu.uob.games.GameWorld;
import edu.uob.games.PlayerState;

public class CustomActionExecutorTest {

    private GameWorld gameWorld;
    private GameState gameState;
    private PlayerState player;
    private Location cabin;
    private Location forest;
    private Artefact axe;
    private GameEntity tree;
    private Artefact log;
    private CustomAction chopAction;

    @BeforeEach
    void setup() {
        // Create game world and state
        gameWorld = new GameWorld();
        gameState = new GameState();

        // Create locations
        cabin = new Location("cabin", "A cozy wooden cabin");
        forest = new Location("forest", "A dense forest with tall trees");

        // Create entities
        axe = new Artefact("axe", "A sharp woodcutter's axe");
        tree = new Furniture("tree", "A tall oak tree");
        log = new Artefact("log", "A wooden log from a tree");

        // Add entities to locations
        cabin.addEntity(axe);
        forest.addEntity(tree);

        // Add locations to game world
        gameWorld.addLocation(cabin);
        gameWorld.addLocation(forest);

        // Create player and set initial state
        player = new PlayerState("testPlayer", cabin);
        player.setLocation(cabin);
        gameState.addPlayer(player);

        // Create custom action
        Set<String> triggers = new HashSet<>(Arrays.asList("chop", "cut", "cut down"));
        Set<String> subjects = new HashSet<>(Arrays.asList("axe", "tree"));
        Set<String> consumed = new HashSet<>(Arrays.asList("tree"));
        Set<String> produced = new HashSet<>(Arrays.asList("log"));
        String narration = "You chopped down the tree with the axe and got a log!";

        chopAction = new CustomAction();
        for (String trigger : triggers) {
            chopAction.addTriggers(trigger);
        }
        for (String subject : subjects) {
            chopAction.addSubjects(subject);
        }
        for (String item : consumed) {
            chopAction.addConsumed(item);
        }
        for (String item : produced) {
            chopAction.addProduced(item);
        }
        chopAction.addNarration(narration);
        gameWorld.addAction(chopAction);
    }

    @Test
    void testCannotExecuteWithoutAllSubjects() {
        CustomActionExecutor executor = new CustomActionExecutor();
        List<String> command = new LinkedList<>(Arrays.asList("chop", "tree"));

        // Player doesn't have the axe yet
        String result = CustomActionExecutor.executeCustomAction(gameWorld, gameState, player, String.join(" ", command));
        assertFalse(result.toLowerCase().contains("chopped"));
        assertTrue(result.toLowerCase().contains("missing"));

        // Tree should still exist
        assertTrue(forest.hasEntity("tree"));
        assertFalse(forest.hasEntity("log"));
    }

    @Test
    void testSuccessfulExecution() {
        CustomActionExecutor executor = new CustomActionExecutor();

        // Player picks up the axe
        player.addToInventory(axe);
        cabin.removeEntity(axe);

        // Player moves to forest
        player.setLocation(forest);

        List<String> command = new LinkedList<>(Arrays.asList("chop", "tree", "with", "axe"));

        String result = CustomActionExecutor.executeCustomAction(gameWorld, gameState, player, String.join(" ", command));
        assertTrue(result.toLowerCase().contains("chopped") || result.equals(chopAction.getNarration()));

        // Tree should be consumed
        assertFalse(forest.hasEntity("tree"));

        // Log should be produced
        assertTrue(forest.hasEntity("log"));
    }

    @Test
    void testAlternativeTriggerWords() {
        CustomActionExecutor executor = new CustomActionExecutor();

        // Player picks up the axe
        player.addToInventory(axe);
        cabin.removeEntity(axe);

        // Player moves to forest
        player.setLocation(forest);

        List<String> command = new LinkedList<>(Arrays.asList("cut", "tree", "with", "axe"));

        String result = CustomActionExecutor.executeCustomAction(gameWorld, gameState, player, String.join(" ", command));
        assertTrue(result.toLowerCase().contains("chopped") || result.equals(chopAction.getNarration()));
        assertFalse(forest.hasEntity("tree"));

        // Log should be produced
        assertTrue(forest.hasEntity("log"));
    }

    @Test
    void testDifferentWordOrder() {
        CustomActionExecutor executor = new CustomActionExecutor();

        // Player picks up the axe
        player.addToInventory(axe);
        cabin.removeEntity(axe);

        // Player moves to forest
        player.setLocation(forest);

        // Different word order
        List<String> command = new LinkedList<>(Arrays.asList("use", "axe", "to", "chop", "tree"));

        String result = CustomActionExecutor.executeCustomAction(gameWorld, gameState, player, String.join(" ", command));
        assertTrue(result.toLowerCase().contains("chopped") || result.equals(chopAction.getNarration()));

        // Tree should be consumed
        assertFalse(forest.hasEntity("tree"));

        // Log should be produced
        assertTrue(forest.hasEntity("log"));
    }

    @Test
    void testPartialCommand() {
        CustomActionExecutor executor = new CustomActionExecutor();

        // Player picks up the axe (the only tool they have)
        player.addToInventory(axe);
        cabin.removeEntity(axe);

        // Player moves to forest
        player.setLocation(forest);

        // Partial command
        List<String> command = new LinkedList<>(Arrays.asList("chop", "tree"));

        String result = CustomActionExecutor.executeCustomAction(gameWorld, gameState, player, String.join(" ", command));
        assertTrue(result.toLowerCase().contains("chopped") || result.equals(chopAction.getNarration()));

        // Tree should be consumed
        assertFalse(forest.hasEntity("tree"));

        // Log should be produced
        assertTrue(forest.hasEntity("log"));
    }

    @Test
    void testWithExtraneousEntities() {
        CustomActionExecutor executor = new CustomActionExecutor();

        // Create another item
        Artefact torch = new Artefact("torch", "A flaming torch");
        player.addToInventory(torch);

        // Player picks up the axe
        player.addToInventory(axe);
        cabin.removeEntity(axe);
        player.setLocation(forest);

        // Command with extra entities not needed for the action
        List<String> command = new LinkedList<>(Arrays.asList("chop", "tree", "with", "axe", "and", "torch"));

        String result = CustomActionExecutor.executeCustomAction(gameWorld, gameState, player, String.join(" ", command));
        assertFalse(result.toLowerCase().contains("chopped"));
        assertTrue(result.toLowerCase().contains("cannot") || result.toLowerCase().contains("invalid"));
        // Tree should still exist
        assertTrue(forest.hasEntity("tree"));
        assertFalse(forest.hasEntity("log"));
    }

    @Test
    void testAmbiguousCommand() {
        CustomActionExecutor executor = new CustomActionExecutor();

        // Create another tree
        Furniture pineTree = new Furniture("tree", "A tall pine tree");
        forest.addEntity(pineTree);

        // Player picks up the axe
        player.addToInventory(axe);
        cabin.removeEntity(axe);

        // Player moves to forest
        player.setLocation(forest);

        // which tree?
        List<String> command = new LinkedList<>(Arrays.asList("chop", "tree", "with", "axe"));

        String result = CustomActionExecutor.executeCustomAction(gameWorld, gameState, player, String.join(" ", command));
        assertTrue(result.toLowerCase().contains("ambiguous") || result.toLowerCase().contains("which") || result.toLowerCase().contains("multiple"));
        assertFalse(result.toLowerCase().contains("chopped"));

        // No tree should be consumed
        assertTrue(forest.hasEntity("tree"));
        assertFalse(forest.hasEntity("log"));
    }
}
