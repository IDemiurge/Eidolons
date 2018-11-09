package eidolons.libgdx.texture;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.data.filesys.PathFinder;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by PC on 03.11.2016.
 */
public class TexturePackerLaunch {
    public static final String ATLAS_EXTENSION = ".txt";
    public static final String WORKSPACE_PATH_POTIONS = PathFinder.getImagePath() +
     PathFinder.getPotionsAnimPath() + "processing//";

    public static   String WORKSPACE_PATH = PathFinder.getImagePath() + PathFinder.getWeaponAnimPath() + "workspace//";
    public static final boolean TRIM = true;
    public static final boolean POTIONS = false;
    private static final String OUTPUT_DIR = PathFinder.getImagePath() +
     PathFinder.getWeaponAnimPath() + "atlas//";
    private static final String OUTPUT_DIR_POTION = PathFinder.getImagePath() +
     PathFinder.getPotionsAnimPath() + "atlas//";

    public static final String TEST_WEAPONS_WORKSPACE =
     "Y:\\[Eidolons demos]\\weapons3d workspace//";

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
     "Y:/[Eidolons demos]/Eidolons Halloween Demo Lite/resources/img/ui",
     //     "ui",
     //     "main",
    };

    static String mainFoldersExceptions[] = {
     "main/sprites",
    };

    static String cullFolders[] = {
      "Y:/[Eidolons demos]/weapons3d workspace/",
    }
    ;
    private static float cullPercentage=0.33f;

    public static void main(String[] args) {
        if (DialogMaster.confirm("Pack ui?")) {
            packImages(mainFolders);
            return;
        }
        if (DialogMaster.confirm("Cull?")) {
            cullImages(cullPercentage, true, cullFolders);
            return;
        }
        String[] chosen = packs;
        GuiManager.init();
        if (CoreEngine.isExe() || CoreEngine.isJar())
            chosen =
             ListChooser.chooseFile(POTIONS ? WORKSPACE_PATH_POTIONS : WORKSPACE_PATH, null, SELECTION_MODE.MULTIPLE, true)
              .split(";");
        else if (DialogMaster.confirm("Pack all weapons?")) {
            chosen=null ;
        } else {
            chosen =
             ListChooser.chooseFile(POTIONS ? WORKSPACE_PATH_POTIONS : WORKSPACE_PATH, null, SELECTION_MODE.MULTIPLE, true)
              .split(";");
        }
        packWeaponSprites(chosen);
    }

    private static void cullImages( float percentage, boolean subdirs, String... folders ) {
        int denominator = Math.round(1 / percentage);
        for (String folder : folders) {
            int i = 0;
            for (File imageFile : FileManager.getFilesFromDirectory(folder, false, subdirs)) {
                if (!ImageManager.isImageFile(imageFile.getName()))
                    continue;
                if (i % denominator == 0)
                    imageFile.delete();

//                java.nio.file.Files.isSymbolicLink()
//                java.nio.file.Files.readSymbolicLink()
                i++;
            }
        }

    }

    private static void packImages(String[] folders) {
        Settings settings = getSetting();
        for (String sub : folders) {
            //            List<File> files = FileManager.getFilesFromDirectory(PathFinder.getImagePath() + sub, false, true);
            //            for (File folder : files) {
            String inputDir = null;
            //            Math.sqrt(files.size())
            if (sub.contains(":")) {
                inputDir = sub;
            } else {
                inputDir = PathFinder.getImagePath() + sub;
            }

            String outputDir = inputDir;
            String packFileName = StringMaster.getLastPathSegment(sub);

            TexturePacker.process(settings, inputDir, outputDir, packFileName);
            //            }
        }
    }

    private static Settings getSetting() {
        Settings settings = new Settings();
        settings.maxHeight = (int) Math.pow(2, 13);
        settings.maxWidth = (int) Math.pow(2, 13);
        settings.atlasExtension = ATLAS_EXTENSION;
        settings.stripWhitespaceY = TRIM;
        settings.stripWhitespaceX = TRIM;
        settings.square = false;
        settings.format = Format.RGBA4444;
        settings.limitMemory = false;
        settings.jpegQuality = 0.9f;
        settings.combineSubdirectories = false;
        return settings;
    }


    public static void packWeaponSprites(String[] args) {
        String outputDir = POTIONS ? OUTPUT_DIR_POTION : OUTPUT_DIR;
        if (args == null) {
            args =  FileManager.getFilesFromDirectory(TEST_WEAPONS_WORKSPACE, true).stream().map(
             file -> file.getName()). collect(Collectors.toList()) . toArray(new String[124]);
            WORKSPACE_PATH=TEST_WEAPONS_WORKSPACE;
            outputDir = TEST_WEAPONS_WORKSPACE + "atlas//";
        }
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

                pack(inputDir, outputDir, packFileName);
                processed = true;
            }
            if (!processed) {
                pack(dir, outputDir, sub);
            }
        }

    }

    public static void pack(String inputDir, String outputDir, String packFileName) {
        Settings settings = getSetting();
        TexturePacker.process(settings, inputDir, outputDir, packFileName);
    }

}

