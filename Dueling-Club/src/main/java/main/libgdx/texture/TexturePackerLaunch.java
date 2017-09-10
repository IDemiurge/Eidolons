package main.libgdx.texture;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import main.data.filesys.PathFinder;

/**
 * Created by PC on 03.11.2016.
 */
public class TexturePackerLaunch {
    static String packs[] = {
     "long swords",
     "hammers",
     "daggers",
     "fists",
    };

    public static void main(String[] args) {
        packWeaponSprites(packs);
    }

    public static void packWeaponSprites(String[] args) {
        Settings settings = new Settings();
        settings.maxHeight = (int) Math.pow(2, 14);
        settings.maxWidth = (int) Math.pow(2, 14);
//        settings.stripWhitespaceY = true;
//        settings.stripWhitespaceX = true;
        settings.square = false;

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

