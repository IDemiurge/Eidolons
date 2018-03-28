package main.libgdx.texture;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import main.data.filesys.PathFinder;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.data.FileManager;
import main.system.graphics.GuiManager;
import main.system.launch.CoreEngine;

import java.io.File;
import java.util.List;

/**
 * Created by PC on 03.11.2016.
 */
public class TexturePackerLaunch {
    public static final String ATLAS_EXTENSION = ".txt";
    public static final String WORKSPACE_PATH_POTIONS = PathFinder.getImagePath() +
     PathFinder.getPotionsAnimPath() + "processing//";

    public static final String WORKSPACE_PATH = PathFinder.getImagePath() + PathFinder.getWeaponAnimPath() + "workspace//";
    public static final boolean TRIM = true;
    public static final boolean POTIONS = false;
    private static final String OUTPUT_DIR = PathFinder.getImagePath() +
     PathFinder.getWeaponAnimPath() + "atlas//";
    private static final String OUTPUT_DIR_POTION = PathFinder.getImagePath() +
     PathFinder.getPotionsAnimPath() + "atlas//";

    static String packs[] = {
//     "long swords",
     "hammers",
     "great_swords",
     "daggers",
     "maces",
     "axe",
//     "fists",
//     "short swords",
//     "test",
//     "crossbows",
//     "bows",
//     "bolts",
//     "arrows",
//     "pollaxe",
    };

    static String mainFolders[] = {
//     "gen",
     "ui",
//     "main",
    };

    static String mainFoldersExceptions[] = {
     "main\\sprites",
    };

    public static void main(String[] args) {
//        packImages(mainFolders);
        String[] chosen = packs;
        GuiManager.init();
        if (CoreEngine.isExe() || CoreEngine.isJar())
            chosen =
             ListChooser.chooseFile(POTIONS ? WORKSPACE_PATH_POTIONS : WORKSPACE_PATH, null, SELECTION_MODE.MULTIPLE, true)
              .split(";");
        else if (DialogMaster.confirm("Choose?")) {
            chosen =
             ListChooser.chooseFile(POTIONS ? WORKSPACE_PATH_POTIONS : WORKSPACE_PATH, null, SELECTION_MODE.MULTIPLE, true)
              .split(";");
        }
        packWeaponSprites(chosen);
    }

    private static void packImages(String[] folders) {
        Settings settings = getSetting();
        for (String sub : folders) {
//            List<File> files = FileManager.getFilesFromDirectory(PathFinder.getImagePath() + sub, false, true);
//            for (File folder : files) {
            String inputDir = PathFinder.getImagePath() + sub;
//            Math.sqrt(files.size())
            String outputDir = inputDir;
            String packFileName = sub;

            TexturePacker.process(settings, inputDir, outputDir, packFileName);
//            }
        }
    }

    private static Settings getSetting() {
        Settings settings = new Settings();
        settings.maxHeight = (int) Math.pow(2, 14);
        settings.maxWidth = (int) Math.pow(2, 14);
        settings.atlasExtension = ATLAS_EXTENSION;
        settings.stripWhitespaceY = TRIM;
        settings.stripWhitespaceX = TRIM;
        settings.square = false;
        settings.format = Format.RGBA4444;
        settings.limitMemory = false;
//        settings.jpegQuality = 0.7f;
        return settings;
    }

    public static void packWeaponSprites(String[] args) {
        Settings settings = getSetting();
        String outputDir = POTIONS ? OUTPUT_DIR_POTION : OUTPUT_DIR;
        for (String sub : args) {
            String dir = POTIONS ? WORKSPACE_PATH_POTIONS : WORKSPACE_PATH
             + sub;
            List<File> subFolders = FileManager.getFilesFromDirectory(dir, true);
            boolean processed = false;
            for (File subFolder : subFolders) {
                if (!subFolder.isDirectory())
                    continue;
                String inputDir =
//                 PathFinder.getWeaponAnimPath() + "workspace//"
//                 + sub + "//" +
                 subFolder.getPath();
                String packFileName = subFolder.getName();

                TexturePacker.process(settings, inputDir, outputDir, packFileName);
                processed = true;
            }
            if (!processed) {
                TexturePacker.process(settings, dir, outputDir, sub);
            }
        }
        return;
//            TexturePacker.process(inputDir, outputDir, packFileName);
    }

}

