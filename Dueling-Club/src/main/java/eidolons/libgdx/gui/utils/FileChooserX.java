package eidolons.libgdx.gui.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import java.util.HashMap;
import java.util.Map;

public class FileChooserX {


    private static Map<Stage, FileChooser> map = new HashMap<>();

    public static String chooseFile(String folder, String format, Stage stage) {
        GuiEventManager.triggerWithParams(GuiEventType.CHOOSE_FILE, folder, format);
        initFileChooser(stage, folder, format);
        return (String)
                WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.SELECTION);
    }


    private static void initFileChooser(Stage stage, String folder, String format) {
//        FileChooser.setFavoritesPrefsName("com.your.package.here.filechooser");
        FileChooser fileChooser = map.get(stage);
        Gdx.input.setInputProcessor(stage);
        if (fileChooser == null) {
            fileChooser = new FileChooser(FileChooser.Mode.OPEN);
            stage.addActor(fileChooser.fadeIn());
            map.put(stage, fileChooser);
            fileChooser.setDirectory(folder);
            fileChooser.setFileFilter(
                    new OrFileFilter(DirectoryFileFilter.DIRECTORY,
                            new SuffixFileFilter("." + format)));
        } else {
            stage.addActor(fileChooser.fadeIn());

        }
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
        FileChooser finalFileChooser = fileChooser;
        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(Array<FileHandle> file) {
                if (file.size == 0) {
                    return;
                }
                WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.SELECTION, file.get(0).file().getAbsolutePath());
                finalFileChooser.fadeOut();
            }
        });

    }
}
