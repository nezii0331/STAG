package edu.uob.actions;

import java.util.LinkedList;
import java.util.List;

public class CustomAction implements GameAction {
    private final List<String> triggers;
    private final List<String> subjects;
    private final List<String> consumed;
    private final List<String> produced;
    private String narration;

    public CustomAction() {
        this.triggers = new LinkedList<>();
        this.subjects = new LinkedList<>();
        this.consumed = new LinkedList<>();
        this.produced = new LinkedList<>();
        this.narration = " ";

    }

    public void addTriggers(String keyword) {
        this.triggers.add(keyword);
    }

    public void addSubjects(String entity) {
        this.subjects.add(entity);
    }

    public void addConsumed(String entity) {
        this.consumed.add(entity);
    }

    public void addProduced(String entity) {
        this.produced.add(entity);
    }

    public void addNarration(String narration) {
        this.narration = narration;
    }

    @Override
    public List<String> getTriggers() {
        return this.triggers;
    }

    @Override
    public List<String> getSubjects() {
        return this.subjects;
    }

    @Override
    public List<String> getConsumed() {
        return this.consumed;
    }

    @Override
    public List<String> getProduced() {
        return this.produced;
    }

    @Override
    public String getNarration() {
        return this.narration;
    }
}
