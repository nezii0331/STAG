package edu.uob.entities;

import java.util.HashSet;
import java.util.Set;

public class Location extends GameEntity{
// ===  field area === //
    //Stores <walkable location> names (cabin, forest, cellar)
    private Set<String> paths;
    private Set<Artefact> artefacts;
    private Set<Furniture> furniture;
    private Set<GameCharacter> characters;
    private Set<Player> players;

// === Constructor === //
    public Location(String name, String description){

        //CALL FOR UPPER
        super(name, description);

        this.paths = new HashSet<>();
        this.artefacts = new HashSet<>();
        this.furniture = new HashSet<>();
        this.characters = new HashSet<>();
        this.players = new HashSet<>();
    }

// === method area === //
    // add path
    public void addPath(String pathName){
        this.paths.add(pathName);
    }

    // take all path
    public Set<String> getPaths(){
        return this.paths;
    }

    // pick artefacts
    public void addArtefact(Artefact item){
        this.artefacts.add(item);
    }

    //take out all artefacts
    public Set<Artefact> getArtefacts(){
        return this.artefacts;
    }

    public void addFurniture(Furniture item ){
        this.furniture.add(item);
    }

    public Set<Furniture> getFurniture(){
        return this.furniture;
    }

    public void addCharacter(GameCharacter character){
        this.characters.add(character);
    }

    public Set<GameCharacter> getCharacters(){
        return this.characters;
    }

    public void addPlayer(Player player){
        this.players.add(player);
    }

    public Set<Player> getPlayers(){
        return this.players;
    }
}
