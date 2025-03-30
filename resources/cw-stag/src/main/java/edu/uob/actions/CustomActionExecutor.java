package edu.uob.actions;

import edu.uob.entities.Artefact;
import edu.uob.entities.GameEntity;
import edu.uob.entities.Location;
import edu.uob.games.GameState;
import edu.uob.games.GameWorld;
import edu.uob.games.PlayerState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Execute action for customer
 */


public class CustomActionExecutor {

    //Confirm if there is a key trigger in player input
    public static boolean toMatchTrigger(Set<String> triggers, String command){
        for(String trigger : triggers){
            if(command.toLowerCase().contains(trigger.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    // 1. Checks whether the player inventory or location has the item specified by subject
    public static boolean toSubjectsAvailable(PlayerState player, Location location, String subject){
        // Check if this subject exists in the player inventory
        for (GameEntity item : player.getInventory()){
            if(item.getName().equalsIgnoreCase(subject)){
                return true;
            }
        }

        // Check whether the entity in the scene has this subject
        for (GameEntity item : location.getEntities()){
            if(item.getName().equalsIgnoreCase(subject)){
                return true;
            }
        }
        return false;
    }

    // 2. Remove consumed items from player or location
    public static void toApplyConsumed(PlayerState player, Location location, String consumed){
       //player
        for(GameEntity item : new HashSet<>(player.getInventory())){
            if (item.getName().equalsIgnoreCase(consumed)){
                player.removeFromInventory(item);
            }
        }
        for(GameEntity item : new HashSet<>(location.getEntities())){
            if (item.getName().equalsIgnoreCase(consumed)){
                // TODO:might have to add this method
                location.removeEntity(item);
            }
        }
    }


    // 3. Add the produced items to location or inventory (use world to find objects)
    public static void toApplyProduced(PlayerState player, Location location, String produced, GameWorld world) {
        // if player use produce then add thing to location or player's bag
        // location TODO:might have to add this method
        GameEntity entity = world.findEntityByName(produced);
        if (entity == null) {
            return;
        }

        if (entity instanceof Artefact) {
            player.addToInventory(entity);
        } else {
            // TODO:might have to add this method
            location.addEntity(entity);
        }
    }

    // 4. Integrate the above functions and return narration or error messages
    public static String executeCustomAction(GameWorld world, GameState state, PlayerState player, String command) {
        // get all action
        Set<GameAction> actions = world.getAllActions();

        //From all GameActions, find the CustomAction
        //and then compare whether the player input command contains a trigger.

        // pick up CustomAction
        for (GameAction action : actions) {
            // check is CustomAction?
            if (action instanceof CustomAction) {
                CustomAction ca = (CustomAction) action;
                // this for trigger
                if (CustomActionExecutor.matchTrigger(ca.getTriggers(), command)) {
                    // check for every subject
                    for (String subject : ca.getSubjects()) {
                        if (!CustomActionExecutor.toCheckSubjects(player, player.getLocation(), subject)) {
                            return "You’re missing something required to perform this action.";
                        }
                    }
                    for(String consumed : ca.getConsumed()){
                        CustomActionExecutor.toApplyConsumed(player, player.getLocation(), consumed);
                    }
                    for(String produced : ca.getProduced()){
                        CustomActionExecutor.toApplyProduced(player, player.getLocation(), produced,world);
                    }
                    return ca.getNarration();
                }
            }
        }
        return "I don't understand your command.";
    }

    public static boolean matchTrigger(List<String> triggers, String command){
        // If the trigger is "chop" and the instruction is "chop tree", this will be true
        for (String trigger : triggers){
            if(command.toLowerCase().contains(trigger.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    public static boolean toCheckSubjects(PlayerState player, Location location, String subject){
        //player bag
        for(GameEntity item : player.getInventory()){
            if(item.getName().equalsIgnoreCase(subject)){
                return true;
            }
        }
        //location
        for(GameEntity item : location.getEntities()){
            if(item.getName().equalsIgnoreCase(subject)) {
                return true;
            }
        }
        return false;
    }
}
