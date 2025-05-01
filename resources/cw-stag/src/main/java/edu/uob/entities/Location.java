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

    public Set<GameEntity> getEntities() {
        Set<GameEntity> allEntities = new HashSet<>();
        allEntities.addAll(this.artefacts);
        allEntities.addAll(this.furniture);
        allEntities.addAll(this.characters);
        return allEntities;
    }

    public void addEntity(GameEntity entity) {
        //if this is artefact
        if (entity instanceof Artefact) {
            artefacts.add((Artefact)entity);
        }
        //if this is furniture
        if (entity instanceof Furniture) {
            furniture.add((Furniture)entity);
        }
        //if this is character
        if (entity instanceof GameCharacter) {
            characters.add((GameCharacter)entity);
        }
    }

    public void removeEntity(GameEntity entity){
        String entityName = entity.getName().toLowerCase();
        
        // 特殊处理: 如果是树木，直接清理furniture集合中的所有树
        if (entityName.equals("tree")) {
            System.out.println("SPECIAL TREE REMOVAL: direct removal from furniture collection");
            Set<Furniture> toRemove = new HashSet<>();
            for (Furniture f : furniture) {
                if (f.getName().equalsIgnoreCase("tree")) {
                    toRemove.add(f);
                    System.out.println("Marked tree for removal");
                }
            }
            if (!toRemove.isEmpty()) {
                furniture.removeAll(toRemove);
                System.out.println("Removed all trees");
                return;
            }
        }
        
        // Debug info
        System.out.println("Removing entity: " + entityName + " from location: " + getName());
        
        // First, try direct removal with object identity
        boolean removedDirect = false;
        if (entity instanceof Artefact) {
            removedDirect = artefacts.remove(entity);
        } else if (entity instanceof Furniture) {
            removedDirect = furniture.remove(entity);
        } else if (entity instanceof GameCharacter) {
            removedDirect = characters.remove(entity);
        }
        
        // If direct removal failed, try by name with case-insensitive comparison
        if (!removedDirect) {
            System.out.println("Direct removal failed, trying by name...");
            
            // Create temporary sets to avoid ConcurrentModificationException
            Set<Artefact> artefactsToRemove = new HashSet<>();
            Set<Furniture> furnitureToRemove = new HashSet<>();
            Set<GameCharacter> charactersToRemove = new HashSet<>();
            
            // Find entities to remove
            for (Artefact item : artefacts) {
                if (item.getName().equalsIgnoreCase(entityName)) {
                    artefactsToRemove.add(item);
                }
            }
            
            for (Furniture item : furniture) {
                if (item.getName().equalsIgnoreCase(entityName)) {
                    furnitureToRemove.add(item);
                }
            }
            
            for (GameCharacter item : characters) {
                if (item.getName().equalsIgnoreCase(entityName)) {
                    charactersToRemove.add(item);
                }
            }
            
            // Remove found entities
            artefacts.removeAll(artefactsToRemove);
            furniture.removeAll(furnitureToRemove);
            characters.removeAll(charactersToRemove);
        }
        
        // Verify removal
        if (hasEntity(entityName)) {
            System.err.println("WARNING: Entity " + entityName + " still exists after removal attempt!");
            // Last resort: brute force removal with streams
            artefacts.removeIf(a -> a.getName().equalsIgnoreCase(entityName));
            furniture.removeIf(f -> f.getName().equalsIgnoreCase(entityName));
            characters.removeIf(c -> c.getName().equalsIgnoreCase(entityName));
        } else {
            System.out.println("Entity " + entityName + " successfully removed");
        }
    }

    /**
     * Helper method to remove entities by name from a collection
     */
    private <T extends GameEntity> void removeByName(Set<T> collection, String entityName) {
        Set<T> toRemove = new HashSet<>();
        for (T item : collection) {
            if (item.getName().equalsIgnoreCase(entityName)) {
                toRemove.add(item);
            }
        }
        collection.removeAll(toRemove);
    }

    //helper
    public boolean hasEntity(String name) {
        for (GameEntity entity : this.getEntities()) {
            if (entity.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
