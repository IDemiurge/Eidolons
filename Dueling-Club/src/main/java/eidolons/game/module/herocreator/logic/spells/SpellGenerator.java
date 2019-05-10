package eidolons.game.module.herocreator.logic.spells;

import main.content.DC_TYPE;
import main.content.enums.entity.SpellEnums;
import main.content.enums.entity.SpellEnums.SPELL_UPGRADE;
import main.content.enums.system.MetaEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.images.ImageManager;

import java.util.List;

/*
 * makes me wonder if it wouldn't be cool to have generators for most types including units and encounters ;) 
 * So much control and diversity...
 */
public class SpellGenerator {
    /*
     * It would be easy to generate proper Meta-Versions for spells, the
     * questions are: 1) How to display them? 2) How to use them?
     */
    @Deprecated
    public static void init() {
        try {
            // generateSpellUpgrades();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            LogMaster.log(1, "*** Failed to generate spell upgrades!");
        }
    }

    public static void generateSpellUpgrades() {
        for (ObjType t : DataManager.getTypes(DC_TYPE.SPELLS)) {
            List<String> groups = ContainerUtils.openContainer(t
             .getProperty(G_PROPS.SPELL_UPGRADE_GROUPS));
            // ++ spell_upgrade_modification_exceptions
            if (groups.isEmpty()) {
                continue;
            }
            for (SPELL_UPGRADE ug : SpellEnums.SPELL_UPGRADE.values()) {

                if (!groups.contains(StringMaster.getWellFormattedString(ug.toString()))) {
                    continue;
                }
                try {
                    generateUpgradedVersion(t, ug);
                } catch (Exception e) {
                    // main.system.ExceptionMaster.printStackTrace(e);
                    LogMaster.log(0, t.getName()
                     + " - failed to generate spell upgrade: " + ug.toString());
                }
            }
        }
        /*
         * let's have a dummy Spell Type per UG! Use its fields...
		 */

    }

    //TODO support varargs! SPELL_UPGRADE...
    public static ObjType generateUpgradedVersion(ObjType t, SPELL_UPGRADE ug) {
        ObjType type = getNewType(t);
        SpellUpgradeMaster.applyUpgrade(type, ug);
        type.setImage(generateImagePath(type, ug));
        type.setGroup("Upgrade", false);
        type.setProperty(G_PROPS.WORKSPACE_GROUP, MetaEnums.WORKSPACE_GROUP.POLISH + ""); // TODO
        DataManager.addType(type);
        return type;
    }

    public static String generateName(Entity type) {
        String suffix = ": " + type.getProperty(G_PROPS.SPELL_UPGRADE_GROUPS).replace(";", ", ");
        suffix = suffix.substring(0, suffix.length() - 2);
        return type.getName() + suffix;
    }

    public static String generateImagePath(Entity type, SPELL_UPGRADE ug) {
        String imgPath = StringMaster.cropFormat(type.getImagePath()) + ug.getImgSuffix()
         + StringMaster.getFormat(type.getImagePath());
        if (!ImageManager.isImage(imgPath)) {
            return type.getImagePath();
        }
        return imgPath;
    }

    public static ObjType getNewType(ObjType type) {
        ObjType newType = new ObjType(type);
        type.getGame().initType(newType);
        newType.setGenerated(true);

        newType.setProperty(G_PROPS.BASE_TYPE, type.getName());
        return newType;
    }

}