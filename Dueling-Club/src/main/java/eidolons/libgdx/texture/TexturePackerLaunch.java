package eidolons.libgdx.texture;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.data.filesys.PathFinder;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.PathUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.launch.Flags;

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

    public static String WORKSPACE_PATH = PathFinder.getImagePath() + PathFinder.getWeaponAnimPath() + "workspace//";


    public static final boolean POTIONS = false;
    private static final String OUTPUT_DIR = PathFinder.getImagePath() +
            PathFinder.getWeaponAnimPath() + "atlas//";
    private static final String OUTPUT_DIR_POTION = PathFinder.getImagePath() +
            PathFinder.getPotionsAnimPath() + "atlas//";

    public static final String TEST_WEAPONS_WORKSPACE =
            "Y:\\[Eidolons demos]\\weapons3d workspace//";
    public static final String TEST_WEAPONS_OUTPUT =
            "Y:\\[Eidolons demos]\\weapons3d lite//";

    static String[] packs = {
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

    static String[] mainFolders = {
            //            "", //root
            //     "gen",
            "ui",
            //                 "main",
    };

    static String[] mainFoldersExceptions = {
            "main/sprites",
    };

    static String[] cullFolders = {
            "Y:/[Eidolons demos]/weapons3d workspace/",
    };
    private static final float cullPercentage = 0.33f;
    private static Settings settings;
    private static boolean atlasGen;

    public static void main(String[] args) {
        if (DialogMaster.confirm("Gen Atlases?")) {
            generateAtlases();
            return;
        }
        if (DialogMaster.confirm("Custom pack?")) {
            customPack();
            return;
        }
        if (DialogMaster.confirm("Pack all?")) {
            packImages(mainFolders);
            return;
        }
        if (DialogMaster.confirm("Cull?")) {
            cullImages(cullPercentage, true, cullFolders);
            return;
        }
        String[] chosen = packs;
        GuiManager.init();
        if (Flags.isExe() || Flags.isJar())
            chosen =
                    ListChooser.chooseFile(POTIONS ? WORKSPACE_PATH_POTIONS : WORKSPACE_PATH, null, SELECTION_MODE.MULTIPLE, true)
                            .split(";");
        else if (DialogMaster.confirm("Pack all weapons?")) {
            chosen = null;
        } else {
            chosen =
                    ListChooser.chooseFile(POTIONS ? WORKSPACE_PATH_POTIONS : WORKSPACE_PATH, null, SELECTION_MODE.MULTIPLE, true)
                            .split(";");
        }
        packWeaponSprites(chosen);
    }


    private static void cullImages(float percentage, boolean subdirs, String... folders) {
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
        Settings settings = getSettings();
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
            String packFileName = PathUtils.getLastPathSegment(sub);

            TexturePacker.process(settings, inputDir, outputDir, packFileName);
            //            }
        }
    }

    public static final String[] atlases = {
            // "ui",
            "grid/main",
    };

    private static void generateAtlases() {
        atlasGen = true;
        for (String name : atlases) {
            String input = PathFinder.getAtlasImgPath() + name;
            String output = PathFinder.getAtlasGenPath();
            pack(input, output, name);
        }
    }

    public static Settings getWorstSettings() {
        Settings settings = getSettings();
        //        settings.format = Format.RGBA8888;
        settings.format = Format.RGBA4444;
        settings.jpegQuality = 0.55f;
        if (DialogMaster.confirm("Jpg?")) {
            Integer i = DialogMaster.inputInt("Quality?", (int) (settings.jpegQuality * 100));
            if (i != null) {
                settings.jpegQuality = new Float(i) / 100;
            }
            settings.format = Format.RGB888;
            settings.outputFormat = "jpg";
        }

        settings.maxHeight = (int) Math.pow(2, 14);
        settings.maxWidth = (int) Math.pow(2, 14);
        return settings;
    }

    public static Settings getBestSettings() {
        Settings settings = getSettings();
        settings.format = Format.RGBA8888;
        settings.jpegQuality = 0.85f;

        settings.maxHeight = (int) Math.pow(2, 12);
        settings.maxWidth = (int) Math.pow(2, 12);
        return settings;
    }

    public static Settings getAtlasSettings() {
        Settings settings = getSettings();
        settings.format = Format.RGBA8888;
        settings.jpegQuality = 0.8f;

        settings.maxHeight = (int) Math.pow(2, 13);
        settings.maxWidth = (int) Math.pow(2, 14);
        return settings;
    }

    public static Settings getSettings() {
        if (settings != null)
            return settings;
        settings = new Settings();
        settings.combineSubdirectories = atlasGen || DialogMaster.confirm("Is combine Subdirectories ?");

        //        Float f = new Float(DialogMaster.inputInt("Scale?", 100)) / 100;
        //        if (f != 0) {
        //            settings.scale = new float[]{f};
        //        }

        settings.maxHeight = (int) Math.pow(2, 12);
        settings.maxWidth = (int) Math.pow(2, 12);
        //        settings.maxHeight = (int) Math.pow(2, 13);
        //        settings.maxWidth = (int) Math.pow(2, 13);
        settings.atlasExtension = ATLAS_EXTENSION;
        boolean TRIM = false;
        if (!atlasGen)
            TRIM = DialogMaster.confirm("Trip empty space?");
        settings.stripWhitespaceY = TRIM;
        settings.stripWhitespaceX = TRIM;
        settings.square = false;
        settings.format = Format.RGBA8888;
        // settings.limitMemory = false;
        settings.jpegQuality = 0.7f;
        if (!atlasGen)
            if (DialogMaster.confirm("Jpg?")) {
                settings.outputFormat = "jpg";
                settings.format = Format.RGB888;
            }
        return settings;
    }


    public static void packWeaponSprites(String[] args) {
        String outputDir = POTIONS ? OUTPUT_DIR_POTION : OUTPUT_DIR;
        if (args == null) {
            args = FileManager.getFilesFromDirectory(TEST_WEAPONS_WORKSPACE, true).stream().map(
                    file -> file.getName()).collect(Collectors.toList()).toArray(new String[124]);
            WORKSPACE_PATH = TEST_WEAPONS_WORKSPACE;
            outputDir = TEST_WEAPONS_OUTPUT;
        }
        for (String sub : args) {
            String dir = POTIONS ? WORKSPACE_PATH_POTIONS : WORKSPACE_PATH;
            dir = FileManager.formatPath(dir, true);
            List<File> subFolders = FileManager.getFilesFromDirectory(dir + sub, true);
            boolean processed = false;
            for (File subFolder : subFolders) {
                if (!subFolder.isDirectory())
                    continue;
                String inputDir = subFolder.getPath();
                String packFileName = subFolder.getName();
                inputDir = FileManager.formatPath(inputDir, true);
                String suffix = inputDir.replace(dir, "");
                pack(inputDir, outputDir + "/" + suffix, packFileName);
                processed = true;
            }
            if (!processed) {
                pack(dir, outputDir, sub);
            }
        }

    }

    private static void customizeSettings(Settings settings) {
        boolean bool = DialogMaster.confirm("Half scale?");
        if (bool) {
            settings.scale = new float[]{
                    0.5f
            };
        }
        bool = DialogMaster.confirm("Low quality?");
        if (bool) {
            settings.format = Format.RGBA4444;
        }


    }

    public static void pack(String inputDir, String outputDir, String packFileName) {
        Settings settings = getSettings();
        if (atlasGen)
            settings = getBestSettings();
        else if (DialogMaster.confirm("Customize Settings?")) {
            customizeSettings(settings);
        } else {
            settings =
                    DialogMaster.confirm("Best settings?") ? getBestSettings() :
                            DialogMaster.confirm("Worst settings?") ? getWorstSettings() :
                                    getSettings();
        }
        TexturePacker.process(settings, inputDir, outputDir, packFileName);
    }

    private static void customPack() {
        String inputDir = DialogMaster.inputText("Folder path to pack?", PathFinder.getSpritesPath());
        String outputDir = DialogMaster.inputText("Output to? (cancel if same path)", inputDir);
        if (outputDir == null) {
            outputDir = inputDir;
        }
        String name = DialogMaster.inputText(
                "atlas name?",
                "");
        if (StringMaster.isEmpty(name)) {
            name = PathUtils.getLastPathSegment(inputDir);
            if (name.endsWith("0")) {
                name = StringMaster.cropLastSegment(name, "_", true);
            }
        }
        String packFileName = FileManager.getUniqueFileVersion(name, outputDir);
        pack(inputDir, outputDir, packFileName); //+"/"+suffix
    }


    public static void pack(String inputDir, String outputDir, String packFileName, Settings settings) {
        TexturePacker.process(settings, inputDir, outputDir, packFileName);
    }
}

