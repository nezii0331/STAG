package edu.uob.games;

import edu.uob.entities.GameEntity;
import edu.uob.entities.Location;
import java.util.LinkedHashSet;
import java.util.Set;

public class PlayerState {
    // player name
    private String playerName;

    // player location
    private Location location;

    // player with items
    private Set<GameEntity> inventory;

    // initiate
    private int health = 3;

    // create initial
    public PlayerState(String playerName, Location location) {
        this.playerName = playerName;
        this.location = location;
        this.inventory = new LinkedHashSet<>();
    }

    // Getter for playerName
    public String getName() {
        return this.playerName;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    // getInventory()
    public Set<GameEntity> getInventory() {
        return this.inventory;
    }

    // addToInventory(GameEntity)
    public void addToInventory(GameEntity item) {
        this.inventory.add(item);
    }

    // removeFromInventory(GameEntity)
    public void removeFromInventory(GameEntity item) {
        this.inventory.remove(item);
    }

    //get health hp
    public int getHealth() {
        return this.health;
    }

    //set hp
    public void setHealth(int health) {
        this.health = health;
    }
}
