package libgdx.particles.spell;

import main.content.enums.GenericEnums;
import main.data.ability.construct.VariableManager;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
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

    public static  String checkAlias(String value) {
        String res = "";
        for (String substring : ContainerUtils.openContainer(value)) {
            if (PathUtils.getPathSegments(substring).size() == 1) {
                String speed = VariableManager.getVar(substring);
                substring = VariableManager.removeVarPart(substring);
                GenericEnums.VFX vfx = new EnumMaster<GenericEnums.VFX>().retrieveEnumConst(GenericEnums.VFX.class, substring);
                if (vfx != null) {
                    if (!speed.isEmpty()) {
                        res += vfx.getPath() + "(" +speed +");";
                    } else
                        res += vfx.getPath() + ";";
                }
            } else
                res += substring + ";";
        }
        return res;
    }
}
