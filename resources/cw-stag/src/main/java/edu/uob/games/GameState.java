package edu.uob.games;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import edu.uob.entities.Location;

public class GameState {
    private Map<String, PlayerState> playerStates;
    private Set<PlayerState> playersHere = new HashSet<>();

    public GameState() {
        this.playerStates = new LinkedHashMap<>();
    }


    public PlayerState currentStates(String playerName, Location startLocation){
        if(!playerStates.containsKey(playerName)){
            playerStates.put(playerName, new PlayerState(playerName, startLocation));
        }
        return playerStates.get(playerName);
    }

   public Set<PlayerState> getAllPlayerStatesAt(Location location) {
       for (PlayerState player : playerStates.values()) {
           if (player.getLocation().equals(location)) {
               playersHere.add(player);
           }
       }
       return playersHere;
   }

    public void addPlayer(PlayerState player) {
        this.playerStates.put(player.getName(), player);
    }
}