package edu.uob;

import edu.uob.entities.Location;
import edu.uob.games.GameWorld;
import edu.uob.parsers.EntityParser;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

import static org.junit.jupiter.api.Assertions.*;

final class EntitiesFileTests {

  // Test to make sure that the basic entities file is readable
  @Test
  void testBasicEntitiesFileIsReadable() {
      try {
          Parser parser = new Parser();
          FileReader reader = new FileReader("config" + File.separator + "basic-entities.dot");
          parser.parse(reader);
          Graph wholeDocument = parser.getGraphs().get(0);
          ArrayList<Graph> sections = wholeDocument.getSubgraphs();

          // The locations will always be in the first subgraph
          ArrayList<Graph> locations = sections.get(0).getSubgraphs();
          Graph firstLocation = locations.get(0);
          Node locationDetails = firstLocation.getNodes(false).get(0);
          // Yes, you do need to get the ID twice !
          String locationName = locationDetails.getId().getId();
          assertEquals("cabin", locationName, "First location should have been 'cabin'");

          // The paths will always be in the second subgraph
          ArrayList<Edge> paths = sections.get(1).getEdges();
          Edge firstPath = paths.get(0);
          Node fromLocation = firstPath.getSource().getNode();
          String fromName = fromLocation.getId().getId();
          Node toLocation = firstPath.getTarget().getNode();
          String toName = toLocation.getId().getId();
          assertEquals("cabin", fromName, "First path should have been from 'cabin'");
          assertEquals("forest", toName, "First path should have been to 'forest'");

      } catch (FileNotFoundException fnfe) {
          fail("FileNotFoundException was thrown when attempting to read basic entities file");
      } catch (ParseException pe) {
          fail("ParseException was thrown when attempting to read basic entities file");
      }
  }

  // MY test for EntityParser
    /// ============check=========== ///
  @Test
  void testEntityParserCreatesCorrectWorld() {
      EntityParser parser = new EntityParser();
      File file = new File("config/basic-entities.dot");
      GameWorld world = parser.parseEntities(file);

      Location cabin = world.getLocation("cabin");
      assertNotNull(cabin);
      assertEquals("A log cabin in the woods", cabin.getDescription());

      assertTrue(cabin.getPaths().contains("forest"));
      assertEquals(2, cabin.getArtefacts().size());
  }

    @Test
    void testSimpleLook() {
        File entitiesFile = new File("config/basic-entities.dot");
        File actionsFile = new File("config/basic-actions.xml");
        GameServer server = new GameServer(entitiesFile, actionsFile);
        String response = server.handleCommand("simon: look");
        assertTrue(response.toLowerCase().contains("cabin"));
    }
}
