package main.utilities.music;

import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.FileManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PlaylistHandler {

    private static final String ROOT_PATH_PICK = "C:\\music\\playlists\\auto\\";
    private static final String ROOT_PATH = "C:\\music\\playlists\\";

    public enum PLAYLIST_TYPE{
        deep,
        ost,
        fury,
        warmup,
        gym,
        auto,
    }

    public static void playRandom(PLAYLIST_TYPE type){
        String path= ROOT_PATH_PICK+type;
        List<File> fileList = FileManager.getFilesFromDirectory(path, false);

        int randomIndex = RandomWizard.getRandomIndex(fileList);
        try {
            File file = fileList.get(randomIndex);
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
