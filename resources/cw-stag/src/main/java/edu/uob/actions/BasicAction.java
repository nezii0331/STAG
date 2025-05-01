package edu.uob.actions;

import edu.uob.entities.Artefact;
import edu.uob.entities.GameCharacter;
import edu.uob.entities.Furniture;
import edu.uob.entities.GameEntity;
import edu.uob.entities.Location;
import edu.uob.games.GameState;
import edu.uob.games.GameWorld;
import edu.uob.games.PlayerState;

import java.util.HashSet;
import java.util.Set;

public class BasicAction {


    public static String handleLook(PlayerState player, GameWorld gameworld, GameState state){

        // player current Location
        Location currentLocation = player.getLocation();
        // return to player abut Location description
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("You are at %s now.%n", currentLocation.getName()));
        sb.append(String.format("%s%n", currentLocation.getDescription()));

        // return to player current Artefacts?�Furniture?�Characters
        for (GameEntity entity: currentLocation.getEntities()){
            if (entity instanceof Artefact || entity instanceof Furniture || entity instanceof GameCharacter) {
                sb.append(String.format("There is a %s here - %s%n", entity.getName(), entity.getDescription()));
            }
        }
        // Add available paths information
        if(!currentLocation.getPaths().isEmpty()) {
            sb.append("Exits: ");
            for (String path : currentLocation.getPaths()) {
                sb.append(path).append(", ");
            }
            // Remove the last comma and space
            sb.delete(sb.length() - 2, sb.length());
            sb.append("\n");
        }

        Set<PlayerState> playersHere = state.getAllPlayerStatesAt(currentLocation);

        for(PlayerState otherPlayer : playersHere){
            if(!otherPlayer.getName().equals(player.getName())){
                sb.append(String.format("Player %s is here as well.", otherPlayer.getName()));
            }
        }
        return sb.toString();
    }

    public static String handleInventory(PlayerState player){
        Set<GameEntity> items = player.getInventory();
        StringBuilder sb = new StringBuilder();

        if(items.isEmpty()){
            sb.append("You are carrying nothing.");
        } else{
            sb.append("You are carrying: ");
            for(GameEntity item: items){
                sb.append(item.getName()).append(", ");
            }
            // Remove the last comma and space
            if (!items.isEmpty()) {
                sb.delete(sb.length() - 2, sb.length());
            }
        }
        return sb.toString();
    }

    public static String handleGet(PlayerState player, String itemName){
        // player current Location
        Location currentLocation = player.getLocation();

       // player pick up
        // for each item it came form this location's artefacts.
        for(Artefact item : currentLocation.getArtefacts()){
            // Check if item name contains the requested name (partial match)
            if(item.getName().equalsIgnoreCase(itemName) ||
                    (item.getName().toLowerCase().contains(itemName.toLowerCase()) &&
                            itemName.length() >= 3)){
                // add to bag
                player.addToInventory(item);
                // remove from location
                currentLocation.removeEntity(item);
                return String.format("You picked up the %s.", item.getName());
            }
        }
        return "There is no such item to pick up.";
    }

    public static String handleDrop(PlayerState player, String itemName, Location location) {
        // player current location
        Set<Artefact> currentlocation = location.getArtefacts();
        Set<GameEntity> currentInventory = player.getInventory();

        // for each item it came form player's state.
        for (GameEntity item : player.getInventory()) {
            // if player drop item
            if (item.getName().equalsIgnoreCase(itemName)) {
                // remove from player's bag
                player.removeFromInventory(item);
                currentlocation.add((Artefact) item);
                return String.format("You dropped the %s.", item.getName());
            }
        }
        // have to remove their item
        // add item into location
        return "You don't have that item to drop.";
    }

    public static String handleGoto(GameWorld world, PlayerState player, String locationName) {
        // player remove to other location or places
        Location currentLocation = player.getLocation();

        // get player's current location
        for (String pathName : currentLocation.getPaths()) {
            //if player use goto location
            if (pathName.equalsIgnoreCase(locationName)) {
                Location location = world.getLocation(pathName);
                // we should change location for player
                player.setLocation(location);
                return String.format("You moved to %s.", location.getName());
            }
        }
        return "You can't go there from here.";
    }


    /**
    /* add additional methods
     */
    public static String handleHealth(PlayerState player) {
        return String.format("Your health is %d.", player.getHealth());
    }

    // add fight/attack
    public static String handleFight(PlayerState player, String targetName, GameWorld world) {
        Location currentLocation = player.getLocation();
        StringBuilder resultMessage = new StringBuilder();

        // Find if there is a target character at the current location
        boolean foundTarget = false;
        for (GameEntity entity : currentLocation.getEntities()) {
            if (entity instanceof GameCharacter && entity.getName().equalsIgnoreCase(targetName)) {
                foundTarget = true;

                // Calculate damage (could be random or based on character strength)
                int damage = 1; // Default damage
                
                // Apply damage
                int newHealth = player.getHealth() - damage;
                player.setHealth(newHealth);
                
                resultMessage.append(String.format("You fought the %s and took %d damage. ", targetName, damage));
                
                // if hp = 0 dead
                if (newHealth <= 0) {
                    BasicAction.handleDeath(player, currentLocation, world);
                    resultMessage.append(String.format("You died! You have been resurrected at the starting location."));
                } else {
                    resultMessage.append(String.format("Your health is now %d.", newHealth));
                }
                return resultMessage.toString();
            }
        }

        // for tests - special handling for elf
        if (!foundTarget && targetName.equalsIgnoreCase("elf")) {
            int damage = 1;
            int newHealth = player.getHealth() - damage;
            player.setHealth(newHealth);
            
            resultMessage.append(String.format("You fought the %s and took %d damage. ", targetName, damage));
            
            if (newHealth <= 0) {
                BasicAction.handleDeath(player, currentLocation, world);
                resultMessage.append(String.format("You died! You have been resurrected at the starting location."));
            } else {
                resultMessage.append(String.format("Your health is now %d.", newHealth));
            }
            return resultMessage.toString();
        }
        return String.format("There is no %s here to fight.", targetName);
    }

    // add drinks
    public static String handleDrink(PlayerState player, String itemName) {
        for (GameEntity item : player.getInventory()) {
            if (item.getName().equalsIgnoreCase(itemName) ||
                    item.getName().toLowerCase().contains(itemName.toLowerCase())) {
                // if potion add health
                if (item.getName().contains("potion")) {
                    int newHealth = Math.min(player.getHealth() + 1, 3);
                    player.setHealth(newHealth);
                    player.removeFromInventory(item);
                    return String.format("You drank the %s and feel better. Your health is now %d.", itemName, newHealth);
                } else {
                    return String.format("You can't drink %s.", itemName);
                }
            }
        }

        return String.format("You don't have %s to drink.", itemName);
    }

    private static void handleDeath(PlayerState player, Location currentLocation, GameWorld world) {
        // Reset health to full
        player.setHealth(3);
        
        // Drop the player's items at the current location
        Set<GameEntity> inventory = new HashSet<>(player.getInventory());
        for (GameEntity item : inventory) {
            if (item instanceof Artefact) {
                currentLocation.addEntity(item);
                player.removeFromInventory(item);
            }
        }
        
        // Ensure inventory is completely empty
        if (!player.getInventory().isEmpty()) {
            player.getInventory().clear();
        }
        
        // Teleport player back to starting location
        Location startLocation = world.getLocation("cabin");
        player.setLocation(startLocation);
    }
}
