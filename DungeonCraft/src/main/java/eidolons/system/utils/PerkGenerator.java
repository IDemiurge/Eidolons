package eidolons.system.utils;

import com.badlogic.gdx.graphics.Texture;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.DC_Engine;
import libgdx.GDX;
import libgdx.GdxImageMaster;
import eidolons.content.consts.Images;
import libgdx.texture.TextureCache;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.HeroEnums.CLASS_GROUP;
import main.content.enums.entity.HeroEnums.CLASS_PERK_GROUP;
import main.content.enums.entity.HeroEnums.PERK_PARAM;
import main.content.values.parameters.G_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Writer;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;
import main.system.images.ImageManager;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/*
Double perks - combine two !


 */
public class PerkGenerator {
    private static final int PERK_LEVELS = 3;

    public static void main(String[] args) {
        generatePerks();
        applyIconFolders();
//        if (CoreEngine.isIggDemo()){
//            return
//        }
    }

    private static void applyIconFolders() {
        String root = "C:\\eidolons\\Eidolons\\resources\\res\\img\\gen\\perk\\full";
        for (File folder : FileManager.getFilesFromDirectory(root, true)) {
            if (folder.isDirectory()){
                List<File> icons = FileManager.getFilesFromDirectory(folder.getPath(), false);
                if (icons.isEmpty()) {
                    continue;
                }
                List<ObjType> types = DataManager.getTypes(DC_TYPE.PERKS).stream().filter(
                        t -> t.getName().contains(folder.getName())).collect(Collectors.toList());
                for (ObjType type : types) {
                    File icon =
                            icons.size() > 1 ?
                            icons.remove(RandomWizard.getRandomIndex(icons)) :
                            icons.get(RandomWizard.getRandomIndex(icons)) ;
//                    String path = "gen/perk/" +type.getProperty(G_PROPS.PERK_GROUP) + "/" +type.getName()+".png";
//                    ResourceMaster.writeImage(icon.getPath(),PathFinder.getImagePath()+ path);
                    type.setImage(
                            icon.getPath().split("img")[1]);
//                            GdxImageMaster.cropImagePath(icon.getPath()));

                }
            }
        }
        for (ObjType type : DataManager.getTypes(DC_TYPE.PERKS)) {
            if (type.getIntParam("circle")>0)
                continue;
            new File(root + "/"+type.getName()).mkdirs();
        }

        XML_Writer.writeXML_ForTypeGroup(DC_TYPE.PERKS);
    }
        private static void createIconFolders() {
        String root = "C:\\eidolons\\Eidolons\\resources\\res\\img\\gen\\perk\\full";
        for (ObjType type : DataManager.getTypes(DC_TYPE.PERKS)) {
            if (type.getIntParam("circle")>0)
                continue;
            new File(root + "/"+type.getName()).mkdirs();
        }
    }
    public static void generatePerks() {
        DC_Engine.mainMenuInit();

/**
 * adjust descriptions
 * random icon from folder
 *
 *  unique perks
 *  perk for classes
 *  diff prop
 *
 * append to class description
 *
 */

//        for (ObjType type : DataManager.getTypes(DC_TYPE.PERKS)) {
//            type.setImage(img);
//        }

        generateParameterPerks();
        adjustTypes();
        XML_Writer.writeXML_ForTypeGroup(DC_TYPE.PERKS);
//                generateAbilityPerks();
//                generatePassivePerks();

    }


    private static void adjustTypes() {
        for (ObjType sub : DataManager.getTypes(DC_TYPE.CLASSES)) {
            CLASS_GROUP class_group =
             new EnumMaster<CLASS_GROUP>().retrieveEnumConst(CLASS_GROUP.class,
              sub.getProperty(G_PROPS.CLASS_GROUP));

            String string = sub.getName();

            if (sub.getIntParam(PARAMS.CIRCLE) > 0) {
                string = class_group
                 + "_" + string;
            } else {
                if (class_group == CLASS_GROUP.WIZARD)
                    string = "Wizard_Apprenctice";
                else
                    string = class_group.toString();
            }
            if (class_group == CLASS_GROUP.SORCERER)
                string = string.replace(class_group.toString(), "Apostate");

            CLASS_PERK_GROUP group = new EnumMaster<CLASS_PERK_GROUP>().
             retrieveEnumConst(CLASS_PERK_GROUP.class, string);
            if (group != null)
                sub.setProperty(PROPS.CLASS_PERK_GROUP, group.name());
            else {
            }
        }
        XML_Writer.writeXML_ForTypeGroup(DC_TYPE.CLASSES);
    }

    private static void generateParameterPerks() {
        for (int level = 0; level < PERK_LEVELS; level++) {
            for (PERK_PARAM sub : HeroEnums.PERK_PARAM.values()) {
                PARAMETER param = ContentValsManager.getPARAM(sub.name());
                ObjType type = new ObjType(getName(sub, level)
                 , DC_TYPE.PERKS);

                //                type.setParam(param, sub.values[level] + "");
                //                if (sub.percentage)
                //                    type.setProperty(G_PROPS.STD_BOOLS, STD_BOOLS.PERCENT_MOD.name());
                float amount = sub.values[level];
                String boni = param.getName() + "(" + amount;
                if (sub.percentage)
                    boni += "%";
                boni += ")";
                type.setProperty(PROPS.PARAMETER_BONUSES,
                 boni);
                type.setProperty(G_PROPS.DESCRIPTION,
                 getDescription(sub, amount));
                type.setProperty(PROPS.PERK_PARAM,
                 sub.toString());

                type.setProperty(G_PROPS.PERK_GROUP,
                 getName(sub, 0));
                type.setProperty(G_PROPS.PERK_PARAMS,
                 sub.name());
                type.setParam(G_PARAMS.PERK_LEVEL,
                 level);
                type.setParam(PARAMS.CIRCLE,
                 level);
                type.setImage(getImage(sub, level));
                type.setProperty(G_PROPS.GROUP, "Parameter");

                //TODO image
                DataManager.addType(type);
            }
        }
    }

    private static String getDescription(PERK_PARAM sub, float amount) {
        return "Increases hero's " +
         sub.getParam().getDisplayedName() + " by " +(int) amount;
    }

    private static void syncPerkImages() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.PERKS)) {
            String path = type.getImagePath();
            ObjType baseType = null;
            if (ImageManager.isImage(path)) {
                try {
                    baseType = DataManager.getType(type.getProperty(G_PROPS.BASE_TYPE), DC_TYPE.PERKS);
                    path =
                     baseType.getImagePath();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            type.setImage(path);
            String properPath = ImageManager.getValueIconsPath()+"/perks/gen/" +
            baseType.getName()+".png";

            GdxImageMaster.writeImage(GDX.file(PathFinder.getImagePath() +
             properPath), new Texture(GDX.file(PathFinder.getImagePath() +
             path)));
        }

    }

    private static String getImage(PERK_PARAM sub, int level) {
        //generate tiered via overlays


        PARAMETER relatedValue = ContentValsManager.getPARAM(sub.name());
        if (relatedValue != null) {
            String image = StrPathBuilder.build(PathFinder.getPerkImagePath(),
             relatedValue.getName() + ".png");

            if (TextureCache.isImage(image)) {
                return image;
            }
            String prefix = "attribute";
            switch (sub) {
                case STARTING_FOCUS:
                case FOCUS_REGEN:

                case ESSENCE_REGEN:
                case CARRYING_CAPACITY:
                case TOUGHNESS_REGEN:
                case DEFENSE:
                case SIGHT_RANGE:
                case QUICK_SLOTS:
                case ARMOR:
                case SNEAK_ATTACK_MOD:
            }
        }
        //mastery

        return Images.UNKNOWN_PERK;
    }

    private static String getName(PERK_PARAM sub, int level) {
        if (level == 0)
            return sub.name;
        return sub.name + " " + NumberUtils.getRoman(level + 1);
    }

    public enum PERK_ABILITY {
        Consume_Magic, Arcane_Fury, Mark_Sorcerer,
    }

    public enum PERK_PARAM_COMBO {
        //        MAGIC_RESISTANCES(5, 15, 30, false, "Relentless"),
        //        PHYSICAL_RESISTANCES(5, 15, 30, false, "Relentless"),

    }

    public enum PERK_TYPE {
        MASTERY, ATTRIBUTE, PARAMETER, ABILITY, PASSIVE,
        //
    }

}
