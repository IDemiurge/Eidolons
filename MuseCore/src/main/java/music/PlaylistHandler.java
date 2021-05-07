package music;

import main.content.VALUE;
import main.content.ValueMap;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistHandler {

    public static final String ROOT_PATH_PICK = "C:\\music\\playlists\\auto\\";
    private static final String ROOT_PATH = "C:\\music\\playlists\\";
    private static final Map<PLAYLIST_TYPE, List<File>> cache = new HashMap<>();
    private static final Map<PLAYLIST_TYPE, List<File>> cacheAlt = new HashMap<>();

    public enum PLAYLIST_TYPE {
        deep, //1
        ost, //2
        gym, //3
        auto, //4
        fury, //5
        goodly, //6
        pagan, //7
        dark, //8
        coding, //9
        rpg, //10
        metal, //11
        finest //12

        ,
        warmup,
        design,
        writing,
    }

    public static void playRandom(boolean alt, PLAYLIST_TYPE type) {
        for (int i = 0; i < 12; i++) {
            List<File> fileList = getCache(alt).get(type);
            if (!ListMaster.isNotEmpty(fileList)) {
                String path = ROOT_PATH_PICK + type;
                if (alt) {
                    path += "/alt";
                }
                fileList = FileManager.getFilesFromDirectory(path, false);
                getCache(alt).put(type, new ArrayList<>(fileList));
            }
            if (playRandom(fileList)) {
                return;
            }
        }
    }

    public static Map<PLAYLIST_TYPE, List<File>> getCache(boolean a) {
        return a? cacheAlt : cache;
    }

    private static boolean playRandom(List<File> fileList) {
        int randomIndex = RandomWizard.getRandomIndex(fileList);

        File file = fileList.remove(randomIndex);
        if (isTryRootPathAlways()) {
            if (play(ROOT_PATH, file.getName()))
                return true;
        }

        return play("", file.getName());
    }

    public static boolean play(  String path) {
        return play(ROOT_PATH, path);
    }
    public static boolean play(String appendPath, String path) {
        try {
            File properFile = FileManager.getFile(appendPath + path);
            if (properFile.exists()) {
                Desktop.getDesktop().open(properFile);
                System.out.println("-- Playing" +
                        properFile.getPath() +
                        " --");
                return true;
            }
            System.out.println("-- Does not exist: " +
                    properFile.getPath() +
                    " --");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isTryRootPathAlways() {
        return true;
    }
}
