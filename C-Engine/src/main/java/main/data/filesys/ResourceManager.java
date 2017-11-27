package main.data.filesys;

import main.system.datatypes.DequeImpl;

import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class ResourceManager {
    private static String PATH;

    public static void init() {
        PATH = PathFinder.getEnginePath();
    }

    public static List<String> getFilesInFolder(String path) {
        DequeImpl<String> list = new DequeImpl<>(
                Arrays.asList(getResourcesInFolder(path)));
        // for (String s : list) {
        // if (!getFile(s).isFile()) {
        // list.remove(s);
        // }
        // }
        return new ArrayList<>(list);
    }

    public static String[] getResourcesInFolder(String path) {
        File file = getFile(path);
        return file.list();
    }

    public static File getFile(String path) {
        return new File(PATH + "\\" + path);
    }
}
