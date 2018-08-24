package eidolons.game.module.dungeoncrawl.generator;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import eidolons.game.module.dungeoncrawl.generator.model.RoomModelTransformer;
import eidolons.game.module.dungeoncrawl.generator.test.LevelStats.LEVEL_GEN_FLAG;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.system.data.DataUnit;
import main.system.data.DataUnitFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/21/2018.
 */
public class LevelDataMaker {
    public static LevelData getDefaultLevelData(
     int sublevel
    ) {
        DataUnitFactory<LevelData> factory = new DataUnitFactory();
        factory.setValueNames(LEVEL_VALUES.values());
        factory.setValues(Arrays.stream(LEVEL_VALUES.values()).map(
         value -> value.getDefaultValue() + "").
         collect(Collectors.toList()).toArray(
         new String[LEVEL_VALUES.values().length]
        ));
        return new LevelData(factory.constructDataString());
    }


    public static LevelData generateData(
     SUBLEVEL_TYPE subType,
     LOCATION_TYPE locationType) {

        LevelData data = getDefaultLevelData(0);

        initSublevelType(data, subType);
        initLocationType(data, locationType);


        data.setReqs(getDefaultReqs(data));

        return data;
    }
    public static LEVEL_DATA_MODIFICATION[] getMods(LOCATION_TYPE locationType) {
        switch (locationType) {
            case CRYPT:
                return new LEVEL_DATA_MODIFICATION[]{

                };
        }
        return new LEVEL_DATA_MODIFICATION[0];
    }

    public enum LEVEL_DATA_MODIFICATION{
        LENGTHEN_MAIN_PATH,
        NO_DOORS,
        WRAP_ROOMS,
        WRAP_TYPE,
        ;
        Object arg;

    }

    private static void initSublevelType(LevelData data, SUBLEVEL_TYPE subType) {
        data.setSublevelType(subType);

        switch (subType) {
            case COMMON:
                data.setValue(LEVEL_VALUES.ZONES, "4");
                break;
            case PRE_BOSS:
                data.setValue(LEVEL_VALUES.ZONES, "3");
                break;
            default:
                data.setValue(LEVEL_VALUES.ZONES, "2");
                break;
        }
    }

    private static void initLocationType(LevelData data, LOCATION_TYPE locationType) {
        data.setLocationType(locationType);
        data.setTemplateGroups(getTemplatesForLocationType(locationType));

        switch (locationType) {
            case CAVE:
                break;
            case CRYPT:
                break;
            case BARROW:
                break;
            case DEN:
                break;
            case HIVE:
                break;
            case SEWER:
                break;

            case DUNGEON:
                break;
            case HELL:
                break;
            case ASTRAL:
                break;
            case ARCANE:
                break;
            case HOUSE:
                break;


            case TEMPLE:
                break;
            case CASTLE:
                break;

            case CEMETERY:
                                data.setValue(LEVEL_VALUES.DOOR_CHANCE_MOD, "150");
            case RUIN:
            case CAMP:
            case GROVE:
                initSurfaceData(data);
                //                data.setValue(LEVEL_VALUES.WRAP_CELL_TYPE, );
                //                data.setValue(LEVEL_VALUES.WRAP_ROOMS, );
                //                data.setValue(LEVEL_VALUES.DOOR_CHANCE_MOD, );
                //                data.setValue(LEVEL_VALUES.MAIN_PATHS,);
                //                data.setValue(LEVEL_VALUES.BONUS_PATHS,);
                //                data.setValue(LEVEL_VALUES.MAIN_PATH_LENGTH,);
                //                data.setValue(LEVEL_VALUES.BONUS_PATH_LENGTH,);
                break;
        }
    }

    private static void initSurfaceData(LevelData data) {

        data.setValue(LEVEL_VALUES.WRAP_CELL_TYPE,
         ROOM_CELL.INDESTRUCTIBLE.getSymbol()+
          RoomModelTransformer.WRAP_SEPARATOR+
          ROOM_CELL.FLOOR.getSymbol());
        data.setValue(LEVEL_VALUES.VOID_CELL_TYPE,
         ROOM_CELL.FLOOR.getSymbol());
        data.setValue(LEVEL_VALUES.WRAP_ROOMS, "2");
        data.setValue(LEVEL_VALUES.SIZE_MOD, "200");
        data.setValue(LEVEL_VALUES.ROOM_COUNT_MOD, "70");

        data.setValue(LEVEL_VALUES.SURFACE, "true");
    }

    private static ROOM_TEMPLATE_GROUP[] getTemplatesForLocationType(LOCATION_TYPE locationType) {
        if (LevelGenerator.TEST_MODE)
            locationType =LevelGenerator.TEST_LOCATION_TYPE;
        //        return new ROOM_TEMPLATE_GROUP[]{
        //         ROOM_TEMPLATE_GROUP.CRYPT,
        //        };
        switch (locationType) {
            case CAMP:
                return new ROOM_TEMPLATE_GROUP[]{
                 ROOM_TEMPLATE_GROUP.GROVE,
                };
            case CAVE:
                return new ROOM_TEMPLATE_GROUP[]{
                 ROOM_TEMPLATE_GROUP.CAVE,
                 ROOM_TEMPLATE_GROUP.MAZE,
                };
            case CEMETERY:
                return new ROOM_TEMPLATE_GROUP[]{
                 ROOM_TEMPLATE_GROUP.CEMETERY,
                };
            case CRYPT:
                return new ROOM_TEMPLATE_GROUP[]{
                 ROOM_TEMPLATE_GROUP.CRYPT,
                 ROOM_TEMPLATE_GROUP.CRYPT_TOWER,
                 ROOM_TEMPLATE_GROUP.TOWER,
                 ROOM_TEMPLATE_GROUP.TEMPLE,
                };
            case BARROW:
                return new ROOM_TEMPLATE_GROUP[]{
                 ROOM_TEMPLATE_GROUP.CAVE_MAZE,
                 ROOM_TEMPLATE_GROUP.CRYPT,
                 ROOM_TEMPLATE_GROUP.CAVE,
                 ROOM_TEMPLATE_GROUP.MAZE,
                };
            case HIVE:
                break;
            case DUNGEON:
                return new ROOM_TEMPLATE_GROUP[]{
                 ROOM_TEMPLATE_GROUP.DUNGEON,
                 ROOM_TEMPLATE_GROUP.DUNGEON_CASTLE,
                 ROOM_TEMPLATE_GROUP.TEMPLE_DUNGEON,
                 ROOM_TEMPLATE_GROUP.CRYPT,
                };
            case SEWER:
                break;
            case HELL:
                break;
            case ASTRAL:
                break;
            case ARCANE:
                return new ROOM_TEMPLATE_GROUP[]{
                 ROOM_TEMPLATE_GROUP.TOWER,
                 ROOM_TEMPLATE_GROUP.TOWER_TEMPLE,
                 ROOM_TEMPLATE_GROUP.CRYPT_TOWER,
                 ROOM_TEMPLATE_GROUP.CASTLE_TEMPLE,
                };
            case DEN:
                break;
            case RUIN:
                break;
            case TEMPLE:
                return new ROOM_TEMPLATE_GROUP[]{
                 ROOM_TEMPLATE_GROUP.TEMPLE,
                 ROOM_TEMPLATE_GROUP.TEMPLE_DUNGEON,
                 ROOM_TEMPLATE_GROUP.TEMPLE_CRYPT,
                 ROOM_TEMPLATE_GROUP.CASTLE_TEMPLE,
                };
            case CASTLE:
                return new ROOM_TEMPLATE_GROUP[]{
                 ROOM_TEMPLATE_GROUP.CASTLE,
                 ROOM_TEMPLATE_GROUP.DUNGEON_CASTLE,
                 ROOM_TEMPLATE_GROUP.CASTLE_TEMPLE,
                 ROOM_TEMPLATE_GROUP.TEMPLE,
                };
            case HOUSE:
                break;
            case GROVE:
                break;
        }
        return new ROOM_TEMPLATE_GROUP[]{ROOM_TEMPLATE_GROUP.CRYPT};
    }

    public static DataUnit<LEVEL_GEN_FLAG> getDefaultLevelFlags(
    ) {
        DataUnitFactory<LevelData> factory = new DataUnitFactory();
        factory.setValueNames(LEVEL_GEN_FLAG.values());
        factory.setValues(Arrays.stream(LEVEL_GEN_FLAG.values()).map(
         value -> value.getDefaultValue() + "").
         collect(Collectors.toList()).toArray(
         new String[LEVEL_VALUES.values().length]
        ));
        return new DataUnit(factory.constructDataString());
    }

    public static DataUnit<LEVEL_REQUIREMENTS> getDefaultReqs(LevelData data) {
        SUBLEVEL_TYPE sublevelType = data.getSublevelType();
        LOCATION_TYPE type = data.getLocationType();

        DataUnitFactory<LevelData> factory = new DataUnitFactory();
        factory.setValueNames(LEVEL_REQUIREMENTS.values());
        List<String> vals = new ArrayList<>();
        for (LEVEL_REQUIREMENTS req : LEVEL_REQUIREMENTS.values()) {
            String val = getReq(req, sublevelType, type, data) + "";
            vals.add(val);
        }

        factory.setValues(vals.toArray(
         new String[LEVEL_VALUES.values().length]));
        return new DataUnit(factory.constructDataString());
    }

    private static Object getReq(LEVEL_REQUIREMENTS req,
                                 SUBLEVEL_TYPE sublevelType, LOCATION_TYPE type, LevelData data) {
        float sizeMod = new Float(data.getIntValue(LEVEL_VALUES.SIZE_MOD)) / 100;
        float roomMod = new Float(data.getIntValue(LEVEL_VALUES.ROOM_COUNT_MOD)) / 100;
        switch (req) {
            case maxRooms:
                switch (sublevelType) {
                    case COMMON:
                        return Math.round(18 *roomMod);
                    case PRE_BOSS:
                        return Math.round(12 *roomMod);
                    case BOSS:
                        return Math.round(8 *roomMod);
                }
            case minRooms:
                switch (sublevelType) {
                    case COMMON:
                        return Math.round(12 *roomMod);
                    case PRE_BOSS:
                        return Math.round(6 *roomMod);
                    case BOSS:
                        return Math.round(4 *roomMod);
                }
            case minFillRatio:
                return 0.3f;
            case minDimensionRatio:
                switch (sublevelType) {
                    case COMMON:
                        return 0.4f;
                    case PRE_BOSS:
                        return 0.3f;
                    case BOSS:
                        return 0.2f;
                }
            case maxSquare:
                return (int) Math.pow((int) getReq(LEVEL_REQUIREMENTS.maxDimension, sublevelType, type, data), 2)
                 * 5 / 4;
            case maxDimension:
                //level.getModel().getData().getROOM_COEF()
                switch (sublevelType) {
                    case COMMON:
                        return Math.round(60 *sizeMod);
                    case PRE_BOSS:
                        return Math.round(45 *sizeMod);
                    case BOSS:
                        return Math.round(30 *sizeMod);
                }
        }
        return null;
    }
    public enum LEVEL_REQUIREMENTS {
        minRooms, maxRooms, minFillRatio, minDimensionRatio, maxDimension, maxSquare;
    }

    //    public static LevelData generateDataViaDialog(ObjType place, ObjType dungeon){
    ////        DialogMaster.inputInt()
    //    }


}
