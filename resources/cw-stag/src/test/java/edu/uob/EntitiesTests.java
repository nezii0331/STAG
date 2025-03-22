package edu.uob;

import edu.uob.entities.*;
import edu.uob.entities.Character;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class EntitiesTests {

    @Test
    void testArtefactBasic() {
        Artefact axe = new Artefact("axe", "A razor sharp axe");
        assertEquals("axe", axe.getName());
        assertEquals("A razor sharp axe", axe.getDescription());
    }

    @Test
    void testFurnitureBasic(){
        Furniture trapdoor = new Furniture("trapdoor", "Wooden trapdoor");
        assertEquals("trapdoor", trapdoor.getName());
        assertEquals("Wooden trapdoor", trapdoor.getDescription());
    }

    @Test
    void testCharacterBasic(){
        Character elf = new Character("elf", "Angry Elf");
        assertEquals("elf", elf.getName());
        assertEquals("Angry Elf", elf.getDescription());
    }

    @Test
    void testLocationAddPath(){
        Location cabin = new Location("cabin", "A log cabin in the woods");
        assertEquals("cabin", cabin.getName());
        assertEquals("A log cabin in the woods", cabin.getDescription());

        cabin.addPath("forest");
        Set<String> paths = cabin.getPaths();
        assertTrue(paths.contains("forest"));

    }

    @Test
    void testAddArtefactsToLocation(){
        Location cellar = new Location("cellar", "A dusty cellar");
        Artefact potion = new Artefact("potion", "Magic potion");

        cellar.addArtefact(potion);
        assertTrue(cellar.getArtefacts().contains(potion));
    }

    @Test
    void testPlayer(){
        Player player = new Player("Rol", "The only one");
        assertEquals("Rol", player.getName());
    }
}