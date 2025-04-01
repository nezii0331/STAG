package edu.uob;

import edu.uob.actions.CustomAction;
import edu.uob.entities.Location;
import edu.uob.games.GameWorld;
import edu.uob.parsers.ActionParser;
import edu.uob.parsers.EntityParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GameParserTest {
    private static EntityParser entityParser;
    private static ActionParser actionParser;
    private static File entitiesFile;
    private static File actionsFile;
    private static GameWorld gameWorld;
    private static Set<CustomAction> customActions;

    @BeforeAll
    public static void setUp() {
        // Initialize parsers
        entityParser = new EntityParser();
        actionParser = new ActionParser();

        // Set up files
        entitiesFile = new File("config/basic-entities.dot");
        actionsFile = new File("config/basic-actions.xml");

        // Check if files exist
        if (!entitiesFile.exists()) {
            throw new RuntimeException("Entities file not found at: " + entitiesFile.getAbsolutePath());
        }
        if (!actionsFile.exists()) {
            throw new RuntimeException("Actions file not found at: " + actionsFile.getAbsolutePath());
        }

        // Parse entities and actions
        gameWorld = entityParser.parseEntities(entitiesFile);
        if (gameWorld == null) {
            throw new RuntimeException("Failed to parse entities file");
        }

        customActions = actionParser.parseAction(actionsFile);
        if (customActions == null) {
            throw new RuntimeException("Failed to parse actions file");
        }
    }



    // Entity Parsing Tests
    @Test
    public void testEntityParsing_Locations() {
        assertNotNull(gameWorld, "GameWorld should not be null");

        // Verify specific locations
        Location cabin = gameWorld.getLocation("cabin");
        Location forest = gameWorld.getLocation("forest");
        Location cellar = gameWorld.getLocation("cellar");

        assertNotNull(cabin, "Cabin location should exist");
        assertNotNull(forest, "Forest location should exist");
        assertNotNull(cellar, "Cellar location should exist");

        // Check location descriptions
        assertEquals("A log cabin in the woods", cabin.getDescription(), "Cabin description should match");
        assertEquals("A dark forest", forest.getDescription(), "Forest description should match");
        assertEquals("A dusty cellar", cellar.getDescription(), "Cellar description should match");
    }

    @Test
    public void testEntityParsing_Paths() {
        Location cabin = gameWorld.getLocation("cabin");
        Location forest = gameWorld.getLocation("forest");
        Location cellar = gameWorld.getLocation("cellar");

        // Verify paths between locations
        assertTrue(cabin.getPaths().contains("forest"), "Cabin should have a path to forest");
        assertTrue(forest.getPaths().contains("cabin"), "Forest should have a path to cabin");
        assertTrue(cellar.getPaths().contains("cabin"), "Cellar should have a path to cabin");
    }

    @Test
    public void testEntityParsing_Artefacts() {
        Location cabin = gameWorld.getLocation("cabin");
        Location forest = gameWorld.getLocation("forest");

        // Check artefacts in cabin
        assertTrue(cabin.getArtefacts().stream().anyMatch(a -> a.getName().equals("axe")),
                "Cabin should contain axe");
        assertTrue(cabin.getArtefacts().stream().anyMatch(a -> a.getName().equals("potion")),
                "Cabin should contain potion");

        // Check artefacts in forest
        assertTrue(forest.getArtefacts().stream().anyMatch(a -> a.getName().equals("key")),
                "Forest should contain key");
    }

    @Test
    public void testEntityParsing_Furniture() {
        Location cabin = gameWorld.getLocation("cabin");
        Location forest = gameWorld.getLocation("forest");

        // Check furniture in cabin
        assertTrue(cabin.getFurniture().stream().anyMatch(f -> f.getName().equals("trapdoor")),
                "Cabin should contain trapdoor");

        // Check furniture in forest
        assertTrue(forest.getFurniture().stream().anyMatch(f -> f.getName().equals("tree")),
                "Forest should contain tree");
    }

    @Test
    public void testEntityParsing_Characters() {
        Location cellar = gameWorld.getLocation("cellar");

        // Check characters in cellar
        assertTrue(cellar.getCharacters().stream().anyMatch(c -> c.getName().equals("elf")),
                "Cellar should contain elf");
    }

    // Action Parsing Tests
    @Test
    public void testActionParsing_BasicValidation() {
        assertNotNull(customActions, "Actions set should not be null");
        assertFalse(customActions.isEmpty(), "Actions set should not be empty");
        assertEquals(2, customActions.size(), "Should parse 2 actions");
    }

    @Test
    public void testActionParsing_TrapdoorAction() {
        CustomAction trapdoorAction = customActions.stream()
                .filter(action -> action.getTriggers().contains("open"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Trapdoor action not found"));

        // Verify trapdoor action details
        assertTrue(trapdoorAction.getTriggers().contains("open"), "Action should have 'open' trigger");
        assertTrue(trapdoorAction.getTriggers().contains("unlock"), "Action should have 'unlock' trigger");

        assertTrue(trapdoorAction.getSubjects().contains("trapdoor"), "Action should have 'trapdoor' subject");
        assertTrue(trapdoorAction.getSubjects().contains("key"), "Action should have 'key' subject");

        assertEquals(1, trapdoorAction.getConsumed().size(), "Should consume 1 entity");
        assertTrue(trapdoorAction.getConsumed().contains("key"), "Should consume 'key'");

        assertEquals(1, trapdoorAction.getProduced().size(), "Should produce 1 entity");
        assertTrue(trapdoorAction.getProduced().contains("cellar"), "Should produce 'cellar'");

        assertEquals("You unlock the trapdoor and see steps leading down into a cellar",
                trapdoorAction.getNarration(), "Narration should match");
    }

    @Test
    public void testActionParsing_TreeAction() {
        CustomAction treeAction = customActions.stream()
                .filter(action -> action.getTriggers().contains("chop"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Tree action not found"));

        // Verify tree action details
        assertTrue(treeAction.getTriggers().contains("chop"), "Action should have 'chop' trigger");
        assertTrue(treeAction.getTriggers().contains("cut"), "Action should have 'cut' trigger");
        assertTrue(treeAction.getTriggers().contains("cutdown"), "Action should have 'cutdown' trigger");

        assertTrue(treeAction.getSubjects().contains("tree"), "Action should have 'tree' subject");
        assertTrue(treeAction.getSubjects().contains("axe"), "Action should have 'axe' subject");

        assertEquals(1, treeAction.getConsumed().size(), "Should consume 1 entity");
        assertTrue(treeAction.getConsumed().contains("tree"), "Should consume 'tree'");

        assertEquals(1, treeAction.getProduced().size(), "Should produce 1 entity");
        assertTrue(treeAction.getProduced().contains("log"), "Should produce 'log'");

        assertEquals("You cut down the tree with the axe",
                treeAction.getNarration(), "Narration should match");
    }

    // Error Handling Tests
    @Test
    public void testEntityParsing_InvalidFile() {
        File invalidFile = new File("non_existent_file.dot");
        GameWorld invalidWorld = entityParser.parseEntities(invalidFile);

        assertNull(invalidWorld, "GameWorld should be null for invalid file");
    }

    @Test
    public void testActionParsing_InvalidFile() {
        File invalidFile = new File("non_existent_file.xml");
        Set<CustomAction> invalidActions = actionParser.parseAction(invalidFile);

        assertNotNull(invalidActions, "Actions set should not be null");
        assertTrue(invalidActions.isEmpty(), "Actions set should be empty for invalid file");
    }
}