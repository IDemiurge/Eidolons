package music;

import main.system.PathUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PlaylistHandler {

    public static final String ROOT_PATH_PICK = "C:\\music\\playlists\\auto\\";
    private static final String ROOT_PATH = "C:\\music\\playlists\\";
    private static final Map<PLAYLIST_TYPE, List<File>> cache = new HashMap<>();
    private static final Map<PLAYLIST_TYPE, List<File>> cacheAlt = new HashMap<>();
    public static int draft=0;


    public enum PLAYLIST_TYPE {
        deep, //1
        ost, //2
        gym, //3
        battles, //4
        fury, //5
        goodly, //6
        pagan, //7
        dark, //8
        warmup, //9
        rpg, //10
        metal, //11
        future //12

        ,
        auto,
        coding,
        design,
        writing,
    }

    public static int draft(List<File> fileList, int n) {
        if (n>fileList.size())
            n = fileList.size();

        int from=0;
        int to=n;
        //TODO
        List<File> list = fileList.subList(from, to);

        List<String> collect = list.stream().
                map(file->  StringMaster.format( StringMaster.cropFormat(file.getName()))).
                collect(Collectors.toList());
        Object[] options = collect.toArray();
        return
                JOptionPane.showOptionDialog(null, "Pick one", "Draft", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);

    }

    public static void playRandom(boolean alt, PLAYLIST_TYPE type) {
        for (int i = 0; i < 12; i++) {
            List<File> fileList = getPlaylistFiles(alt, type);
            if (playRandom(fileList)) {
                return;
            }
        }
    }

    public static Map<PLAYLIST_TYPE, List<File>> getCache(boolean a) {
        return a ? cacheAlt : cache;
    }

    public static List<File> getPlaylistFiles(boolean alt, PLAYLIST_TYPE type) {
        return getPlaylistFiles(true, alt, type);
    }
    public static List<File> getPlaylistFiles(boolean useCache, boolean alt, PLAYLIST_TYPE type) {
        List<File> fileList = getCache(alt).get(type);
        if (!useCache || !ListMaster.isNotEmpty(fileList)) {
            String path = ROOT_PATH_PICK + type;
            if (alt) {
                path += "/alt";
            }
            fileList = FileManager.getFilesFromDirectory(path, false);
            getCache(alt).put(type, new ArrayList<>(fileList));
        }
        return fileList;
    }
    private static boolean playRandom(List<File> fileList) {
        int randomIndex = getRandomIndex(fileList);

        File file = fileList.remove(randomIndex);
        if (isTryRootPathAlways()) {
            if (play(ROOT_PATH, file.getName()))
                return true;
        }
        String prefix = PathUtils.cropLastPathSegment(file.getAbsolutePath());
        return play(prefix, file.getName());
    }

    private static int getRandomIndex(List<File> fileList) {
        if (draft > 0) {
            int result = draft(fileList, draft);
            if (result > 0)
                return result;
        }
        return RandomWizard.getRandomIndex(fileList);
    }

    public static boolean play(String path) {
        return play(ROOT_PATH, path);
    }

    public static boolean play(String appendPath, String path) {
        try {
            File properFile = FileManager.getFile(appendPath + path);
            if (properFile.exists()) {
                Desktop.getDesktop().open(properFile);
                history.add(0, properFile.getPath());
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
