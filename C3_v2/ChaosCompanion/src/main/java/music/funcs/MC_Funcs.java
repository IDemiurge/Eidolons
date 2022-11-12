package music.funcs;

import main.swing.generic.services.DialogMaster;
import main.system.PathUtils;
import main.system.auxiliary.StringMaster;
import music.PLAYLIST_TYPE;
import music.PlaylistHandler;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class MC_Funcs {

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
        List<File> files = PlaylistHandler.getPlaylistFiles(false, alt, type);
        List<String> sub = files.stream().map(f -> StringMaster.format(StringMaster.cropFormat(f.getName())))
                .collect(Collectors.toList());

        int index =
                DialogMaster.pagedOptions(sub, batchSize, alt);
        if (index > 0)
            return files.get(index).getAbsolutePath();
        return showAll(!alt, shuffle, type);
    }
}
