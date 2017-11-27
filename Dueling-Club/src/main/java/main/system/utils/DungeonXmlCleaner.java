package main.system.utils;

import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/6/2017.
 */
public class DungeonXmlCleaner {


    public static void main(String[] args) {

        List<File> files = FileManager.getFilesFromDirectory(PathFinder.getDungeonFolder(), false, true);
        files.forEach(file -> {
            String content = FileManager.readFile(file);
            if (file.getName().contains("Ravenguard")) {
                content.trim();
            }
            content = cleanDungeon(content);
            FileManager.write(content, file.getPath());
        });
        ;

    }

    private static String cleanDungeon(String content) {
        content = removeDuplicateWalls(content);
        return content;
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
        List<String> list = new ArrayList<>();
        for (String substring : StringMaster.open(cleanedObjectsNode, ",")) {

            if (list.contains(substring)) {
                cleanedObjectsNode = cleanedObjectsNode.replaceFirst(substring + ",", "");
            }
            if (checkWall(substring)) {
                list.add(substring);
                continue;
            }

        }

        if (objectsNode.length() != cleanedObjectsNode.length()) {
            content = content.replace(objectsNode, cleanedObjectsNode);
        }

        return content;
    }

    private static boolean checkWall(String substring) {
        if (substring.contains("Wall")) {
            return true;
        }

        return false;
    }
}
