package eidolons.system.utils.content;

import eidolons.content.PROPS;
import eidolons.game.core.master.ObjCreator;
import eidolons.game.exploration.dungeon.generator.GeneratorEnums;
import eidolons.game.exploration.dungeon.generator.init.RngBfObjProvider;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.ASPECT;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.UNIT_GROUP;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;

/**
 * Created by JustMe on 11/24/2017.
 */
public class PlaceholderGenerator {
    public static final GeneratorEnums.ROOM_CELL[] placeholder_cells = {
            GeneratorEnums.ROOM_CELL.ART_OBJ,
            GeneratorEnums.ROOM_CELL.ALT_WALL,
            GeneratorEnums.ROOM_CELL.WALL ,
            GeneratorEnums.ROOM_CELL.SPECIAL_ART_OBJ,
            GeneratorEnums.ROOM_CELL.CONTAINER,
            GeneratorEnums.ROOM_CELL.SPECIAL_CONTAINER,
            GeneratorEnums.ROOM_CELL.DOOR,
            GeneratorEnums.ROOM_CELL.TRAP,
            GeneratorEnums.ROOM_CELL.LIGHT_EMITTER,
            GeneratorEnums.ROOM_CELL.WALL_WITH_LIGHT_OVERLAY,
            GeneratorEnums.ROOM_CELL.WALL_WITH_DECOR_OVERLAY
    };
    private static final String ROOM_CELL_PLACEHOLDER = "Bf Placeholder";

    public static String getPlaceholderName(GeneratorEnums.ROOM_CELL cell) {
        String name = StringMaster.format(cell.name());
        return name + " "+ ObjCreator.PLACEHOLDER ;
    }
    public static void generateForRoomCells() {
        for (ObjType objType :    new ArrayList<>(DataManager.getTypesSubGroup(DC_TYPE.BF_OBJ, "Placeholder"))) {
            DataManager.removeType(objType);
        }
        ObjType baseType =new ObjType(ROOM_CELL_PLACEHOLDER, DC_TYPE.BF_OBJ);
        for (GeneratorEnums.ROOM_CELL cell : placeholder_cells) {
            ObjType type = new ObjType(baseType);
            String name = StringMaster.format(cell.name());
            type.setName(getPlaceholderName(cell));
            PROPERTY groupsProp = PROPS.PLACEHOLDER_DATA;
            type.setProperty(groupsProp, getSubgroupsForCell(cell));
            type.setProperty(PROPS.PLACEHOLDER_SYMBOL, cell.symbol);
            type.setGenerated(false);

            type.setProperty(G_PROPS.BF_OBJECT_GROUP, "Placeholder");
            type.setProperty(G_PROPS.BF_OBJECT_TYPE, "Special");
            type.setProperty(G_PROPS.IMAGE, "main/bf/placeholder/" +
                    "placeholder " +
                    name +
                    ".png");
            DataManager.addType(type);
            boolean indestructible=false;
            switch (cell) {
                case WALL:
                case ALT_WALL:
                    indestructible=true;
            }
            if(indestructible){
                ObjType newType = new ObjType(type.getName() + StringMaster.wall(true), type);
            newType.addProperty(G_PROPS.STD_BOOLS, GenericEnums.STD_BOOLS.INVULNERABLE.name());
            newType.addProperty(G_PROPS.STD_BOOLS, GenericEnums.STD_BOOLS.INDESTRUCTIBLE.name());
            DataManager.addType(newType);
            newType.setGenerated(false);
            }
        }

    }
    private static String getWeightMapForPlaceholder(GeneratorEnums.ROOM_CELL cell) {
        //more than style? or maybe we should review them just so
        DungeonEnums.DUNGEON_STYLE style = null;
        RngBfObjProvider.getWeightString(cell, style);
        return "";
    }

    private static String getSubgroupsForCell(GeneratorEnums.ROOM_CELL cell) {
        switch (cell) {
            case ART_OBJ:
            case WALL_WITH_DECOR_OVERLAY:
            case WALL_WITH_LIGHT_OVERLAY:
            case LIGHT_EMITTER:
            case TRAP:
            case DOOR:
            case SPECIAL_CONTAINER:
            case CONTAINER:
            case SPECIAL_ART_OBJ:
                return "";
        }
        return null;
    }

    public static void generate() {
        /*
        unit subgroups
         */
        for (UNIT_GROUP group : UnitEnums.UNIT_GROUP.values()) {
            for (String sub : ContainerUtils.openContainer(group.getSubgroups(), ",")) {
                for (PLACEHOLDER_AI_TYPE aiType : PLACEHOLDER_AI_TYPE.values()) {
                    ObjType type = generate(group, sub, aiType.name());
                    type.setProperty(PROPS.AI_TYPE, aiType.name());
                }
                for (PLACEHOLDER_POWER power : PLACEHOLDER_POWER.values()) {
                    generate(group, sub, power.name());
                }
            }
        }
    }

    private static ObjType generate(UNIT_GROUP group, String sub, String suffix) {
        //aspect ?
        // base type?
        String name = ObjCreator.PLACEHOLDER + " " + group.toString()
                + " " + sub + "_" + suffix;
        ObjType type = new ObjType(name, DC_TYPE.UNITS);
        type.setProperty(G_PROPS.GROUP, sub);
        type.setProperty(G_PROPS.ASPECT, ASPECT.NEUTRAL.toString());
        type.setProperty(G_PROPS.UNIT_GROUP, group.toString());
        DataManager.addType(type);
        return type;
    }


    public enum PLACEHOLDER_AI_TYPE {
        RANGED,
        MAGE,
        SNEAK,
        TANK,
        BRUTE,

    }

    public enum PLACEHOLDER_POWER {
        REGULAR,
        REGULAR_MASS,
        ELITE,
        ELITE_MASS,
        BOSS,
    }
}
