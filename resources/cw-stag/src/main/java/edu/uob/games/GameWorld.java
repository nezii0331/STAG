package edu.uob.games;

import edu.uob.entities.Location;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage all location and entity data
 */

public class GameWorld {
    private Map<String, Location> locations;

    public GameWorld(){
        this.locations = new HashMap<>();
    }

    public void addLocation(Location location){
        this.locations.put(location.getName(), location);
    }

    public Location getLocation(String name){
        return this.locations.get(name);
    }
}
