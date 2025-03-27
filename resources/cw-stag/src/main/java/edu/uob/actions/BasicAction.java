package edu.uob.actions;

import edu.uob.entities.Artefact;
import edu.uob.entities.GameEntity;
import edu.uob.entities.Location;
import edu.uob.games.GameState;
import edu.uob.games.GameWorld;
import edu.uob.games.PlayerState;

import java.util.Set;

public class BasicAction {


    public static String handleLook(PlayerState player, GameWorld gameworld, GameState state){

        // player current Location
        Location currentLocation = player.getLocation();
        // return to player abut Location description
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("You are at %s now.%n", currentLocation.getName()));
        sb.append(String.format("%s%n", currentLocation.getDescription()));

        // return to player current Artefacts、Furniture、Characters
        for (GameEntity entity: currentLocation.getEntities()){
            sb.append(String.format("There is a %s here - %s%n", entity.getName(), entity.getDescription()));
        }

        // TODO: other player who's here
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
               sb.append("You are carring with .");
               for(GameEntity item: items){
                   sb.append(item.getName()).append(",");
            }
               int length = sb.length();
               sb.delete(length - 2, length);//in order to delete space and ,
        }
        return sb.toString();
    }

    public static String handleGet(PlayerState player, String itemName){
        // player current Location
        Location currentLocation = player.getLocation();

       // player pick up
        // for each item it came form this location's artefacts.
        for(Artefact item : currentLocation.getArtefacts()){
            if(item.getName().equalsIgnoreCase(itemName)){
                //add to bag
                player.addToInventory(item);
                //remove from location
                currentLocation.getArtefacts().remove(item);
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
}
