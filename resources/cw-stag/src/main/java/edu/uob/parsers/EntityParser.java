package edu.uob.parsers;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import edu.uob.entities.Artefact;
import edu.uob.entities.Furniture;
import edu.uob.entities.GameCharacter;
import edu.uob.games.GameWorld;
import com.alexmerz.graphviz.objects.Edge;
import edu.uob.entities.Location;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Parse .dot to create entities
 */
public class EntityParser {
    public GameWorld parseEntities(File entitiesFile){
        try {
            if (!entitiesFile.exists()) {
                System.err.println("File not found: " + entitiesFile.getAbsolutePath());
                return null;
            }

            GameWorld world = new GameWorld();
            // Build parser
            Parser parser = new Parser();
            
            // Parse .dot files using JPGD API
            FileReader reader = new FileReader(entitiesFile);
            parser.parse(reader);
            reader.close();

            // Get the root from files
            Graph root = parser.getGraphs().get(0);

            //get subgraph
            Graph locationSection = root.getSubgraphs().get(0);
            for (Graph location : locationSection.getSubgraphs()) {
                Node locationNode = location.getNodes(false).get(0);
                String name = locationNode.getId().getId();
                String description = locationNode.getAttribute("description");

                Location loc = new Location(name, description);
                world.addLocation(loc);

                System.out.printf("Location: %s\n", name);
                System.out.printf("Description: %s\n", description);

                for (Graph subgraph : location.getSubgraphs()){
                    if (subgraph.getId().getId().equals("artefacts")){
                        for(Node artefact : subgraph.getNodes(false)){
                            String artefactName = artefact.getId().getId();
                            String artefactDescription = artefact.getAttribute("description");

                            Artefact artefacts = new Artefact(artefactName, artefactDescription);
                            loc.addArtefact(artefacts);
                            System.out.printf("artefacts: %s\n", artefactName);
                            System.out.printf("Description: %s\n", artefactDescription);
                        }
                    }
                }

                for (Graph subgraph : location.getSubgraphs()){
                    if (subgraph.getId().getId().equals("furniture")){
                        for(Node furniture : subgraph.getNodes(false)){
                            String furnitureName = furniture.getId().getId();
                            String furnitureDescription = furniture.getAttribute("description");

                            Furniture furnObj = new Furniture(furnitureName, furnitureDescription);
                            loc.addFurniture(furnObj);
                            System.out.printf("furniture: %s\n", furnitureName);
                            System.out.printf("Description: %s\n", furnitureDescription);
                        }
                    }
                }
                for (Graph subgraph : location.getSubgraphs()){
                    if (subgraph.getId().getId().equals("characters")){
                        for(Node character : subgraph.getNodes(false)){
                            String characterName = character.getId().getId();
                            String characterDescription = character.getAttribute("description");

                            GameCharacter characterObj = new GameCharacter(characterName, characterDescription);
                            loc.addCharacter(characterObj);
                            System.out.printf("character: %s\n", characterName);
                            System.out.printf("Description: %s\n", characterDescription);
                        }
                    }
                }
            }
            Graph pathSection = root.getSubgraphs().get(1);

            for (Edge edge : pathSection.getEdges()) {
                String from = edge.getSource().getNode().getId().getId();
                String to = edge.getTarget().getNode().getId().getId();

                System.out.printf("Path: %s → %s\n", from, to);

                Location fromLocation = world.getLocation(from);
                if (fromLocation != null) {
                    fromLocation.addPath(to);
                }
            }
            return world;
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + entitiesFile.getAbsolutePath());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error while parsing entities: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

