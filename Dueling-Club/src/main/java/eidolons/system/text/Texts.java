package eidolons.system.text;

import main.data.StringMap;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Texts {
    private static final String TEXT_HEADER_SEPARATOR = Pattern.quote("***");

    static {
        init();
    }

    //    public static final String TEXT_HEADER_SEPARATOR = "***";
    static Map<String, Map<String, String>> maps;

    public static void init() {
        maps = new HashMap<>();
        for (File file : FileManager.getFilesFromDirectory(PathFinder.getRootPath() + getTextsPath(), true, true)) {
            if (file.isFile()) {
                String name = StringMaster.cropFormat(file.getName());
                boolean scriptsFile = false;
                if (name.contains("comments")) {
                    name = "comments";
                } else if (name.contains("scripts")) {
                    name = "scripts";
                    scriptsFile = true;
                }
                String text = FileManager.readFile(file);
                Map<String, String> map =
                        parseText(text, scriptsFile);
                Map<String, String> prev = maps.get(name);
                if (prev != null) {
                    prev.putAll(map);
                } else
                    maps.put(name, map);
            }
        }

    }

    private static Map<String, String> parseText(String text, boolean scriptsFile) {
        Map<String, String> map = new StringMap<>();
        for (String chunk : text.split(TEXT_HEADER_SEPARATOR)) {
            chunk = chunk.trim();
            if (chunk.isEmpty()) {
                continue;
            }
            String key = StringMaster.splitLines(chunk, false)[0].trim();
            chunk = chunk.replaceFirst(key, "");
            key = formatKey(key, scriptsFile);
            map.put(key, chunk);
        }
        return map;
    }

    private static String formatKey(String key, boolean scriptsFile) {
//        if (scriptsFile) {
//            return key.replace(" ", "_");
//        }
        return key;
    }

    private static String getTextsPath() {
        return PathFinder.getTextPathLocale() + "main/";
    }

    public static Map<String, String> getTextMap(String key) {
        return maps.get(key);
    }

    public static Map<String, String> getComments() {
        return getTextMap("comments");
    }

    private static Map<String, String> getScriptsMap() {
        return getTextMap("scripts");
    }

    public static String getScript(String scriptName) {
        String script = getScriptsMap().get(scriptName);
        if (script != null) {
            return script;
        }
        return getScriptsMap().get(scriptName.replace(" ", "_"));
    }
    /**
     * keep the maps of all relevant texts!
     *
     * parse from the sources
     *
     *
     */
}
