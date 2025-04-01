package edu.uob.games;

import edu.uob.actions.BasicAction;
import edu.uob.actions.CustomActionExecutor;
import edu.uob.entities.Location;

//Add diversion:
// use BasicAction for built-in, otherwise leave it to CustomActionExecutor
public class GameController {

    private final GameWorld world;
    private final GameState state;

    public GameController(GameWorld world, GameState state){
        this.world = world;
        this.state = state;
    }

    public String handleCommand(String input){
        // 1. parser input → playerName, command
        String[] parts = input.split(":");// 用冒號切開

        // check long
        if(parts.length < 2){
            return "Your command is invalid, please use like [player : command].";
        }
        // take space and
        String playerName = parts[0].trim().toLowerCase();
        String command = parts[1].trim().toLowerCase();

        // 2. create start PlayerState
        Location startLocation = world.getLocation("cabin");
        PlayerState startState = state.currentStates(playerName, startLocation);

        // 3. handle basic command
        if(command.equals("look")){
            return BasicAction.handleLook(startState, world, state);
        }

        if(command.equals("inventory") || command.equals("inv")){
            return BasicAction.handleInventory(startState);
        }
        // add hp
        if(command.equals("health")){
            return BasicAction.handleHealth(startState);
        }
        if(command.startsWith("get ")){
            String item = command.substring(4).trim(); // get item name
            return BasicAction.handleGet(startState, item);
        }

        if(command.startsWith("drop ")){
            String item = command.substring(5).trim();
            return BasicAction.handleDrop(startState, item, startState.getLocation());
        }

        if(command.startsWith("goto ")){
            String destination = command.substring(5).trim();
            return BasicAction.handleGoto(world, startState, destination);
        }

        //drink
        if(command.startsWith("drink ")){
            String item = command.substring(6).trim();
            return BasicAction.handleDrink(startState, item);
        }

        //add fight
        if(command.startsWith("fight ") || command.startsWith("attack ")){
            String target;
            if(command.startsWith("fight ")){
                target = command.substring(6).trim();
            } else {
                target = command.substring(7).trim();
            }
            return BasicAction.handleFight(startState, target, world);
        }

        // give what they want
        return CustomActionExecutor.executeCustomAction(world, state, startState, command);
    }
}