package eidolons.system.text;

import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.system.EventCallbackParam;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Texts {
    private static final String TEXT_HEADER_SEPARATOR = Pattern.quote("***");
    static{
        init();
    }

//    public static final String TEXT_HEADER_SEPARATOR = "***";
    static Map<String, Map<String, String>> maps;

    public static void init() {
        maps = new HashMap<>();
        for (File file : FileManager.getFilesFromDirectory(PathFinder.getRootPath()+getTextsPath(), true, true)) {
            if (file.isFile()) {
                String text = FileManager.readFile(file);
                Map<String, String> map = parseText(text);
                String name = StringMaster.cropFormat(file.getName());
                maps.put(name, map);
            }
        }

    }

    private static Map<String, String> parseText(String text) {
        Map<String, String> map = new XLinkedMap<>();
        for (String chunk : text.split(TEXT_HEADER_SEPARATOR)) {
            chunk=chunk.trim();
            if (chunk.isEmpty()) {
                continue;
            }
            String key = StringMaster.splitLines(chunk, false)[0];
            chunk= chunk.replaceFirst(key, "");
            map.put(key, chunk );
        }
        return map;
    }

    private static String getTextsPath() {
        return PathFinder.getTextPathLocale() + "main/";
    }

    public static Map<String, String> getTextMap(String key) {
        return maps.get(key);
    }
    /**
     * keep the maps of all relevant texts!
     *
     * parse from the sources
     *
     *
     */
}
