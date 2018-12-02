package eidolons.libgdx.particles.spell;

import main.system.PathUtils;
import main.system.auxiliary.Loop;
import main.system.auxiliary.data.FileManager;

import java.io.File;

/**
 * Created by JustMe on 11/14/2018.
 */
public class SpellVfxMaster {
    public static final boolean SAME_FOLDER_RANDOM_VFX = true;


    public static final boolean TEST_MODE = true;
    public static final java.lang.String VFX_TEST_SPELLS = "Spurt of Flame;Arcane Flame;Shadow Flame;Shadow Bolt;Shock Grasp;Ray of Arcanum;";

    public static String getRandomVfx(String path) {
        String folder = PathUtils.cropLastPathSegment(path);
        Loop loop = new Loop(30);
        while (loop.continues()) {
            File file = FileManager.getRandomFile(folder);
            if (file != null) {
                return file.getPath();
            }
        }
        return path;
    }

    public static boolean isRandomVfx() {
        return false;
    }

}
