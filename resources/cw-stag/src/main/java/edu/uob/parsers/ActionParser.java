package edu.uob.parsers;

import edu.uob.actions.CustomAction;
import org.w3c.dom.*;
import javax.xml.parsers.*;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ActionParser {
    public Set<CustomAction> parseAction(File actionFile) {
        try {
            Set<CustomAction> actions = new HashSet<>();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();

            // Build DocumentBuilder
            // Parsering XML to become Document
            Document doc = db.parse(actionFile);
            doc.getDocumentElement().normalize();

            //Take out all <action> tags
            NodeList actionNodes = doc.getElementsByTagName("action");

            // Print out how many actions there are in total
            System.out.printf("total %d action: %n\n", actionNodes.getLength());

            //get each line
            for(int i = 0; i < actionNodes.getLength(); i++) {
                Element actionElements = (Element) actionNodes.item(i);
                NodeList triggers = actionElements.getElementsByTagName("triggers");

                if (triggers.getLength() > 0) {
                    Element triggerElement = (Element) triggers.item(0);

                    NodeList keywords = triggerElement.getElementsByTagName("keyword");

                    for (int j = 0; j < keywords.getLength(); i++) {
                        String keyword = keywords.item(j).getTextContent().trim();
                        System.out.printf(" trigger: %s%n", keyword);



                    }
                    CustomAction actions = new CustomAction;
                    CustomAction.addKeyword(actions);
                }
            }

            // return null (之後再填)
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        // 使用JAXP API解析.xml文件
        // 創建CustomAction實例

        // 用 getElementsByName("action") 拿出所有動作
    }
}