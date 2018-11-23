package eidolons.libgdx.anims.construct;

import eidolons.entity.active.Spell;
import eidolons.libgdx.anims.AnimData.ANIM_VALUES;
import eidolons.libgdx.anims.construct.AnimConstructor.ANIM_PART;
import main.content.values.properties.PROPERTY;
import main.system.PathUtils;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 11/15/2018.
 */
public class AnimResourceFinder {
    private static boolean findClosestResource;

    public static String findResourceForSpell(Spell spell,
                                              String partPath, String size,
                                              PROPERTY[] props, String pathRoot,
                                              boolean closest) {
        String path = PathUtils.buildPath(
         pathRoot, partPath);
        //        spell.getTargeting();
        String file = null;
        for (PROPERTY p : props) {
            String name = spell.getProperty(p);
            if (name.isEmpty()) continue;
            file = FileManager.findFirstFile(path, name, closest);
            if (file != null) {
                break;
            }
            name = spell.getProperty(p);
            file = FileManager.findFirstFile(path, name, closest);
            if (file != null) {
                break;
            }
            name = spell.getProperty(p) + " " + partPath + size;
            file = FileManager.findFirstFile(path, name, closest);
            if (file != null) {
                break;
            }
        }
        //        if (file != null || closest || isPartIgnored(partPath))

        if (CoreEngine.isJar())
            System.out.println(pathRoot + " root; file found " + file);
        return file;
        //        return findResourceForSpell(spell, partPath, size, props, pathRoot, true);
    }

    public static boolean isFindClosestResource(ANIM_PART part, ANIM_VALUES val,
                                                int partsCount) {

        if (part != ANIM_PART.PRECAST)
            if (part != ANIM_PART.AFTEREFFECT)
                if (partsCount < 2)
                    return true;

        switch (part) {
            case MAIN:
                if (val == ANIM_VALUES.PARTICLE_EFFECTS) {
                    return true;
                }
        }
        return findClosestResource;
    }
}
