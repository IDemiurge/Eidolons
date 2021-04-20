package script;

import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlaylistGenerator {

    private static String genRoot = "C:/music/playlists/gen 2021 2/";
    public static final boolean merge = true;
    private static String subfolders = "old mix;2;1;[ambi][spec][ost]atmo metal";
    private static int depth = 0;

    public static void main(String[] args) {
        String root = "D:\\music";
        gen(depth, root);
    }

    public static void gen(int depth, String root) {
        File[] all = (new File(root).listFiles());
        //could do just 1 level deep + recursion!
        for (File file : all) {
            if (file.isDirectory()) {
                if (depth > 0)
                    gen(depth - 1, file.getAbsolutePath());
                else
                    generatePlaylist(file, true);
            }
        }

    }

    private static void generatePlaylist(File root, boolean top) {
        List<File> all = Arrays.asList(root.listFiles());
        if (top && merge && !isMusSubfolder(root.getName())) {
            all = FileManager.getFilesFromDirectory(root.getAbsolutePath(), false, true);
        }
        boolean generated = false;
        for (File file : all) {
            if (file.isDirectory()) {
                generatePlaylist(file, true);
            } else {
                if (!generated && isMusic(file)) {
                    generated = generate(all, root);
                }
            }
        }
    }

    private static boolean isMusSubfolder(String name) {
        return subfolders.contains(name);
    }

    private static boolean isMusic(File file) {
        String format = StringMaster.getFormat(file.getName());
        return format.equalsIgnoreCase(".mp3") || format.equalsIgnoreCase(".wav")
                || format.equalsIgnoreCase(".ogg") || format.equalsIgnoreCase(".flac");
    }

    private static boolean generate(List<File> all, File root) {
        List<File> music = all.stream().filter(file -> file.isFile() && isMusic(file)).collect(Collectors.toList());
        StringBuilder contentBuilder = new StringBuilder();
        if (music.size() > 1) {
            // main.system.auxiliary.src.main.log.LogMaster.src.main.log(1, "Music folder: " + root);
        } else {
            if (music.size() == 2) {
                //ask?
            }
            return true;
        }
        int i = 0;
        for (File file : music) {
            contentBuilder.append(file.getAbsolutePath() + "\n"); //relative?
            i++;
        }
        String path = genRoot + getListName(root);
        main.system.auxiliary.log.LogMaster.log(1, "Writing playlist with " +
                i +
                " files to: " + path);
        FileManager.write(contentBuilder.toString(),
                path);


        return true;
    }

    private static String getListName(File root) {
        String name = root.getName();
        int i = StringMaster.firstIndexOfAny(name, "()[]");
        if (i > 7) {
            name = name.substring(0, i - 1);
        }
        return name + ".m3u";
    }

}
