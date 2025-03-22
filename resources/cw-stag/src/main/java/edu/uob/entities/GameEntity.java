package edu.uob.entities;

/**
 * Abstract base class
 * providing basic attributes such as name and description
 */

public abstract class GameEntity
{
    private String name;
    private String description;

    public GameEntity(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }
}
