package eidolons.libgdx.gui.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.stage.GenericGuiStage;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class FileChooserX {


    private static final Map<Stage, FileChooser> map = new HashMap<>();
    public static final WaitMaster.WAIT_OPERATIONS waitOperation =
            WaitMaster.WAIT_OPERATIONS.FILE_SELECTION;

    public static String chooseFile(String folder, String format, Stage stage) {
        GuiEventManager.triggerWithParams(GuiEventType.CHOOSE_FILE, folder, format);
        initFileChooser(stage, folder, format);
        return (String)
                WaitMaster.waitForInput(waitOperation);
    }


    private static void initFileChooser(Stage stage, String folder, String format) {
        //        FileChooser.setFavoritesPrefsName("com.your.package.here.filechooser");
        FileChooser fileChooser = map.get(stage);
        if (!folder.contains(PathFinder.getRootPath())) {
            folder = PathFinder.getRootPath() + folder;
        }
        Gdx.input.setInputProcessor(stage);
        if (fileChooser == null) {
            fileChooser = new FileChooser(FileChooser.Mode.OPEN);
            if (stage instanceof GenericGuiStage) {
                ((GenericGuiStage) stage).setFileChooser(fileChooser);
            }
            stage.addActor(fileChooser.fadeIn());
            map.put(stage, fileChooser);
            if (format != null)
                fileChooser.setFileFilter(
                        new OrFileFilter(DirectoryFileFilter.DIRECTORY,
                                new SuffixFileFilter("." + format)));
        } else {
            stage.addActor(fileChooser.fadeIn());
        }
        fileChooser.setDirectory(folder);
        fileChooser.setSize(GdxMaster.getWidth() * 3 / 4, GdxMaster.getHeight() * 3 / 5);
        GdxMaster.center(fileChooser);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
        FileChooser finalFileChooser = fileChooser;
        FileChooserAdapter adapter;
        fileChooser.clearListeners();
        fileChooser.setListener(adapter = new FileChooserAdapter() {
                    @Override
                    public void canceled() {
                        super.canceled();
                        WaitMaster.receiveInput(waitOperation, null);
                        finalFileChooser.fadeOut();
                    }

                    @Override
                    public void selected(Array<FileHandle> file) {
                        if (file.size == 0) return;
                        WaitMaster.receiveInput(waitOperation, file.get(0).file().getAbsolutePath());
                        finalFileChooser.fadeOut();
                    }
                }

        );
        FileChooser finalFileChooser1 = fileChooser;
        fileChooser.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    try {
                        Method selectionFinished = FileChooser.class.getDeclaredMethod("selectionFinished");
                        selectionFinished.setAccessible(true);
                        try {
                            selectionFinished.invoke(finalFileChooser1);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
                return super.keyDown(event, keycode);
            }
        });
    }
}
