package eidolons.system.utils.content;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.DescriptionMaster;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.content.consts.libgdx.GdxStringUtils;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.exploration.dungeon.objects.InteractiveObjMaster;
import eidolons.content.consts.Images;
import eidolons.system.utils.file.ResourceMaster;
import main.content.CONTENT_CONSTS.OBJECT_ARMOR_TYPE;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.GenericEnums.RESIST_GRADE;
import main.content.enums.GenericEnums.STD_BOOLS;
import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.BfObjEnums.BF_OBJECT_GROUP;
import main.content.enums.entity.BfObjEnums.BF_OBJECT_TYPE;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.HeroEnums.SUBRACE;
import main.content.enums.entity.HeroEnums.GENDER;
import main.content.enums.entity.HeroEnums.RACE;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.*;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.CLASSIFICATIONS;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.PLACE_SUBTYPE;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.PLACE_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Reader;
import main.entity.EntityCheckMaster;
import main.entity.type.ObjType;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.images.ImageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.content.enums.entity.ActionEnums.WEAPON_ATTACKS.*;

public class ContentGenerator {

    public static final String SPELL_TESTED = "Shadow Fury";
    private static final boolean OVERWRITE_DESCR = false;
    private static final float ESS_COST_COEF = 0.5f;
    private static final float ESS_COST_SD_COEF = 0.7f;

    static PARAMS[] params = {PARAMS.TOUGHNESS, PARAMS.ENDURANCE, PARAMS.ARMOR,};
    static PROPERTY[] heroProps = {
            PROPS.SKILLS,
            PROPS.CLASSES,
            PROPS.PERKS,
            PROPS.INVENTORY,
    };

    public static String getTestSpellFilter(String name) {
        switch (name) {
            case "Grimbart":
                return "VFX";
            case "Gwynn":
                return "CAST";
            case "Raina Ardren":
                return "IMPACT";
            case "Gorr Eddar":
                return "MAIN";
        }
        return name;
    }

    public static void generateSpellVfxVariants() {
        generateSpellVfxVariants(SPELL_TESTED, PathFinder.getVfxPath() + "advanced/",
                PROPS.ANIM_MISSILE_VFX, PROPS.ANIM_VFX_CAST
                , PROPS.ANIM_VFX_IMPACT
                , PROPS.ANIM_VFX_MAIN
        );
    }

    public static void generateSpellVfxVariants(String baseName, String vfxFolder, PROPERTY... props) {
        ObjType type = DataManager.getType(baseName, DC_TYPE.SPELLS);
        generateSpellVfxVariants(type, vfxFolder, props);
    }

    public static void setRandomizeDefaultSkillIcons() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.SKILLS)) {
            File folder = new File(PathFinder.getImagePath() + "gen/skills/workshop/map/" + type.getProperty(G_PROPS.MASTERY));
            if (!folder.isDirectory()) {
                continue;
            }
            String imgPath = FileManager.getRandomFilePath(folder.getPath());
            if (ImageManager.isImage(
                    imgPath
            )) {
                type.setImage(imgPath);
            }
        }
    }

    public static void randomizeSkillIcons() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.SKILLS)) {
            File folder = new File(PathUtils.cropLastPathSegment(type.getImagePath()));
            if (!folder.isDirectory()) {
                continue;
            }
            String newImage = FileManager.getRandomFilePath(
                    folder.getPath());
            if (ImageManager.isImage(newImage)) {
                newImage = GdxStringUtils.cropImagePath(newImage);
                type.setImage(newImage);
            }
        }
    }

    public static void setDefaultSkillIcons() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.SKILLS)) {
            if (ImageManager.isImage(type.getImagePath())) {
                type.setImage(Images.getDefaultSkillImage(type.getProperty(G_PROPS.MASTERY)));
            }
        }

    }

    public static void generateSpellVfxVariants(ObjType type, String vfxFolder, PROPERTY... props) {
        String name = type.getName();

        for (File file : FileManager.getFilesFromDirectory(vfxFolder, false)) {
            if (props.length == 1) {
                String vfxPath = GdxStringUtils.cropImagePath(file.getPath());
                vfxPath = PathUtils.cropFirstPathSegment(vfxPath);
                name = type.getName() + " " + StringMaster.getLastPart(props[0].name(), "_") + " " + PathUtils.getLastPathSegment(vfxPath);
                ObjType newType = new ObjType(name, type);
                newType.setProperty(props[0], vfxPath);
                main.system.auxiliary.log.LogMaster.log(1, "Vfx spell new Type " + newType);
                DataManager.addType(newType);
                newType.setGenerated(false);
            } else {
                for (PROPERTY prop : props) {
//                    name = NameMaster.getUniqueVersionedName(name, type.getOBJ_TYPE_ENUM());
                    generateSpellVfxVariants(type, vfxFolder, prop);
                }
                return;
            }
        }

    }

    public static void generateUnitGroupsEnumsTxt() {
        String contents = "";
        loop:
        for (File file : FileManager.getFilesFromDirectory(PathFinder.getUnitGroupPath(), false, true)) {
            for (char c : file.getName().toCharArray()) {
                if (Character.isDigit(c))
                    continue loop;
            }
            contents += StringMaster.getEnumFormat(
                    StringMaster.getEnumFormatSaveCase(
                            StringMaster.cropFormat(file.getName()))) + ",\n";

        }
        String text = "public enum " +
                " {" + contents + ";\n}";
        System.out.println(text);

    }

    public static void main(String[] args) {
        DC_Engine.mainMenuInit();
        generateUnitGroupsEnumsTxt();
        generateTypeEnumsTxt(true, DC_TYPE.UNITS, DC_TYPE.BF_OBJ);

    }

    public static void generateTypeEnumsTxt(boolean perGroup, DC_TYPE... TYPES) {
        for (DC_TYPE TYPE : TYPES) {
            //auto find src path?
            String contents;
            Map<String, List<ObjType>> map = new XLinkedMap<>();
            Map<String, List<ObjType>> subMap = new XLinkedMap<>();
            StringBuilder contentsBuilder = new StringBuilder();
            for (ObjType type : DataManager.getTypes(TYPE)) {
                contentsBuilder.append(StringMaster.getEnumFormat(type.getName())).append(",\n");
                if (perGroup) {
                    MapMaster.addToListMap(subMap, type.getSubGroupingKey().toUpperCase(),
                            type);
                    MapMaster.addToListMap(map, type.getGroup().toUpperCase(),
                            type);
                }
            }
            contents = contentsBuilder.toString();
            String text = "public enum " + TYPE.name().toUpperCase() + "_TYPES" +
                    " implements OBJ_TYPE_ENUM {\n" + contents + ";\n}";
            System.out.println(text);
            for (String group : map.keySet()) {
                  contentsBuilder = new StringBuilder();
                for (ObjType type : map.get(group)) {
                    contentsBuilder.append(StringMaster.getEnumFormat(type.getName())).append(",\n");
                }
                contents = contentsBuilder.toString();
                text = "public enum  " + TYPE.name().toUpperCase() + "_TYPES_" +
                        StringMaster.toEnumFormat(group) +
                        " implements OBJ_TYPE_ENUM {\n" + contents + ";\n}";
                System.out.println(text);

            }
            for (String group : subMap.keySet()) {
                  contentsBuilder = new StringBuilder();
                for (ObjType type : subMap.get(group)) {
                    contentsBuilder.append(StringMaster.getEnumFormat(type.getName())).append(",\n");
                }
                contents = contentsBuilder.toString();
                text = "public enum  " + TYPE.name().toUpperCase() + "_SUB_TYPES_" +
                        StringMaster.toEnumFormat(group) +
                        " implements OBJ_TYPE_ENUM {\n" + contents + ";\n}";
                System.out.println(text);

            }

        }
    }

    public static void generatePlaces() {
        for (PLACE_SUBTYPE sub : PLACE_SUBTYPE.values()) {
            String name = StringMaster.format(sub.name());
            ObjType type = new ObjType(name, MACRO_OBJ_TYPES.PLACE);
            type.setProperty(MACRO_PROPS.DUNGEON_TYPES, getDUNGEON_TYPES(sub));
            type.setProperty(MACRO_PROPS.PLACE_TYPE, getPLACE_TYPE(sub));
            type.setProperty(MACRO_PROPS.PLACE_SUBTYPE, name);
            type.setProperty(MACRO_PROPS.MAP_ICON, "global/map/icons/places/" + name + ".png");
            type.setProperty(G_PROPS.IMAGE, "global/map/icons/places/preview/" + name + ".png");
            DataManager.addType(type);
            //            type = new ObjType(name+ " alt" ,type);
            //            DataManager.addType(type);
        }
    }

    public static void afterRead() {
        clearGenType();

        if (DataManager.isTypesRead(DC_TYPE.SKILLS)) {
//            setDefaultSkillIcons();
//            setRandomizeDefaultSkillIcons();
//            randomizeSkillIcons();
        }
        if (DataManager.isTypesRead(DC_TYPE.SPELLS))
            generateSpellVfxVariants();
        if (DataManager.isTypesRead(DC_TYPE.BF_OBJ))
            generateIndestructibleWalls();
        if (DataManager.isTypesRead(DC_TYPE.BF_OBJ))
            generateFalseWalls();
        if (DataManager.isTypesRead(DC_TYPE.SCENARIOS))
            generateRngScenarios();
//        updateImagePaths(DC_TYPE.SKILLS);
//        writeImages(DC_TYPE.SKILLS);
//            writeDataToText();
    }

    public static void generateKeyObjects() {
        List<ObjType> types = DataManager.getTypes(DC_TYPE.ITEMS);
        for (ObjType type : types) {

            if (type.getSubGroupingKey().equalsIgnoreCase("keys")) {
                String name = "Hanging " + type.getName();
                if (DataManager.isTypeName(name, DC_TYPE.BF_OBJ))
                    continue;

                ObjType baseType = DataManager.getType("dummy hung obj", DC_TYPE.BF_OBJ);
                ObjType newType = new ObjType(name, baseType);
                newType.setProperty(G_PROPS.BF_OBJECT_CLASS, "Key");
                newType.setGenerated(false);
                newType.setImage("main/bf/hanging/keys/" +
//                        type.getName()
                        "Jade Key" + ".png");
                DataManager.addType(newType);
            }

        }

    }

    private static void clearGenType() {
        for (ObjType type : new ArrayList<>(DataManager.getTypes(DC_TYPE.BF_OBJ))) {
            if (type.checkBool(STD_BOOLS.INDESTRUCTIBLE) ||
                    type.checkBool(STD_BOOLS.FAUX)) {
                if (type.getName().contains("Indestructible Indestructible")) {
                    DataManager.removeType(type);
                }
                if (type.getName().contains("Marked Indestructible")) {
                    DataManager.removeType(type);
                }
                if (type.getName().contains("Marked Marked")) {
                    DataManager.removeType(type);
                }
            }

        }
    }

    private static void writeImages(OBJ_TYPE... TYPES) {
        for (OBJ_TYPE T : TYPES) {
            for (ObjType type : DataManager.getTypes(T)) {
                String path = type.getImagePath();
                if (!ImageManager.isImage(PathFinder.getImagePath() + path)) {
                    continue;
                }
                if (path.contains("entity")) {
                    continue;
                }
                try {
                    String newPath = "gen/skills/" + type.getProperty(G_PROPS.MASTERY) + "/"
                            + type.getName() + ".png";
                    String newPath2 = "gen/skills/" + type.getProperty(G_PROPS.MASTERY) + "/2/"
                            + type.getName() + ".png";
                    newPath = FileManager.formatPath(newPath);
                    path = StringMaster.cropLast(FileManager.formatPath(path, true), 1);
                    if (path.equalsIgnoreCase(newPath))
                        continue;
                    type.setImage(newPath);
                    ResourceMaster.writeImage(path, newPath2, true);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        }
    }

    public static void updateImagePathsForJpg_Png() {
        /**
         * locks
         * mushroom
         * light emitters
         * inscriptions
         */
        for (ObjType type : DataManager.getTypes(DC_TYPE.BF_OBJ)) {

//            if (!EntityCheckMaster.isOverlaying(type)) {
//                continue;
//            }

            String folder = "";
            if (type.getName().toLowerCase().contains("fungi")) {
                folder = "fungi";
            } else if (
                    type.getGroup().equalsIgnoreCase("light emitters")) {
                folder = "light emitters";
            } else if (  type.getName().toLowerCase().contains("inscription")) {
                folder = "inscription";
            } else if ( type.getName().toLowerCase().contains("key")) {
                folder = "keys";
            }

            String name = PathUtils.getLastPathSegment(type.getImagePath());
            String newRoot = "sprites/bf/hanging/" + folder + "/" + name;
            if (!GdxStringUtils.isImage(newRoot)) {
                continue;
            }
            type.setImage(newRoot);
        }


    }

    private static void saveAllAsJPG() {

        for (ObjType type : DataManager.getTypes(DC_TYPE.BF_OBJ)) {
            if (EntityCheckMaster.isOverlaying(type)) {
                continue;
            }
            String path = type.getImagePath();
            String newPath= StringMaster.cropFormat(path)+".jpg";
            ResourceMaster.writeImage(path, newPath);

            type.setImage(newPath);
        }
        }

    private static void updateImagePaths(OBJ_TYPE... TYPES) {
        for (OBJ_TYPE T : TYPES) {
            for (ObjType type : DataManager.getTypes(T)) {
                String path = type.getImagePath();
                path = path.replace(StrPathBuilder.build("gen", "entity"), StrPathBuilder.build("main"));
                if (!ImageManager.isImage(path)) {
                    main.system.auxiliary.log.LogMaster.log(1, path + " - is not a valid img for " + type);
                    continue;
                }
                type.setImage(path);
            }
        }
    }

    private static void writeDataToText() {
//        DataManager.getTypeMap()
        for (String s : XML_Reader.getTypeMaps().keySet()) {
            for (ObjType t : XML_Reader.getTypeMaps().get(s).values()) {
                String data = t.getDescription();
                if (!data.isEmpty()) {
                    String old = DescriptionMaster.getDescription(t, false);
                    if (!old.isEmpty()) {
                        if (!OVERWRITE_DESCR) {
                            continue;
                        }
                    }
                    FileManager.write(data, DescriptionMaster.getDescriptionPath(t));
                }
            }
        }
    }

    public static void generateConsumableItemsFromOverlaying() {
        ObjType baseType = DataManager.getType("Consumable", DC_TYPE.ITEMS);
        for (ObjType type : DataManager.getTypes(
                DC_TYPE.BF_OBJ)) {
            if (!type.checkProperty(G_PROPS.BF_OBJECT_TAGS, BfObjEnums.BF_OBJECT_TAGS.CONSUMABLE.toString()))
                continue;
            ObjType newType = new ObjType(
                    InteractiveObjMaster.getConsumableItemName(type.getName()),
                    baseType);
            newType.setImage(type.getImagePath());
            DataManager.addType(newType);
            newType.setGenerated(false); //to save it!
        }
    }

    public static void generateFalseWalls() {
        for (ObjType type : DataManager.getTypesGroup(
                DC_TYPE.BF_OBJ, BF_OBJECT_GROUP.WALL.name())) {
            if (type.checkBool(STD_BOOLS.INDESTRUCTIBLE) ||
                    type.checkBool(STD_BOOLS.FAUX))
                continue;
            ObjType newType = new ObjType(type.getName() + StringMaster.wall(null), type);
            newType.addProperty(G_PROPS.STD_BOOLS, STD_BOOLS.FAUX.name());
            DataManager.addType(newType);
            newType.setGenerated(false);
        }
    }

    public static void generateIndestructibleWalls() {
        for (ObjType type : DataManager.getTypesGroup(DC_TYPE.BF_OBJ, BF_OBJECT_GROUP.WALL.name())) {

            if (type.checkBool(STD_BOOLS.INDESTRUCTIBLE) ||
                    type.checkBool(STD_BOOLS.FAUX)) {
            }
//            ObjType newType = new ObjType(type.getName() + WallMap.v(true), type);
//            newType.addProperty(G_PROPS.STD_BOOLS, STD_BOOLS.INVULNERABLE.name());
//            newType.addProperty(G_PROPS.STD_BOOLS, STD_BOOLS.INDESTRUCTIBLE.name());
//            DataManager.addType(newType);
//            newType.setGenerated(false);
        }
    }

    public static void generateRngScenarios() {
        for (LOCATION_TYPE type : LOCATION_TYPE.values()) {
            ObjType newType = new ObjType(StringMaster.format(type.name()),
                    DC_TYPE.SCENARIOS);
            newType.setGroup("Random", true);
            switch (type) {
                case CAVE:
                case CRYPT:

                default:
                    newType.setImage(Images.EMPTY_SPELL);
                    newType.setProperty(G_PROPS.FULLSIZE_IMAGE,
                            "demo/previews/Ironhelm Tunnel.png");
            }
            newType.setProperty(PROPS.SUBDUNGEON_TYPE,
                    type.toString());
            DataManager.addType(newType);
        }
    }

    private static String getPLACE_TYPE(PLACE_SUBTYPE sub) {
        switch (sub) {
            case CAMP:
                return PLACE_TYPE.LOCATION.name();
            case WIZARD_TOWER:
            case DARK_TOWER:
                return PLACE_TYPE.BUILDING.name();
        }
        return PLACE_TYPE.DUNGEON.name();
    }

    private static String getDUNGEON_TYPES(PLACE_SUBTYPE sub) {
        switch (sub) {
            //TODO dwarf
            case ELVEN_RUINS:
                return "elven";
            case PLACE_OF_POWER:
            case WIZARD_TOWER:
            case DARK_TOWER:
                return "arcane";
            case GARRISON:
            case TOWN:
            case INN:
            case HOUSE:
            case CASTLE:
            case TOWER:
            case VILLAGE:
                return "ravenguard";
            case MANSION:
                return "crypt";
            case DEN:
            case SPIDER_DEN:
                return "cavern";
            case CAMP:
                return "surface";
        }
        return sub.name();
    }


    public static void setAbilBaseTypes() {
        List<List<ObjType>> groups = new ArrayList<>();
        List<ObjType> group = new ArrayList<>();

    }

    public static void convertSkillStrings(ObjType type) {
        for (String s : ContainerUtils.open(type.getProp("passives"))) {
            switch (s) {
                case "ParamBonus":
                    type.addProperty(PROPS.PARAMETER_BONUSES, s);
                    break;
            }
        }

    }

    public static void initMaterials() {

        for (MATERIAL material : ItemEnums.MATERIAL.values()) {
            Map<DAMAGE_TYPE, RESIST_GRADE> map = getDefaultResistMap(material.getGroup());
            // setSpecials(material, map);
            map = getDefaultSelfDamageMap(material.getGroup());
            material.setResistGradeMap(map);
            material.setSelfDamageGradeMap(map);
            int hardness = getHardness(material);
            material.setHardness(hardness);
        }
    }

    private static int getHardness(MATERIAL material) {
        switch (material) {
            case COPPER:
                return 65;
            case ADAMANTIUM:
            case MITHRIL:
            case STEEL:
                return 115;
        }
        return getDefaultHardness(material.getGroup());
    }

    private static int getDefaultHardness(ITEM_MATERIAL_GROUP group) {
        switch (group) {
            case BONE:
                return 65;
            case CLOTH:
                return 15;
            case LEATHER:
                return 50;
            case METAL:
                return 85;
            case NATURAL:
                return 25;
            case STONE:
                return 100;
            case WOOD:
                return 35;
        }
        return 0;
    }

    public static void adjustSpellCosts(ObjType t) {
        /*
        focus == essence
        -50% on both?
         */

        Integer cost = t.getIntParam(PARAMS.ESS_COST);
        cost = Math.round(cost * ESS_COST_COEF);

        t.setParam(PARAMS.FOC_COST, cost);
        t.setParam(PARAMS.ESS_COST, cost);

        t.setParam(PARAMS.FOC_REQ, cost);

    }
    public static void generateSpellParams(ObjType t) {
        if (t.getName().contains(" Bolt")) {
            t.addProperty(G_PROPS.SPELL_TAGS, "Missile", true);
        }
        adjustSpellCosts(t);

    }

    public static void generateArmorParams(ObjType t) {

        Integer n = t.getIntParam(PARAMS.MATERIAL_QUANTITY);
        Integer layers = t.getIntParam(PARAMS.ARMOR_LAYERS);
        if (layers == 0) {
            layers = 1;
        } else {

            Integer durability_modifier = t.getIntParam(PARAMS.DURABILITY_MODIFIER);
            int i = durability_modifier / layers / 5;
            i *= 5;
            t.setParam(PARAMS.DURABILITY_MODIFIER, i);
            return;
        }
        if (n > 6) {
            layers = 2;
            n = n * 4 / 5;
        }
        if (n > 9) {
            layers = 3;
            n = n * 2 / 3;
        }
        int cover = 15 * n;
        t.setParam(PARAMS.COVER_PERCENTAGE, cover);
        t.setParam(PARAMS.MATERIAL_QUANTITY, n * layers);
        t.setParam(PARAMS.ARMOR_LAYERS, layers);
        // armor mod?

    }

    private static Map<DAMAGE_TYPE, RESIST_GRADE> getDefaultResistMap(ITEM_MATERIAL_GROUP group) {
        Map<DAMAGE_TYPE, RESIST_GRADE> map = new HashMap<>();
        for (DAMAGE_TYPE dmg_type : GenericEnums.DAMAGE_TYPE.values()) {
            if (!dmg_type.isMagical()) {
                map.put(dmg_type, GenericEnums.RESIST_GRADE.Normal);
            } else {
                map.put(dmg_type, GenericEnums.RESIST_GRADE.Ineffective);
            }
        }

        switch (group) {
            case BONE:
                map.put(GenericEnums.DAMAGE_TYPE.PIERCING, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.LIGHTNING, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.SONIC, GenericEnums.RESIST_GRADE.Resistant);
                break;
            case CLOTH:
            case LEATHER:
                map.put(GenericEnums.DAMAGE_TYPE.BLUDGEONING, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.LIGHTNING, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.SONIC, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.COLD, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.FIRE, GenericEnums.RESIST_GRADE.Resistant);
                break;
            case METAL:
            case STONE:
                map.put(GenericEnums.DAMAGE_TYPE.FIRE, GenericEnums.RESIST_GRADE.Resistant);
                break;

        }
        switch (group) {
            case BONE:
                map.put(GenericEnums.DAMAGE_TYPE.LIGHTNING, GenericEnums.RESIST_GRADE.Impregnable);
                break;
            case CLOTH:
                map.put(GenericEnums.DAMAGE_TYPE.PIERCING, GenericEnums.RESIST_GRADE.Vulnerable);
                break;
            case LEATHER:
                map.put(GenericEnums.DAMAGE_TYPE.FIRE, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.SONIC, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.SLASHING, GenericEnums.RESIST_GRADE.Vulnerable);
                break;
            case METAL:
                map.put(GenericEnums.DAMAGE_TYPE.SLASHING, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.BLUDGEONING, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.LIGHT, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.SONIC, GenericEnums.RESIST_GRADE.Vulnerable);
                map.put(GenericEnums.DAMAGE_TYPE.ACID, GenericEnums.RESIST_GRADE.Vulnerable);
                map.put(GenericEnums.DAMAGE_TYPE.COLD, GenericEnums.RESIST_GRADE.Vulnerable);
                map.put(GenericEnums.DAMAGE_TYPE.LIGHTNING, GenericEnums.RESIST_GRADE.Ineffective);
                break;
            case STONE:
                map.put(GenericEnums.DAMAGE_TYPE.LIGHTNING, GenericEnums.RESIST_GRADE.Impregnable);
                map.put(GenericEnums.DAMAGE_TYPE.PIERCING, GenericEnums.RESIST_GRADE.Resistant);
                break;
            case CRYSTAL:
                map.put(GenericEnums.DAMAGE_TYPE.FIRE, GenericEnums.RESIST_GRADE.Impregnable);
                map.put(GenericEnums.DAMAGE_TYPE.LIGHTNING, GenericEnums.RESIST_GRADE.Impregnable);
                map.put(GenericEnums.DAMAGE_TYPE.PIERCING, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.SLASHING, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.ACID, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.BLUDGEONING, GenericEnums.RESIST_GRADE.Vulnerable);
                map.put(GenericEnums.DAMAGE_TYPE.SONIC, GenericEnums.RESIST_GRADE.Vulnerable);
                map.put(GenericEnums.DAMAGE_TYPE.COLD, GenericEnums.RESIST_GRADE.Vulnerable);
                map.put(GenericEnums.DAMAGE_TYPE.LIGHT, GenericEnums.RESIST_GRADE.Ineffective);
                break;

        }
        // fill with 'normals'
        return map;
    }

    private static Map<DAMAGE_TYPE, RESIST_GRADE> getDefaultSelfDamageMap(ITEM_MATERIAL_GROUP group) {
        Map<DAMAGE_TYPE, RESIST_GRADE> map = new HashMap<>();
        for (DAMAGE_TYPE dmg_type : GenericEnums.DAMAGE_TYPE.values()) {
            if (!dmg_type.isMagical()) {
                map.put(dmg_type, GenericEnums.RESIST_GRADE.Normal);
            } else {
                map.put(dmg_type, GenericEnums.RESIST_GRADE.Ineffective);
            }
        }

        switch (group) {
            case BONE:
                map.put(GenericEnums.DAMAGE_TYPE.PIERCING, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.LIGHTNING, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.SONIC, GenericEnums.RESIST_GRADE.Resistant);
                break;
            case CLOTH:
            case LEATHER:
                map.put(GenericEnums.DAMAGE_TYPE.BLUDGEONING, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.LIGHTNING, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.SONIC, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.COLD, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.FIRE, GenericEnums.RESIST_GRADE.Resistant);
                break;
            case METAL:
            case STONE:
                map.put(GenericEnums.DAMAGE_TYPE.FIRE, GenericEnums.RESIST_GRADE.Resistant);
                break;
        }
        switch (group) {
            case BONE:
                map.put(GenericEnums.DAMAGE_TYPE.LIGHTNING, GenericEnums.RESIST_GRADE.Impregnable);
                break;
            case CLOTH:
                map.put(GenericEnums.DAMAGE_TYPE.PIERCING, GenericEnums.RESIST_GRADE.Vulnerable);
                break;
            case LEATHER:
                map.put(GenericEnums.DAMAGE_TYPE.FIRE, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.SONIC, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.SLASHING, GenericEnums.RESIST_GRADE.Vulnerable);
                break;
            case METAL:
                map.put(GenericEnums.DAMAGE_TYPE.BLUDGEONING, GenericEnums.RESIST_GRADE.Vulnerable);
                map.put(GenericEnums.DAMAGE_TYPE.LIGHT, GenericEnums.RESIST_GRADE.Resistant);
                map.put(GenericEnums.DAMAGE_TYPE.SONIC, GenericEnums.RESIST_GRADE.Vulnerable);
                map.put(GenericEnums.DAMAGE_TYPE.ACID, GenericEnums.RESIST_GRADE.Vulnerable);
                map.put(GenericEnums.DAMAGE_TYPE.COLD, GenericEnums.RESIST_GRADE.Vulnerable);
                map.put(GenericEnums.DAMAGE_TYPE.LIGHTNING, GenericEnums.RESIST_GRADE.Ineffective);
                break;
            case STONE:
                map.put(GenericEnums.DAMAGE_TYPE.LIGHTNING, GenericEnums.RESIST_GRADE.Impregnable);
                map.put(GenericEnums.DAMAGE_TYPE.PIERCING, GenericEnums.RESIST_GRADE.Resistant);
                break;

        }
        // fill with 'normals'
        return map;
    }

    public static String getNaturalArmorTypeForUnit(BattleFieldObject attacked) {
        return StringMaster.format(getObjectArmorTypeForUnit(attacked).toString());
    }

    public static OBJECT_ARMOR_TYPE getObjectArmorTypeForUnit(BattleFieldObject attacked) {
        if (attacked.checkClassification(UnitEnums.CLASSIFICATIONS.WRAITH)) {
            return OBJECT_ARMOR_TYPE.ETHEREAL;
        }
        if (attacked.checkClassification(UnitEnums.CLASSIFICATIONS.MECHANICAL)) {
            return OBJECT_ARMOR_TYPE.METAL;
        }
        if (attacked.checkClassification(UnitEnums.CLASSIFICATIONS.CONSTRUCT)) {
            return OBJECT_ARMOR_TYPE.STONE;
        }
        if (attacked.checkClassification(UnitEnums.CLASSIFICATIONS.ELEMENTAL)) {
            return OBJECT_ARMOR_TYPE.CRYSTAL;
        }
        if (attacked.checkClassification(UnitEnums.CLASSIFICATIONS.UNDEAD)) {
            return OBJECT_ARMOR_TYPE.BONE;
        }
        return OBJECT_ARMOR_TYPE.FLESH;
    }

    private static RESIST_GRADE getGradeForUnitType(ObjType t, DAMAGE_TYPE dmg_type) {
        OBJECT_ARMOR_TYPE armor_type = getObjectArmorTypeForUnit(new Unit(t));
        ITEM_MATERIAL_GROUP group = armor_type.getGroup();
        if (group == null) {
            return GenericEnums.RESIST_GRADE.Normal;
        }
        return getDefaultResistMap(group).get(dmg_type);
    }

    public static void generateArmorPerDamageType(ObjType t, MATERIAL material) {
        Integer armor = t.getIntParam(PARAMS.ARMOR);

        for (DAMAGE_TYPE dmg_type : GenericEnums.DAMAGE_TYPE.values()) {
            RESIST_GRADE grade = (material == null) ? getGradeForUnitType(t, dmg_type) : material
                    .getResistGrade(dmg_type);
            PROPERTY prop = DC_ContentValsManager.getResistGradeForDmgType(dmg_type);
            if (prop == null) {
                continue;
            }

            t.setProperty(prop, grade.toString());
            t.setParam(DC_ContentValsManager.getArmorParamForDmgType(dmg_type), Math.round(armor
                    * grade.getPercent() / 100));
            if (material == null) {
                grade = new EnumMaster<RESIST_GRADE>().retrieveEnumConst(RESIST_GRADE.class, t
                        .getProperty(DC_ContentValsManager.getResistGradeForDmgType(dmg_type)));
            } else {
                grade = material.getSelfDamageGrade(dmg_type);
            }
            t.setProperty(DC_ContentValsManager.getSelfDamageGradeForDmgType(dmg_type), grade
                    .toString());

            prop = DC_ContentValsManager.getSelfDamageGradeForDmgType(dmg_type);
            if (prop == null) {
                continue;
            }
            grade = new EnumMaster<RESIST_GRADE>().retrieveEnumConst(RESIST_GRADE.class, t
                    .getProperty(prop));

            t.setParam(DC_ContentValsManager.getArmorSelfDamageParamForDmgType(dmg_type), Math
                    .round(grade.getPercent()));

        }

    }

    private static String getDefaultWeaponActions(WEAPON_GROUP group) {
        switch (group) {
            case AXES:
                return ContainerUtils.constructStringContainer(ListMaster.toList(
                        Axe_Swing, Chop, Hack,
                        Hook));
            case POLLAXES:
                return ContainerUtils.constructStringContainer(ListMaster.toList(
                        Spike_Stab, Axe_Swing, Chop,
                        Hack, Hook));

            case FLAILS:
                return ContainerUtils.constructStringContainer(ListMaster.toList(
                        Heavy_Swing, Chain_Thrust,
                        Head_Smash));
            case HAMMERS:
            case CLUBS:
                return ContainerUtils
                        .constructStringContainer(ListMaster.toList(Heavy_Swing,
                                Head_Smash, Slam));
            case MACES:
                return ContainerUtils.constructStringContainer(ListMaster.toList(
                        Head_Smash, Heavy_Swing));

            case GREAT_SWORDS:
            case LONG_SWORDS:
                return ContainerUtils.constructStringContainer(ListMaster.toList(
                        Sword_Swing, Slash,
                        Blade_Thrust, Hilt_Smash));
            case SHORT_SWORDS:
                return ContainerUtils.constructStringContainer(ListMaster.toList(
                        Blade_Thrust, Slash,
                        Hilt_Smash, Stab));
            case DAGGERS:
                return ContainerUtils.constructStringContainer(ListMaster.toList(
                        Blade_Thrust, Slash, Stab));

            case SCYTHES:
                return ContainerUtils.constructStringContainer(ListMaster.toList(Hook,
                        Axe_Swing, Hack, Pole_Push,
                        Pole_Smash));
            case SPEARS:
                return ContainerUtils.constructStringContainer(ListMaster.toList(
                        Spear_Poke, Impale,
                        Pole_Smash, Pole_Push));
            case STAVES:
                return ContainerUtils.constructStringContainer(ListMaster.toList(
                        Pole_Smash, Pole_Thrust,
                        Pole_Push));
            case SHIELDS:
                return ContainerUtils.constructStringContainer(ListMaster.toList(
                        Shield_Push, Shield_Bash));
            case CLAWS:
                return ContainerUtils.constructStringContainer(ListMaster.toList(
                        Slice, Rip));
            case FISTS:
                return ContainerUtils.constructStringContainer(ListMaster
                        .toList(Punch, Fist_Swing,
                                Elbow_Smash));
            case FEET:
                return ContainerUtils
                        .constructStringContainer(ListMaster.toList(Hook));
            case MAWS:
                return ContainerUtils.constructStringContainer(ListMaster.toList(Bite,
                        Dig_Into, Tear));
            case FANGS:
                return ContainerUtils.constructStringContainer(ListMaster.toList(Bite,
                        Dig_Into));
            case TAILS:
                return ContainerUtils.constructStringContainer(ListMaster.toList(
                        Tail_Smash, Tail_Sting));
            case HORNS:
                return ContainerUtils.constructStringContainer(ListMaster.toList(
                        Pierce, Tear));
            case INSECTOID:
                return ContainerUtils.constructStringContainer(ListMaster.toList(
                        Pierce, Slice, Stab));
            case HOOVES:
                return ContainerUtils.constructStringContainer(ListMaster
                        .toList(Hoof_Slam));
            case BEAKS:
                return ContainerUtils.constructStringContainer(ListMaster.toList(Bite,
                        Tear, Dig_Into));
            case EYES:
                return ContainerUtils
                        .constructStringContainer(ListMaster.toList(Hook));
            case FORCE:
                return ContainerUtils
                        .constructStringContainer(ListMaster.toList(
                                Force_Push,
                                force_touch,
                                force_blast,
                                force_ray
                                ));

        }
        return null;
    }

    public static void generateWeaponParams(ObjType t) {
        // if (!t.checkProperty(PROPS.WEAPON_ATTACKS)) {
        WEAPON_GROUP group = new EnumMaster<WEAPON_GROUP>().retrieveEnumConst(WEAPON_GROUP.class, t
                .getProperty(G_PROPS.WEAPON_GROUP));
        if (group != null) {
            String weaponActions = getDefaultWeaponActions(group);
            if (weaponActions != null) {
                if (!t.checkProperty(PROPS.WEAPON_ATTACKS)) {
                    t.setProperty(PROPS.WEAPON_ATTACKS, weaponActions);
                }
            }
        }
    }

    private static String getDEFAULT_ATTACK_OF_OPPORTUNITY_ACTION(WEAPON_GROUP group) {
        // TODO Auto-generated method stub
        return null;
    }

    private static String getDEFAULT_INSTANT_ATTACK_ACTION(WEAPON_GROUP group) {
        // TODO Auto-generated method stub
        return null;
    }

    private static String getDEFAULT_COUNTER_ATTACK_ACTION(WEAPON_GROUP group) {
        // TODO Auto-generated method stub
        return null;
    }

    private static String getDEFAULT_ATTACK_ACTION(WEAPON_GROUP group) {
        switch (group) {

        }
        return null;
    }

    private static Integer getDefaultImpactArea(WEAPON_GROUP g, WEAPON_SIZE size) {
        switch (g) {

            case ARROWS:
            case ORBS:
            case WANDS:
            case DAGGERS:
            case CROSSBOWS:
                return 10;
            case HAMMERS:
                switch (size) {
                    case HUGE:
                        return 60;
                    case LARGE:
                        return 45;
                    case MEDIUM:
                        return 35;
                    case SMALL:
                        return 25;
                    case TINY:
                        return 15;
                }
            case AXES:
                switch (size) {
                    case HUGE:
                        return 25;
                    case LARGE:
                        return 20;
                    case MEDIUM:
                        return 15;
                    case SMALL:
                        return 10;
                    case TINY:
                        return 5;
                }
            case BOLTS:
            case SPEARS:
                return 15;
            case BOWS:
            case STAVES:
            case SHORT_SWORDS:
            case POLLAXES:
                return 20;
            case CLUBS:
            case SCYTHES:
            case MACES:
            case LONG_SWORDS:
                return 30;
            case FLAILS:
                return 25;
            case GREAT_SWORDS:
                return 40;
            case SHIELDS:
                return 50;

        }
        return 25;
    }

    // public static void generateBfObjParams(ObjType t) {
    // BF_OBJECT_TYPE type = new EnumMaster<BF_OBJECT_TYPE>().retrieveEnumConst(
    // BF_OBJECT_TYPE.class, t.getProperty(G_PROPS.BF_OBJECT_TYPE));
    // BF_OBJECT_GROUP group = new
    // EnumMaster<BF_OBJECT_GROUP>().retrieveEnumConst(
    // BF_OBJECT_GROUP.class, t.getProperty(G_PROPS.BF_OBJECT_GROUP));
    // BF_OBJECT_SIZE size = new EnumMaster<BF_OBJECT_SIZE>().retrieveEnumConst(
    // BF_OBJECT_SIZE.class, t.getProperty(PROPS.BF_OBJECT_SIZE));
    // BF_OBJ_MATERIAL material = new
    // EnumMaster<BF_OBJ_MATERIAL>().retrieveEnumConst(
    // BF_OBJ_MATERIAL.class, t.getProperty(PROPS.BF_OBJ_MATERIAL));
    // OBJECT_ARMOR_TYPE armorType = new
    // EnumMaster<OBJECT_ARMOR_TYPE>().retrieveEnumConst(
    // OBJECT_ARMOR_TYPE.class, t.getProperty(PROPS.OBJECT_ARMOR_TYPE));
    //
    // if (armorType == null) {
    // switch (type) {
    //
    // }
    // }
    //
    // // TODO
    //
    // // stone/wood/metal/ ...
    // int i = 0;
    // List<String> list = getParamsFor(armorType);
    // for (PARAMS portrait : params) {
    // t.setParam(portrait, list.getOrCreate(i));
    // i++;
    // }
    // list = getResistancesForArmorType(armorType);
    // for (PARAMETER portrait : ValuePages.RESISTANCES) {
    // t.setParam(portrait, list.getOrCreate(i));
    // i++;
    // }
    //
    // }

    // TODO use mods from size? or multiply later...
    // private static String getParams(OBJECT_ARMOR_TYPE armorType, PARAMS portrait) {
    // switch (armorType) {
    // case AETHER:
    // return getAETHERParam(portrait);
    // case BONE:
    // return getBONEParam(portrait);
    // case CRYSTAL:
    // return getCRYSTALParam(portrait);
    // case ETHEREAL:
    // return getETHEREALParam(portrait);
    // case FLESH:
    // return getFLESHParam(portrait);
    // case METAL:
    // return getMETALParam(portrait);
    // case STONE:
    // return getSTONEParam(portrait);
    // case WOOD:
    // return getWOODParam(portrait);
    //
    // }
    //
    // return null;
    // }
    //
    // private static List<String> getResistancesForArmorType(OBJECT_ARMOR_TYPE
    // armorType) {
    // List<String> list = new ArrayList<>();
    // for (PARAMETER portrait : ValuePages.RESISTANCES) {
    // switch ((PARAMS) portrait) {
    // case PIERCING_RESISTANCE:
    // list.add(getPIERCING_RESISTANCE(armorType));
    // list.add(getOrCreate(armorType));
    // break;
    // case BLUDGEONING_RESISTANCE:
    // list.add(getBLUDGEONING_RESISTANCE(armorType));
    // break;
    // case SLASHING_RESISTANCE:
    // list.add(getSLASHING_RESISTANCE(armorType));
    // break;
    // case POISON_RESISTANCE:
    // list.add(getPOISON_RESISTANCE(armorType));
    // break;
    // case FIRE_RESISTANCE:
    // list.add(getFIRE_RESISTANCE(armorType));
    // break;
    // case COLD_RESISTANCE:
    // list.add(getCOLD_RESISTANCE(armorType));
    // break;
    // case ACID_RESISTANCE:
    // list.add(getACID_RESISTANCE(armorType));
    // break;
    // case LIGHTNING_RESISTANCE:
    // list.add(getLIGHTNING_RESISTANCE(armorType));
    // break;
    // case SONIC_RESISTANCE:
    // list.add(getSONIC_RESISTANCE(armorType));
    // break;
    // case LIGHT_RESISTANCE:
    // list.add(getLIGHT_RESISTANCE(armorType));
    // break;
    // case CHAOS_RESISTANCE:
    // list.add(getCHAOS_RESISTANCE(armorType));
    // break;
    // case ARCANE_RESISTANCE:
    // list.add(getARCANE_RESISTANCE(armorType));
    // break;
    // case HOLY_RESISTANCE:
    // list.add(getHOLY_RESISTANCE(armorType));
    // break;
    // case SHADOW_RESISTANCE:
    // list.add(getSHADOW_RESISTANCE(armorType));
    // break;
    // case PSIONIC_RESISTANCE:
    // list.add(getPSIONIC_RESISTANCE(armorType));
    // break;
    // case DEATH_RESISTANCE:
    // list.add(getDEATH_RESISTANCE(armorType));
    // break;
    // }
    // }
    //
    // return list;
    // }
    //
    // private static List<String> getParamsFor(OBJECT_ARMOR_TYPE armorType) {
    // List<String> list = new ArrayList<>();
    // for (PARAMS portrait : params) {
    // list.add(getParams(armorType, portrait));
    // }
    //
    // return list;
    // }

    public static void initHeight(ObjType t) {
        DC_TYPE TYPE = (DC_TYPE) t.getOBJ_TYPE_ENUM();
        if (t.getIntParam(PARAMS.HEIGHT) != 0) {
            return;
        }
        int height = 0;
        switch (TYPE) {
            case BF_OBJ:

                BF_OBJECT_TYPE type = new EnumMaster<BF_OBJECT_TYPE>().retrieveEnumConst(
                        BF_OBJECT_TYPE.class, t.getProperty(G_PROPS.BF_OBJECT_TYPE));
                BF_OBJECT_GROUP group = new EnumMaster<BF_OBJECT_GROUP>().retrieveEnumConst(
                        BF_OBJECT_GROUP.class, t.getProperty(G_PROPS.BF_OBJECT_GROUP));
                if (type != null) {
                    switch (type) {
                        // TODO

                    }
                }
                if (group != null) {
                    switch (group) {
                        case ROCKS:
                            height = 100;
                            break;
                        case TREES:
                            height = 1000;
                            break;
                        default:
                            break;

                    }
                }
                break;

            case UNITS:
                List<CLASSIFICATIONS> c = new EnumMaster<CLASSIFICATIONS>().getEnumList(
                        CLASSIFICATIONS.class, t.getProperty(G_PROPS.CLASSIFICATIONS));

                if (c.contains(UnitEnums.CLASSIFICATIONS.GIANT)) {
                    height = 600;

                } else if (c.contains(UnitEnums.CLASSIFICATIONS.HUMANOID)) {
                    height = 180;

                } else if (c.contains(UnitEnums.CLASSIFICATIONS.TALL)) {
                    height = 225;

                } else if (c.contains(UnitEnums.CLASSIFICATIONS.SHORT)) {
                    height = 125;

                }

            case CHARS:
                RACE race = new EnumMaster<RACE>().retrieveEnumConst(RACE.class, t
                        .getProperty(G_PROPS.RACE));
                SUBRACE bg = new EnumMaster<SUBRACE>().retrieveEnumConst(SUBRACE.class, t
                        .getProperty(G_PROPS.BACKGROUND));
                GENDER g = new EnumMaster<GENDER>().retrieveEnumConst(GENDER.class, t
                        .getProperty(G_PROPS.GENDER));
                if (race != null) {
                    switch (race) {
                        case DWARF:
                            height = 130;
                            break;
                        case ELF:
                            height = 190;
                            break;
                        case HUMAN:
                            height = 180;
                            if (bg == HeroEnums.SUBRACE.MAN_OF_WOLF_REALM) {
                                height += 10;
                            }
                            if (bg == HeroEnums.SUBRACE.MAN_OF_EAGLE_REALM) {
                                height += 15;
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (g == HeroEnums.GENDER.FEMALE) {
                    height -= height / 5;
                }
                break;
        }
        t.setParam(PARAMS.HEIGHT, height);

        // per race?
        // per obj type

    }

}
