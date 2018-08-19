package eidolons.game.module.dungeoncrawl.generator;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
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
        data.setLocationType(locationType);
        data.setSublevelType(subType);
        data.setReqs(
        LevelDataMaker.getDefaultReqs(data));
        data.setTemplateGroups(getTemplatesForLocationType(locationType));
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
            case RUIN:
            case CAMP:
            case GROVE:
//                data.setValue(LEVEL_VALUES.WRAP_CELL_TYPE, );
//                data.setValue(LEVEL_VALUES.WRAP_ROOMS, );
//                data.setValue(LEVEL_VALUES.DOOR_CHANCE_MOD, );
//                data.setValue(LEVEL_VALUES.MAIN_PATHS,);
//                data.setValue(LEVEL_VALUES.BONUS_PATHS,);
//                data.setValue(LEVEL_VALUES.MAIN_PATH_LENGTH,);
//                data.setValue(LEVEL_VALUES.BONUS_PATH_LENGTH,);
                break;
        }

        return data;
    }

    private static ROOM_TEMPLATE_GROUP[] getTemplatesForLocationType(LOCATION_TYPE locationType) {
        if (LevelGenerator.TEST_MODE)
        return new ROOM_TEMPLATE_GROUP[]{
         ROOM_TEMPLATE_GROUP.CRYPT,
        };
        switch (locationType) {
            case CAVE:
                return new ROOM_TEMPLATE_GROUP[]{
                 ROOM_TEMPLATE_GROUP.CAVE,
                 ROOM_TEMPLATE_GROUP.MAZE,
                };
            case CEMETERY:
                break;
            case CRYPT:
                return new ROOM_TEMPLATE_GROUP[]{
                 ROOM_TEMPLATE_GROUP.CRYPT,
                 ROOM_TEMPLATE_GROUP.TOWER,
                 ROOM_TEMPLATE_GROUP.TEMPLE,
                };
            case BARROW:
                return new ROOM_TEMPLATE_GROUP[]{
                 ROOM_TEMPLATE_GROUP.CRYPT,
                 ROOM_TEMPLATE_GROUP.CAVE,
                 ROOM_TEMPLATE_GROUP.MAZE,
                };
            case HIVE:
                break;
            case DUNGEON:
                return new ROOM_TEMPLATE_GROUP[]{ROOM_TEMPLATE_GROUP.CRYPT};
            case SEWER:
                break;
            case HELL:
                break;
            case ASTRAL:
                break;
            case ARCANE:
                break;
            case DEN:
                break;
            case RUIN:
                break;
            case CAMP:
                break;
            case TEMPLE:
                break;
            case CASTLE:
                break;
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
            String val = getReq(req, sublevelType, type, data)+"";
            vals.add(val);
        }

        factory.setValues(vals.toArray(
         new String[LEVEL_VALUES.values().length]));
        return new DataUnit(factory.constructDataString());
    }

    private static Object getReq(LEVEL_REQUIREMENTS req,
                                 SUBLEVEL_TYPE sublevelType, LOCATION_TYPE type, LevelData data) {
        switch (req) {
            case minRooms:
                switch (sublevelType) {
                    case COMMON:
                        return  12;
                    case PRE_BOSS:
                        return 6;
                    case BOSS:
                        return 4;
                }
            case minFillRatio:
                return 0.3f;
            case minDimensionRatio:
                switch (sublevelType) {
                    case COMMON:
                        return  0.4f;
                    case PRE_BOSS:
                        return  0.3f;
                    case BOSS:
                        return  0.2f;
                }
            case maxSquare:
                return (int)Math.pow((int) getReq(LEVEL_REQUIREMENTS.maxDimension, sublevelType, type, data), 2)
                  *5 / 4;
            case maxDimension:
                //level.getModel().getData().getROOM_COEF()
                float sizeMod = new Float(data.getIntValue(LEVEL_VALUES.SIZE_MOD)) / 100;
                switch (sublevelType) {
                    case COMMON:
                        return Math.round(500 * sizeMod);
                    case PRE_BOSS:
                        return Math.round(400 * sizeMod);
                    case BOSS:
                        return  Math.round(350 * sizeMod);
                }
        }
        return null;
    }

    public enum LEVEL_REQUIREMENTS {
        minRooms, minFillRatio, minDimensionRatio, maxDimension, maxSquare;
    }

    //    public static LevelData generateDataViaDialog(ObjType place, ObjType dungeon){
    ////        DialogMaster.inputInt()
    //    }


}
