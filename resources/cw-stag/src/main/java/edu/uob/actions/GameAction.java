package edu.uob.actions;

import java.util.List;

public interface GameAction {
    List<String> getTriggers();
    List<String> getSubjects();
    List<String> getConsumed();
    List<String> getProduced();
    String getNarration();
}
