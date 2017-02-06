package main.system;

import main.content.CONTENT_CONSTS.*;
import main.content.PROPS;
import main.content.properties.G_PROPS;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.List;

public class BfObjPropGenerator {

    public static void generateBfObjProps(ObjType t) {

        BF_OBJECT_TYPE type = new EnumMaster<BF_OBJECT_TYPE>().retrieveEnumConst(
                BF_OBJECT_TYPE.class, t.getProperty(G_PROPS.BF_OBJECT_TYPE));
        BF_OBJECT_GROUP group = new EnumMaster<BF_OBJECT_GROUP>().retrieveEnumConst(
                BF_OBJECT_GROUP.class, t.getProperty(G_PROPS.BF_OBJECT_GROUP));
        if (type == null) {
            switch (t.getProperty(G_PROPS.BF_OBJECT_TYPE).toUpperCase()) {
                case "WALL":
                    group = BF_OBJECT_GROUP.WALL;
                    type = BF_OBJECT_TYPE.STRUCTURE;
                    break;
                case "ENTRANCE":
                    group = BF_OBJECT_GROUP.ENTRANCE;
                    type = BF_OBJECT_TYPE.SPECIAL;
                    break;
                case "DOOR":
                    group = BF_OBJECT_GROUP.DOOR;
                    type = BF_OBJECT_TYPE.SPECIAL;
                    break;
                case "TRAP":
                    group = BF_OBJECT_GROUP.TRAP;
                    type = BF_OBJECT_TYPE.SPECIAL;
                    break;
                case "CRYSTAL":
                    group = BF_OBJECT_GROUP.CRYSTAL;
                    type = BF_OBJECT_TYPE.NATURAL;
                    break;
                case "GATEWAY":
                    group = BF_OBJECT_GROUP.GATEWAY;
                    type = BF_OBJECT_TYPE.STRUCTURE;
                    break;
            }
        }

        if (group == null) {
            switch (t.getProperty(G_PROPS.BF_OBJECT_GROUP).toUpperCase()) {
                case "PROP":
                    type = BF_OBJECT_TYPE.PROP;
                    break;

            }

        }

        switch (t.getProperty(G_PROPS.GROUP).toUpperCase()) {
            case "LIGHT EMITTERS":
            case "CONTAINER":
            case "TREASURE":
                type = BF_OBJECT_TYPE.SPECIAL;
                break;
            case "MAGICAL":
                type = BF_OBJECT_TYPE.PROP;
                break;
            case "PROP":
                type = BF_OBJECT_TYPE.PROP;
                break;

        }
        switch (t.getProperty(G_PROPS.GROUP).toUpperCase()) {
            case "LIGHT EMITTERS":
                group = BF_OBJECT_GROUP.LIGHT_EMITTER;
                break;
            case "CONTAINER":
                group = BF_OBJECT_GROUP.CONTAINER;
                break;
            case "TREASURE":
                group = BF_OBJECT_GROUP.TREASURE;
                break;
            case "MAGICAL":
                group = BF_OBJECT_GROUP.MAGICAL;
                break;
            case "ROCKS":
                group = BF_OBJECT_GROUP.ROCKS;
                break;
            case "TREES":
                group = BF_OBJECT_GROUP.TREES;
                break;
            case "DUNGEON":
                group = BF_OBJECT_GROUP.DUNGEON;
                break;
            case "GRAVES":
                group = BF_OBJECT_GROUP.GRAVES;
                type = BF_OBJECT_TYPE.PROP;
                break;
            case "RUINS":
                group = BF_OBJECT_GROUP.RUINS;
                break;

        }
        if (t.getProperty(G_PROPS.NAME).toUpperCase().contains("GARGOY")) {
            group = BF_OBJECT_GROUP.STATUES;
        } else if (t.getProperty(G_PROPS.NAME).toUpperCase().contains("STATUE")) {
            group = BF_OBJECT_GROUP.STATUES;
        } else if (t.getProperty(G_PROPS.NAME).toUpperCase().contains("EMBLEM")) {
            group = BF_OBJECT_GROUP.HANGING;
        } else if (t.getProperty(G_PROPS.NAME).toUpperCase().contains("TABLE")) {
            group = BF_OBJECT_GROUP.INTERIOR;
        } else if (t.getProperty(G_PROPS.NAME).toUpperCase().contains("WIZARD")) {
            group = BF_OBJECT_GROUP.MAGICAL;
        } else if (t.getProperty(G_PROPS.NAME).toUpperCase().contains("ALCHEM")) {
            group = BF_OBJECT_GROUP.MAGICAL;
        } else if (t.getProperty(G_PROPS.NAME).toUpperCase().contains("SEWER")) {
            group = BF_OBJECT_GROUP.WATER;
        } else if (t.getProperty(G_PROPS.NAME).toUpperCase().contains("WATER")) {
            group = BF_OBJECT_GROUP.WATER;
        }
        if (group != null) {
            t.setProperty(G_PROPS.BF_OBJECT_GROUP, StringMaster
                    .getWellFormattedString(group.name()));
        }

        if (type != null) {
            t.setProperty(G_PROPS.BF_OBJECT_TYPE, StringMaster.getWellFormattedString(type.name()));
        }

    }

    public static void generateBfObjStatProps(ObjType t) {
        // TODO theme from group!
        BF_OBJECT_SIZE size = new EnumMaster<BF_OBJECT_SIZE>().retrieveEnumConst(
                BF_OBJECT_SIZE.class, t.getProperty(PROPS.BF_OBJECT_SIZE));
        BF_OBJ_MATERIAL material = new EnumMaster<BF_OBJ_MATERIAL>().retrieveEnumConst(
                BF_OBJ_MATERIAL.class, t.getProperty(PROPS.BF_OBJ_MATERIAL));
        OBJECT_ARMOR_TYPE armorType = new EnumMaster<OBJECT_ARMOR_TYPE>().retrieveEnumConst(
                OBJECT_ARMOR_TYPE.class, t.getProperty(PROPS.OBJECT_ARMOR_TYPE));
        DIMENSION dimension = new EnumMaster<DIMENSION>().retrieveEnumConst(DIMENSION.class, t
                .getProperty(PROPS.DIMENSION));

        List<BF_OBJ_QUALITY> qualities = new EnumMaster<BF_OBJ_QUALITY>().getEnumList(
                BF_OBJ_QUALITY.class, t.getProperty(PROPS.BF_OBJ_QUALITY));

        BF_OBJECT_GROUP group = new EnumMaster<BF_OBJECT_GROUP>().retrieveEnumConst(
                BF_OBJECT_GROUP.class, t.getProperty(G_PROPS.BF_OBJECT_GROUP));
        if (group == null) {
            switch (group) {
                case COLUMNS:
                    if (qualities.isEmpty()) {
                        qualities.add(BF_OBJ_QUALITY.TOUGH_II);
                        qualities.add(BF_OBJ_QUALITY.ARMORED);
                        qualities.add(BF_OBJ_QUALITY.THICK);
                    }
                    // later from material/armor types
                    if (dimension == null) {
                        dimension = DIMENSION.T1_10;
                    }
                    if (armorType == null) {
                        armorType = OBJECT_ARMOR_TYPE.STONE;
                    }
                    if (material == null) {
                        material = BF_OBJ_MATERIAL.MARBLE;
                    }
                    if (size == null) {
                        size = BF_OBJECT_SIZE.LARGE;
                    }
                    // weight as girth*height*material*coef ?
                    break;
                case CONSTRUCT:
                    if (dimension == null) {
                        dimension = DIMENSION.W2_1;
                    }
                    if (armorType == null) {
                        armorType = OBJECT_ARMOR_TYPE.STONE;
                    }
                    if (material == null) {
                        material = BF_OBJ_MATERIAL.GRANITE;
                    }
                    if (size == null) {
                        size = BF_OBJECT_SIZE.LARGE;
                    }
                    break;
                case TREASURE:
                    if (qualities.isEmpty()) {
                        qualities.add(BF_OBJ_QUALITY.RESISTANT);
                        qualities.add(BF_OBJ_QUALITY.DURABLE);
                        qualities.add(BF_OBJ_QUALITY.ARMORED);
                    }
                    if (dimension == null) {
                        dimension = DIMENSION.W3_1;
                    }
                    if (armorType == null) {
                        armorType = OBJECT_ARMOR_TYPE.WOOD;
                    }
                    if (material == null) {
                        material = BF_OBJ_MATERIAL.RED_OAK;
                    }
                    if (size == null) {
                        size = BF_OBJECT_SIZE.SMALL;
                    }
                    break;
                case CONTAINER:
                    if (qualities.isEmpty()) {
                        qualities.add(BF_OBJ_QUALITY.RESISTANT);
                        qualities.add(BF_OBJ_QUALITY.BRITTLE);
                    }
                    if (dimension == null) {
                        dimension = DIMENSION.W3_1;
                    }
                    if (armorType == null) {
                        armorType = OBJECT_ARMOR_TYPE.WOOD;
                    }
                    if (material == null) {
                        material = BF_OBJ_MATERIAL.RED_OAK;
                    }
                    if (size == null) {
                        size = BF_OBJECT_SIZE.SMALL;
                    }
                    break;
                case CRYSTAL:
                    if (qualities.isEmpty()) {
                        qualities.add(BF_OBJ_QUALITY.RESISTANT);
                        qualities.add(BF_OBJ_QUALITY.BRITTLE);
                        qualities.add(BF_OBJ_QUALITY.ARMORED);
                    }
                    if (dimension == null) {
                        dimension = DIMENSION.T1_4;
                    }
                    if (armorType == null) {
                        armorType = OBJECT_ARMOR_TYPE.CRYSTAL;
                    }
                    if (material == null) {
                        material = BF_OBJ_MATERIAL.CRYSTAL;
                    }
                    if (size == null) {
                        size = BF_OBJECT_SIZE.MEDIUM;
                    }
                    break;
                case DOOR:
                    if (qualities.isEmpty()) {
                        qualities.add(BF_OBJ_QUALITY.TOUGH);
                        qualities.add(BF_OBJ_QUALITY.DURABLE);
                        qualities.add(BF_OBJ_QUALITY.THICK);
                    }
                    if (dimension == null) {
                        dimension = DIMENSION.T1_4;
                    }
                    if (armorType == null) {
                        armorType = OBJECT_ARMOR_TYPE.WOOD;
                    }
                    if (material == null) {
                        material = BF_OBJ_MATERIAL.IRONWOOD;
                    }
                    if (size == null) {
                        size = BF_OBJECT_SIZE.MEDIUM;
                    }
                    break;
                case DUNGEON:
                    break;
                case ENTRANCE:
                    break;
                case GATEWAY:
                    break;
                case GRAVES:
                    if (qualities.isEmpty()) {
                        qualities.add(BF_OBJ_QUALITY.TOUGH);
                        qualities.add(BF_OBJ_QUALITY.BRITTLE);
                    }
                    if (dimension == null) {
                        dimension = DIMENSION.T1_3;
                    }
                    if (armorType == null) {
                        armorType = OBJECT_ARMOR_TYPE.STONE;
                    }
                    if (material == null) {
                        material = BF_OBJ_MATERIAL.GRANITE;
                    }
                    if (size == null) {
                        size = BF_OBJECT_SIZE.MEDIUM;
                    }
                    break;
                case HANGING:
                    break;
                case INTERIOR:
                    break;
                case LIGHT_EMITTER:
                    break;
                case MAGICAL:
                    if (qualities.isEmpty()) {
                        qualities.add(BF_OBJ_QUALITY.BRITTLE_II);
                        qualities.add(BF_OBJ_QUALITY.RESISTANT_II);
                    }
                    break;
                case REMAINS:
                    break;
                case ROCKS:
                    if (dimension == null) {
                        dimension = DIMENSION.W2_1;
                    }
                    if (armorType == null) {
                        armorType = OBJECT_ARMOR_TYPE.STONE;
                    }
                    if (material == null) {
                        material = BF_OBJ_MATERIAL.GRANITE;
                    }
                    if (size == null) {
                        size = BF_OBJECT_SIZE.MEDIUM;
                    }
                    break;
                case RUINS:
                    if (qualities.isEmpty()) {
                        qualities.add(BF_OBJ_QUALITY.CRUMBLING_II);
                    }
                    break;
                case STATUES:
                    if (qualities.isEmpty()) {
                        qualities.add(BF_OBJ_QUALITY.TOUGH);
                        qualities.add(BF_OBJ_QUALITY.ARMORED);
                        qualities.add(BF_OBJ_QUALITY.RESISTANT);
                    }
                    break;
                case TRAP:
                    break;
                case TREES:
                    if (qualities.isEmpty()) {
                        qualities.add(BF_OBJ_QUALITY.TOUGH);
                        qualities.add(BF_OBJ_QUALITY.DURABLE_II);
                    }
                    break;
                case VEGETATION:
                    break;
                case WALL:
                    if (qualities.isEmpty()) {
                        qualities.add(BF_OBJ_QUALITY.TOUGH_III);
                        qualities.add(BF_OBJ_QUALITY.ARMORED_II);
                        qualities.add(BF_OBJ_QUALITY.THICK_II);
                        qualities.add(BF_OBJ_QUALITY.RESISTANT);
                        qualities.add(BF_OBJ_QUALITY.DURABLE_II);
                    }
                    break;
                case WATER:
                    break;
                case WINDOWS:
                    break;
                default:
                    break;

            }
        }
        if (size != null) {
            t.setProperty(PROPS.BF_OBJECT_SIZE, StringMaster.getWellFormattedString(size.name()));
        }
        if (material != null) {
            t.setProperty(PROPS.BF_OBJ_MATERIAL, StringMaster.getWellFormattedString(material
                    .name()));
        }
        if (dimension != null) {
            t.setProperty(PROPS.DIMENSION, StringMaster.getWellFormattedString(dimension.name()));
        }
        if (armorType != null) {
            t.setProperty(PROPS.OBJECT_ARMOR_TYPE, StringMaster.getWellFormattedString(armorType
                    .name()));
        }

    }

}
