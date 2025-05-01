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
    /**
     * Parse entities from a DOT file and create a game world
     */
    public GameWorld parseEntities(File entitiesFile) {
        try {
            if (!entitiesFile.exists()) {
                return null;
            }

            GameWorld gameWorld = new GameWorld();
            Graph rootGraph = parseGraphFromFile(entitiesFile);
            if (rootGraph == null) {
                return null;
            }

            // Process locations
            processLocations(rootGraph, gameWorld);
            
            // Process paths between locations
            processPaths(rootGraph, gameWorld);
            
            return gameWorld;
        } catch (FileNotFoundException exception) {
            logError("File not found: " + entitiesFile.getAbsolutePath(), exception);
            return null;
        } catch (Exception exception) {
            logError("Unexpected error while parsing entities: " + exception.getMessage(), exception);
            return null;
        }
    }
    
    /**
     * Parse the DOT file and return the root graph
     */
    private Graph parseGraphFromFile(File dotFile) throws Exception {
        Parser parser = new Parser();
        FileReader fileReader = new FileReader(dotFile);
        parser.parse(fileReader);
        fileReader.close();
        return parser.getGraphs().get(0);
    }
    
    /**
     * Process the locations section of the graph
     */
    private void processLocations(Graph rootGraph, GameWorld gameWorld) {
        Graph locationSection = rootGraph.getSubgraphs().get(0);
        
        for (Graph locationGraph : locationSection.getSubgraphs()) {
            Node locationNode = locationGraph.getNodes(false).get(0);
            String locationName = locationNode.getId().getId();
            String locationDescription = locationNode.getAttribute("description");

            Location location = new Location(locationName, locationDescription);
            gameWorld.addLocation(location);

            // Process entities within this location
            processLocationEntities(locationGraph, location);
        }
    }
    
    /**
     * Process entities (artefacts, furniture, characters) within a location
     */
    private void processLocationEntities(Graph locationGraph, Location location) {
        for (Graph subgraph : locationGraph.getSubgraphs()) {
            String subgraphType = subgraph.getId().getId();
            
            switch (subgraphType) {
                case "artefacts":
                    processArtefacts(subgraph, location);
                    break;
                case "furniture":
                    processFurniture(subgraph, location);
                    break;
                case "characters":
                    processCharacters(subgraph, location);
                    break;
            }
        }
    }
    
    /**
     * Process artefacts in a location
     */
    private void processArtefacts(Graph artefactsGraph, Location location) {
        for (Node artefactNode : artefactsGraph.getNodes(false)) {
            String artefactName = artefactNode.getId().getId();
            String artefactDescription = artefactNode.getAttribute("description");
            
            Artefact artefact = new Artefact(artefactName, artefactDescription);
            location.addArtefact(artefact);
        }
    }
    
    /**
     * Process furniture in a location
     */
    private void processFurniture(Graph furnitureGraph, Location location) {
        for (Node furnitureNode : furnitureGraph.getNodes(false)) {
            String furnitureName = furnitureNode.getId().getId();
            String furnitureDescription = furnitureNode.getAttribute("description");
            
            Furniture furniture = new Furniture(furnitureName, furnitureDescription);
            location.addFurniture(furniture);
        }
    }
    
    /**
     * Process characters in a location
     */
    private void processCharacters(Graph charactersGraph, Location location) {
        for (Node characterNode : charactersGraph.getNodes(false)) {
            String characterName = characterNode.getId().getId();
            String characterDescription = characterNode.getAttribute("description");
            
            GameCharacter character = new GameCharacter(characterName, characterDescription);
            location.addCharacter(character);
        }
    }
    
    /**
     * Process paths between locations
     */
    private void processPaths(Graph rootGraph, GameWorld gameWorld) {
        Graph pathSection = rootGraph.getSubgraphs().get(1);
        
        for (Edge edge : pathSection.getEdges()) {
            String sourceLocationName = edge.getSource().getNode().getId().getId();
            String targetLocationName = edge.getTarget().getNode().getId().getId();
            
            Location sourceLocation = gameWorld.getLocation(sourceLocationName);
            if (sourceLocation != null) {
                sourceLocation.addPath(targetLocationName);
            }
        }
    }
    
    /**
     * Log error messages
     */
    private void logError(String message, Exception exception) {
        System.err.println(message);
        exception.printStackTrace();
    }
}

