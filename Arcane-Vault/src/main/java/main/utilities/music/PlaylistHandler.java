package main.utilities.music;

import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistHandler {

    private static final String ROOT_PATH_PICK = "C:\\music\\playlists\\auto\\";
    private static final String ROOT_PATH = "C:\\music\\playlists\\";
    private static final Map<PLAYLIST_TYPE, List<File>> cache = new HashMap<>();

    public enum PLAYLIST_TYPE {
        deep, //1
        ost, //2
        fury, //3
        warmup, //4
        gym, //5
        goodly, //6
        pagan, //7
        dark, //8
        auto, //9
    }

    public static void playRandom(PLAYLIST_TYPE type) {
        List<File> fileList = cache.get(type);
        if (!ListMaster.isNotEmpty(fileList)) {
            String path = ROOT_PATH_PICK + type;
            fileList = FileManager.getFilesFromDirectory(path, false);
            cache.put(type, new ArrayList<>(fileList));
        }
        playRandom(fileList);
    }

    private static void playRandom(List<File> fileList) { int randomIndex = RandomWizard.getRandomIndex(fileList);
        try {
            File file = fileList.remove(randomIndex);
            File properFile = FileManager.getFile(ROOT_PATH + file.getName());
            Desktop.getDesktop().open(properFile);
            System.out.println("-- Playing" +
                    properFile.getPath() +
                    " --");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
