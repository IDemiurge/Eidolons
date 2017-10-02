package main.libgdx.texture;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import main.data.filesys.PathFinder;

/**
 * Created by PC on 03.11.2016.
 */
public class TexturePackerLaunch {
    public static final String ATLAS_EXTENSION = ".txt";
    public static final boolean TRIM =true ;
    static String packs[] = {
//     "long swords",
//     "hammers",
//     "daggers",
//     "fists",
     "short swords",
//     "test",
//     "crossbows",
//     "bows",
//     "bolts",
//     "arrows",
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
        packWeaponSprites(packs);
    }

    private static void packImages(String[] folders) {
        Settings settings =getSetting();
        for (String sub : folders) {
//            List<File> files = FileManager.getFilesFromDirectory(PathFinder.getImagePath() + sub, false, true);
//            for (File folder : files) {
                String inputDir = PathFinder.getImagePath() + sub;
//            Math.sqrt(files.size())
                String outputDir =inputDir;
                String packFileName = sub  ;

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
        return settings;
    }
    public static void packWeaponSprites(String[] args) {
        Settings settings = getSetting();
        for (String sub : args) {

            String inputDir = PathFinder.getWeaponAnimPath() + "workspace//" + sub;
//            Math.sqrt(files.size())
            String outputDir = PathFinder.getWeaponAnimPath() + "atlas//";
            String packFileName = sub  ;

            TexturePacker.process(settings, inputDir, outputDir, packFileName);
        }
//            TexturePacker.process(inputDir, outputDir, packFileName);
    }

}

