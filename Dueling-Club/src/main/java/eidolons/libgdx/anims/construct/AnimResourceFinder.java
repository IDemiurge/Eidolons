package eidolons.libgdx.anims.construct;

import eidolons.content.PROPS;
import eidolons.entity.active.Spell;
import eidolons.libgdx.anims.AnimData.ANIM_VALUES;
import eidolons.libgdx.anims.AnimEnums;
import eidolons.libgdx.anims.AnimEnums.ANIM_PART;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.system.PathUtils;
import main.system.auxiliary.data.FileManager;
import main.system.launch.Flags;

/**
 * Created by JustMe on 11/15/2018.
 */
public class AnimResourceFinder {
    public static final PROPERTY[] propsExact = {
     G_PROPS.NAME, G_PROPS.SPELL_SUBGROUP,
     G_PROPS.SPELL_GROUP, G_PROPS.ASPECT,
    };
    public static final PROPERTY[] propsGeneral = {
     PROPS.DAMAGE_TYPE,
     G_PROPS.SPELL_TYPE,
    };
    private static boolean findClosestResource;

    public static String findResourceForSpell(Spell spell,
                                              String partPath, String size,
                                              boolean exact, String pathRoot,
                                              boolean closest) {
        PROPERTY[] props = exact ? propsExact : propsGeneral;

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

        if (Flags.isJar())
            System.out.println(pathRoot + " root; file found " + file);
        return file;
        //        return findResourceForSpell(spell, partPath, size, props, pathRoot, true);
    }

    public static boolean isFindClosestResource(ANIM_PART part, ANIM_VALUES val,
                                                int partsCount) {

        if (part != AnimEnums.ANIM_PART.PRECAST)
            if (part != AnimEnums.ANIM_PART.AFTEREFFECT)
                if (partsCount < 2)
                    return true;

        switch (part) {
            case MISSILE:
                if (val == ANIM_VALUES.PARTICLE_EFFECTS) {
                    return true;
                }
        }
        return findClosestResource;
    }
}
