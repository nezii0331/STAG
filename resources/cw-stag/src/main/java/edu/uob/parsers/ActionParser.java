package edu.uob.parsers;

import edu.uob.actions.CustomAction;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ActionParser {
    /**
     * Parse XML file to extract action details
     * @param actionFile XML file containing action definitions
     * @return Set of CustomAction objects parsed from the XML
     */
    public Set<CustomAction> parseAction(File actionFile) {
        Set<CustomAction> actions = new HashSet<>();

        try {
            // Create DocumentBuilder for XML parsing
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();

            // Parse XML file
            Document doc = db.parse(actionFile);
            doc.getDocumentElement().normalize();

            // Get all <action> tags
            NodeList actionNodes = doc.getElementsByTagName("action");

            // Log total number of actions
            System.out.printf("Total %d actions:%n", actionNodes.getLength());

            // Iterate through each action
            for (int i = 0; i < actionNodes.getLength(); i++) {
                Element actionElement = (Element) actionNodes.item(i);
                CustomAction currentAction = new CustomAction();

                // Process triggers
                processTriggersForAction(actionElement, currentAction);
                // Process subjects
                processSubjectsForAction(actionElement, currentAction);
                // Process consumed entities
                processConsumedEntities(actionElement, currentAction);
                // Process produced entities
                processProducedEntities(actionElement, currentAction);
                // Process narration
                processNarration(actionElement, currentAction);
                // Add action to the set
                actions.add(currentAction);
            }

        } catch (Exception e) {
            // Log any parsing errors
            System.err.println("Error parsing action file: " + e.getMessage());
            e.printStackTrace();
        }

        return actions;
    }

    /**
     * Process trigger keywords for an action
     * @param actionElement XML element representing the action
     * @param action CustomAction to add triggers to
     */
    private void processTriggersForAction(Element actionElement, CustomAction action) {
        NodeList triggers = actionElement.getElementsByTagName("triggers");
        if (triggers.getLength() > 0) {
            Element triggerElement = (Element) triggers.item(0);
            NodeList keywords = triggerElement.getElementsByTagName("keyphrase");

            for (int j = 0; j < keywords.getLength(); j++) {
                String keyword = keywords.item(j).getTextContent().trim();
                System.out.printf("Trigger: %s%n", keyword);
                // Assuming addKeyword method exists in CustomAction
//===check==    CustomAction actions = new CustomAction();
                action.addTriggers(keyword);
                //===check==
                System.out.printf("keyword: %s\n", keyword);
            }
        }
    }

    /**
     * Process subject entities for an action
     * @param actionElement XML element representing the action
     * @param action CustomAction to add subjects to
     */
    private void processSubjectsForAction(Element actionElement, CustomAction action) {
        NodeList subjects = actionElement.getElementsByTagName("subjects");
        if (subjects.getLength() > 0) {
            Element subjectElement = (Element) subjects.item(0);
            NodeList keywords = subjectElement.getElementsByTagName("entity");

            for (int j = 0; j < keywords.getLength(); j++) {
                String keyword = keywords.item(j).getTextContent().trim();
                System.out.printf("Subject: %s%n", keyword);
                // Add method to store subjects in CustomAction if needed
                action.addSubjects(keyword);
            }
        }
    }

    /**
     * Process consumed entities for an action
     * @param actionElement XML element representing the action
     * @param action CustomAction to add consumed entities to
     */
    private void processConsumedEntities(Element actionElement, CustomAction action) {
        NodeList consumed = actionElement.getElementsByTagName("consumed");
        if (consumed.getLength() > 0) {
            Element consumedElement = (Element) consumed.item(0);
            NodeList keywords = consumedElement.getElementsByTagName("entity");

            for (int j = 0; j < keywords.getLength(); j++) {
                String keyword = keywords.item(j).getTextContent().trim();
                System.out.printf("Consumed: %s%n", keyword);
                // Add method to store consumed entities in CustomAction if needed
                action.addConsumed(keyword);
            }
        }
    }

    /**
     * Process produced entities for an action
     * @param actionElement XML element representing the action
     * @param action CustomAction to add produced entities to
     */
    private void processProducedEntities(Element actionElement, CustomAction action) {
        NodeList produced = actionElement.getElementsByTagName("produced");
        if (produced.getLength() > 0) {
            Element producedElement = (Element) produced.item(0);
            NodeList keywords = producedElement.getElementsByTagName("entity");

            for (int j = 0; j < keywords.getLength(); j++) {
                String keyword = keywords.item(j).getTextContent().trim();
                System.out.printf("Produced: %s%n", keyword);
                // Add method to store produced entities in CustomAction if needed
                action.addProduced(keyword);
            }
        }
    }

    /**
     * Process narration for an action
     * @param actionElement XML element representing the action
     * @param action CustomAction to add narration to
     */
    private void processNarration(Element actionElement, CustomAction action) {
        NodeList narrations = actionElement.getElementsByTagName("narration");
        if (narrations.getLength() > 0) {
            String narrationWord = narrations.item(0).getTextContent().trim();
            System.out.printf("Narration: %s%n", narrationWord);
            // Add method to store narration in CustomAction if needed
            action.addNarration(narrationWord);
        }
    }
}