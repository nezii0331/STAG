package edu.uob;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import edu.uob.actions.CustomAction;
import edu.uob.actions.GameAction;
import edu.uob.parsers.ActionParser;


public class ParserTest {

    @TempDir
    Path tempDir;
    
    private File entitiesFile;
    private File actionsFile;
    
    @BeforeEach
    void setup() throws IOException {
        // Create temporary entities.dot file
        entitiesFile = tempDir.resolve("entities.dot").toFile();
        String entitiesContent = 
            "digraph game_graph {\n" +
            "  // Locations\n" +
            "  cabin [label=\"cabin\\nA cozy log cabin\"];\n" +
            "  forest [label=\"forest\\nA dense, dark forest\"];\n" +
            "  cave [label=\"cave\\nA mysterious cave\"];\n" +
            "  \n" +
            "  // Paths\n" +
            "  cabin -> forest;\n" +
            "  forest -> cabin;\n" +
            "  forest -> cave;\n" +
            "  cave -> forest;\n" +
            "  \n" +
            "  // Artefacts\n" +
            "  axe [label=\"axe\\nA sharp axe\", shape=polygon, sides=4];\n" +
            "  key [label=\"key\\nA rusty key\", shape=polygon, sides=4];\n" +
            "  \n" +
            "  // Location of artefacts\n" +
            "  cabin -> axe;\n" +
            "  forest -> key;\n" +
            "  \n" +
            "  // Furniture\n" +
            "  tree [label=\"tree\\nA tall oak tree\", shape=polygon, sides=6];\n" +
            "  door [label=\"door\\nA wooden door\", shape=polygon, sides=6];\n" +
            "  \n" +
            "  // Location of furniture\n" +
            "  forest -> tree;\n" +
            "  cave -> door;\n" +
            "  \n" +
            "  // Characters\n" +
            "  elf [label=\"elf\\nA forest elf\", shape=ellipse];\n" +
            "  \n" +
            "  // Location of characters\n" +
            "  forest -> elf;\n" +
            "}";
        Files.writeString(entitiesFile.toPath(), entitiesContent);
        
        // Create temporary actions.xml file
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

    @Test
    void testActionParser() {
        ActionParser parser = new ActionParser();
        // checkssss
        Set<GameAction> actions =  new HashSet<>(parser.parseAction(actionsFile));
        
        // We should have two actions
        assertEquals(2, actions.size());
        
        // Test chop action
        boolean hasChopAction = false;
        for (GameAction action : actions) {
            if (action instanceof CustomAction) {
                CustomAction customAction = (CustomAction) action;
                List<String> triggers = customAction.getTriggers();
                if (triggers.contains("chop")) {
                    hasChopAction = true;
                    
                    // Test subjects
                    List<String> subjects = customAction.getSubjects();
                    assertTrue(subjects.contains("axe"));
                    assertTrue(subjects.contains("tree"));
                    
                    // Test consumed
                    List<String> consumed = customAction.getConsumed();
                    assertTrue(consumed.contains("tree"));
                    List<String> produced = customAction.getProduced();
                    assertTrue(produced.contains("log"));
                    assertEquals("You chopped down the tree with your axe!", customAction.getNarration());
                    break;
                }
            }
        }
        assertTrue(hasChopAction, "Should have a chop action");
        
        // Test unlock action
        boolean hasUnlockAction = false;
        for (GameAction action : actions) {
            if (action instanceof CustomAction) {
                CustomAction customAction = (CustomAction) action;
                List<String> triggers = customAction.getTriggers();
                if (triggers.contains("unlock")) {
                    hasUnlockAction = true;
                    
                    // Test subjects
                    List<String> subjects = customAction.getSubjects();
                    assertTrue(subjects.contains("key"));
                    assertTrue(subjects.contains("door"));
                    assertEquals("You unlocked the door with the key!", customAction.getNarration());
                    break;
                }
            }
        }
        assertTrue(hasUnlockAction, "Should have an unlock action");
    }
}
