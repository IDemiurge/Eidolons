package main.system.utils;

import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 3/20/2017.
 */
public class XmlHelper {

public static void replaceSpecEffectsWithBonusDamage(){
    String passiveAbils;


}


    private static String removeDuplicateWalls(String content) {
        int begin = content.lastIndexOf("<Objects>") + "<Objects>".length();
        int finish = content.indexOf("</Objects>");
        if (begin > finish) {
            return content;
        }
        if (begin < 0) {
            return content;
        }
        if (finish < 0) {
            return content;
        }
        String objectsNode = content.substring(begin, finish);
        String cleanedObjectsNode = objectsNode;
        List<String> list = new LinkedList<>();
        for (String substring : StringMaster.open(cleanedObjectsNode, ",")) {

            if (list.contains(substring)) {
                cleanedObjectsNode = cleanedObjectsNode.replaceFirst(substring + ",", "");
            }
            if (check(substring)) {
                list.add(substring);
                continue;
            }

        }

        if (objectsNode.length() != cleanedObjectsNode.length()) {
            content = content.replace(objectsNode, cleanedObjectsNode);
        }

        return content;
    }

    private static boolean check(String substring) {
        return false;
    }
}
