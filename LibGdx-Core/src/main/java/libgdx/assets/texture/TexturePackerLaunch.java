package libgdx.assets.texture;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import eidolons.content.consts.libgdx.GdxStringUtils;
import libgdx.assets.AssetEnums;
import main.data.filesys.PathFinder;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.PathUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.launch.Flags;
import main.system.util.DialogMaster;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PC on 03.11.2016.
 */
public class TexturePackerLaunch {
    public static boolean FAST = false;

    public static String WORKSPACE_PATH = PathFinder.getImagePath() + PathFinder.getWeaponAnimPath() + "workspace//";

    public static final String WORKSPACE_PATH_POTIONS = PathFinder.getImagePath() +
            PathFinder.getPotionsAnimPath() + "processing//";
    public static final boolean POTIONS = false;

    private static final String OUTPUT_DIR = PathFinder.getImagePath() +
            PathFinder.getWeaponAnimPath() + "atlas//";
    private static final String OUTPUT_DIR_POTION = PathFinder.getImagePath() +
            PathFinder.getPotionsAnimPath() + "atlas//";

    static String[] packs = {
            "hammers", "great_swords", "daggers", "maces", "axe",
    };

    private static Settings settings;
    private static boolean atlasGen;
    private static AssetEnums.ATLAS a;
    private static String OUTPUT = "Output to? (cancel if same path)";
    private static Map<String, Object> defaultOptions = new HashMap<>();

    public static void main(String[] args) {
        if (confirm("Single weapon atk atlases?")) {
            singleWeapons();
            return;
        }
        if (confirm("Gen Atlases?")) {
            generateAtlases();
            return;
        }
        if (confirm("Custom pack?")) {
            customPack();
            return;
        }
        String[] chosen;
        GuiManager.init();
        if (Flags.isExe() || Flags.isJar())
            chosen =
                    ListChooser.chooseFile(POTIONS ? WORKSPACE_PATH_POTIONS : WORKSPACE_PATH, null, SELECTION_MODE.MULTIPLE, true)
                            .split(";");
        else if (confirm("Pack all weapons?")) {
            chosen = null;
        } else {
            chosen =
                    ListChooser.chooseFile(POTIONS ? WORKSPACE_PATH_POTIONS : WORKSPACE_PATH, null, SELECTION_MODE.MULTIPLE, true)
                            .split(";");
        }
        packWeaponSprites(chosen);
    }


    public static final AssetEnums.ATLAS[] atlasesFull = {
            AssetEnums.ATLAS.UI_BASE,
            AssetEnums.ATLAS.UI_DC,
            AssetEnums.ATLAS.UNIT_VIEW,
            AssetEnums.ATLAS.TEXTURES,
            AssetEnums.ATLAS.SPRITES_GRID,
            AssetEnums.ATLAS.SPRITES_UI,
            AssetEnums.ATLAS.SPRITES_ONEFRAME,
    };
    public static final AssetEnums.ATLAS[] atlasesFast = {
            AssetEnums.ATLAS.UI_BASE,
            AssetEnums.ATLAS.UI_DC,
            AssetEnums.ATLAS.UNIT_VIEW,
            AssetEnums.ATLAS.TEXTURES,
            AssetEnums.ATLAS.SPRITES_ONEFRAME,
    };

    public static final AssetEnums.ATLAS[] atlases = {
            // AssetEnums.ATLAS.UI_BASE,
            // AssetEnums.ATLAS.UI_DC,
            AssetEnums.ATLAS.UNIT_VIEW,
            // AssetEnums.ATLAS.TEXTURES,
            // AssetEnums.ATLAS.SPRITES_GRID,
            // AssetEnums.ATLAS.SPRITES_UI,
            // AssetEnums.ATLAS.SPRITES_ONEFRAME,
    };

    public static void generateAtlases() {
        generateAtlases(false);
    }

    public static void generateAtlases(boolean full) {
        atlasGen = true;
        for (AssetEnums.ATLAS atlas : full ? FAST ? atlasesFast : atlasesFull : atlases) {
            a = atlas;
            String name = atlas.toString().toLowerCase();
            String input = PathFinder.getAtlasGenPath() + name;
            pack(input, input, name);
        }
    }

    public static Settings getWorstSettings() {
        Settings settings = getSettings();
        //        settings.format = Format.RGBA8888;
        settings.format = Format.RGBA4444;
        settings.jpegQuality = 0.55f;
        if (confirm("Jpg?")) {
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
        settings.format = Format.RGBA8888; //can we do w/o alpha some? or low quality?
        settings.jpegQuality = 0.8f;

        settings.maxHeight = (int) Math.pow(2, 13);
        settings.maxWidth = (int) Math.pow(2, 13);
        boolean indices = false;
        if (a != null) {
            switch (a) {
                case UI_DC:
                    // indices = true;
                    break;
                case SPRITES_GRID:
                case SPRITES_UI:
                    indices = true;
                    break;
            }
        }
        settings.useIndexes = indices;
        return settings;
    }

    public static Settings getSettings() {
        if (settings != null)
            return settings;
        settings = new Settings();
        settings.combineSubdirectories = atlasGen || confirm("Is combine Subdirectories ?");

        //        Float f = new Float(inputInt("Scale?", 100)) / 100;
        //        if (f != 0) {
        //            settings.scale = new float[]{f};
        //        }

        settings.maxHeight = (int) Math.pow(2, 12);
        settings.maxWidth = (int) Math.pow(2, 12);
        //        settings.maxHeight = (int) Math.pow(2, 13);
        //        settings.maxWidth = (int) Math.pow(2, 13);
        settings.atlasExtension = GdxStringUtils.ATLAS_EXTENSION;
        boolean TRIM = false;
        if (!atlasGen)
            TRIM = confirm("Trip empty space?");
        else {
            // TRIM = Atlases.isTrim(f)
        }
        settings.stripWhitespaceY = TRIM;
        settings.stripWhitespaceX = TRIM;
        settings.square = false;
        settings.format = Format.RGBA8888;
        // settings.limitMemory = false;
        settings.jpegQuality = 0.7f;
        if (!atlasGen)
            if (confirm("Jpg?")) {
                settings.outputFormat = "jpg";
                settings.format = Format.RGB888;
            }
        return settings;
    }


    public static void packWeaponSprites(String[] args) {
        String outputDir = POTIONS ? OUTPUT_DIR_POTION : OUTPUT_DIR;

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
        boolean bool = confirm("Half scale?");
        if (bool) {
            settings.scale = new float[]{
                    0.5f
            };
        }
        bool = confirm("Low quality?");
        if (bool) {
            settings.format = Format.RGBA4444;
        }


    }

    public static void pack(String inputDir, String outputDir, String packFileName) {
        Settings settings = getSettings();
        if (atlasGen) {
            settings = getAtlasSettings();
        } else if (confirm("Customize Settings?")) {
            customizeSettings(settings);
        } else {
            settings =
                    confirm("Best settings?") ? getBestSettings() :
                            confirm("Worst settings?") ? getWorstSettings() :
                                    getSettings();
        }
        TexturePacker.process(settings, inputDir, outputDir, packFileName);
    }


    private static void singleWeapons() {
        /*
        run itself multiple times ...
        with subfolders as separate atlases !

         */
        String root = "C:\\Aphos\\resources\\img\\sprite\\atk";
        defaultOptions.put("atlas name?", "");
        defaultOptions.put("Best settings?", true);
        defaultOptions.put("Customize Settings?", false);
        defaultOptions.put("Is combine Subdirectories ?", false);
        defaultOptions.put("Trip empty space?", true);
        defaultOptions.put("Jpg?", false);
        List<File> files = FileManager.getFilesFromDirectory(root, true, true);
        for (File file : files) {
            if (file.isDirectory())
            if (FileManager.isLeafDirectory(file)) //subdir packing could be useful, to load whole of Blades e.g.
            {
                defaultOptions.put("Folder path to pack?", file.getAbsolutePath());
                defaultOptions.put(OUTPUT, file.getAbsolutePath());
                customPack();
            }
        }
    }

    private static void customPack() {
        String inputDir = inputText("Folder path to pack?", PathFinder.getSpritesPath());
        String outputDir = inputText(OUTPUT, inputDir);
        if (outputDir == null) {
            outputDir = inputDir;
        }
        String name = inputText("atlas name?", "");
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

    private static boolean confirm(String query) {
        Object o = defaultOptions.get(query);
        if (o != null)
            return (boolean) o;
        return DialogMaster.confirm(query);
    }

    private static String inputText(String query, String initial) {
        Object o = defaultOptions.get(query);
        if (o != null)
            return o.toString();
        return DialogMaster.inputText(query, initial);
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

}

