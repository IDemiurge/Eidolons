package music;

import main.swing.generic.services.DialogMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class PlaylistFinder {
   public static final  String folder="C:\\music\\playlists";
    public static void findAndPlay() {
        String name = DialogMaster.inputText("Playlist name?");
        List<File> files = FileManager.findFiles(new File(folder), name, true, true);
        if (files.isEmpty()) {
            files = FileManager.findFiles(new File(folder), name, true, false);
        }
        // if (files.isEmpty()) {
        //     files = FileManager.findFiles(new File(folder), name, true, false);
        // }
        if (files.isEmpty()) {
            return;
        }
        // files =
        //         files.stream().filter(f -> )
        // new ListMaster<File>().getRemovedDuplicates(files);
        File found = files.get(0);
        if (files.size()>1){
            Object[] a = files.stream().map(f -> f.getName()).collect(Collectors.toList()).toArray();
            found = files.get(DialogMaster.optionChoice(a, "Which is it?"));
        }
        PlaylistHandler.play("", found.getPath());
    }
}
