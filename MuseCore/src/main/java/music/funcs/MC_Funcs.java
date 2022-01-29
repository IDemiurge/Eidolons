package music.funcs;

import main.system.auxiliary.StringMaster;
import music.PlaylistHandler;
import music.PlaylistHandler.PLAYLIST_TYPE;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MC_Funcs {

    public enum MC_FUNCS {
        menu, //navigate via simple joption -
        history,
        showAll,

    public static String showHistory(int pageSize) {
        List<String> formattedHistory=
        PlaylistHandler.getHistory().stream().map(list-> PathUtils.getLastPathSegment(list)).collect(Collectors.toList());
        int n = DialogMaster.pagedOptions(formattedHistory, pageSize, false);
        if (n<0)
            return null;
        return PlaylistHandler.getHistory().get(n);
    }

    public static String showAll(boolean alt, boolean shuffle) {
        PLAYLIST_TYPE[] options = PLAYLIST_TYPE.values();
        int i = JOptionPane.showOptionDialog(null, "Pick one", "Draft",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
        PLAYLIST_TYPE type = options[i];
        return showAll(alt, shuffle, type);
    }

    public static String showAll(boolean alt, boolean shuffle, PLAYLIST_TYPE type) {
        /*
        split into chunks of 10 and JOption!
         */
        int batchSize = 10;
        int n = 0;
        List<File> files = PlaylistHandler.getPlaylistFiles(false, alt, type);

        int from = 0;
        int to = batchSize;
        while (n < files.size() / batchSize) {

            List<String> sub = files.subList(from, to).stream().map(f -> StringMaster.format(StringMaster.cropFormat(f.getName())))
                    .collect(Collectors.toList());
            sub.add(0, alt ? "Next..." : "Next!");
            Object[] options = sub.toArray();
            int res = JOptionPane.showOptionDialog(null, "Pick one", "Draft",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
            if (res == -1)
                return null;
            if (res >= 1) {
                return files.get(n * batchSize + res - 1).getAbsolutePath();
            }
            n++;
            from += batchSize;
            to += batchSize;
        }
        return showAll(!alt, shuffle, type);
    }
}
