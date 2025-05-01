package edu.uob.games;

import edu.uob.actions.GameAction;
import edu.uob.entities.GameEntity;
import edu.uob.entities.Location;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manage all location and entity data
 */

public class GameWorld {
    private Map<String, Location> locations;
    private Set<GameAction> actions;

    public GameWorld(){
        this.locations = new HashMap<>();
        this.actions = new HashSet<>();
    }

    public void addLocation(Location location){
        this.locations.put(location.getName(), location);
    }

    public Location getLocation(String name){
        return this.locations.get(name);
    }

    /**
     * Returns all locations in the game world
     * @return Set of all Location objects
     */
    public Set<Location> getLocations(){
        return new HashSet<>(this.locations.values());
    }

    public void addAction(GameAction action){
        this.actions.add(action);
    }

    public Set<GameAction> getAllActions(){
        return this.actions;
    }

    //implement found their name in this world
    public GameEntity findEntityByName(String name){
        for(Location location: locations.values()){
            if (location.getName().equals("storeroom")) {
                continue;
            }
            for(GameEntity entity: location.getEntities()){
                if(entity.getName().equalsIgnoreCase(name)){
                    //if found it return this item to used
                    return entity;
                }
            }
        }
        Location storeroom = this.getLocation("storeroom");
        if (storeroom != null) {
            for(GameEntity entity: storeroom.getEntities()){
                if(entity.getName().equalsIgnoreCase(name)){
                    return entity;
                }
            }
        }
        return null;
    }
}
