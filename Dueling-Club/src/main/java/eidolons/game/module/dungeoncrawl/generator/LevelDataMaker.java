package eidolons.game.module.dungeoncrawl.generator;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_DATA_MODIFICATION;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner;
import eidolons.game.module.dungeoncrawl.generator.model.RoomModelTransformer;
import eidolons.game.module.dungeoncrawl.generator.test.GenerationStats;
import eidolons.game.module.dungeoncrawl.generator.test.LevelStats.LEVEL_GEN_FLAG;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.LOCATION_TYPE_GROUP;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.system.auxiliary.RandomWizard;
import main.system.data.DataUnit;
import main.system.data.DataUnitFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_DATA_MODIFICATION.*;

/**
 * Created by JustMe on 7/21/2018.
 */
public class LevelDataMaker {
    private static Boolean random;

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
        data.setInitializeRequired(RngMainSpawner.TEST_MODE);
        return data;
    }


    public static LEVEL_DATA_MODIFICATION[] getCommonMods() {
        return new LEVEL_DATA_MODIFICATION[]{
         NO_FILL,
         HALF_FILL,
         DOUBLE_FILL,

         NO_ROOM_CHANCE,
         HALF_ROOM_CHANCE,
         DOUBLE_ROOM_CHANCE,


        };
    }

    public static LEVEL_DATA_MODIFICATION[] getRandomizedMods(LOCATION_TYPE_GROUP locationType, Boolean random) {
        List<LEVEL_DATA_MODIFICATION> mods = new ArrayList<>();
        for (LEVEL_DATA_MODIFICATION mod : getModsForGroup(locationType, random)) {
            mods.add(mod);
        }
        for (LEVEL_DATA_MODIFICATION mod : getCommonMods()) {
            mods.add(mod);
        }
        return mods.toArray(new LEVEL_DATA_MODIFICATION[mods.size()]);
    }

    public static LEVEL_DATA_MODIFICATION[] getModsForGroup(
     LOCATION_TYPE_GROUP locationType, Boolean random) {
        if (random == null)
            random = RandomWizard.random();
        switch (locationType) {
            case SURFACE:
                return random
                 ? new LEVEL_DATA_MODIFICATION[0]
                 : new LEVEL_DATA_MODIFICATION[]{
                 NO_LINKS,
                 DECREASE_ROOM_COUNT,
                 INCREASE_SIZE,
                };
            case WIDE:
                return new LEVEL_DATA_MODIFICATION[0];
            case AVERAGE:
                return random
                 ? new LEVEL_DATA_MODIFICATION[]{
                 SHORTEN_MAIN_PATHS,
                 LENGTHEN_BONUS_PATHS,
                 DOUBLE_MAIN_PATHS,
                 HALF_BONUS_PATHS,
                }
                 : new LEVEL_DATA_MODIFICATION[]{
                 LENGTHEN_MAIN_PATHS,
                 SHORTEN_BONUS_PATHS,
                 DOUBLE_BONUS_PATHS,
                };
            case NARROW:
                return random
                 ? new LEVEL_DATA_MODIFICATION[]{
                 NO_LINKLESS,
                 LENGTHEN_MAIN_PATHS,
                 INCREASE_ROOM_COUNT,
                }
                 : new LEVEL_DATA_MODIFICATION[]{

                };
            case NATURAL:
                return random
                 ? new LEVEL_DATA_MODIFICATION[]{
                 NO_DOORS,
                 DOUBLE_FILL
                }:
                 new LEVEL_DATA_MODIFICATION[]{
                  NO_DOORS,
                  DOUBLE_FILL,
                  HALF_FILL
                 };
            case NATURAL_SURFACE:
                return new LEVEL_DATA_MODIFICATION[]{
                 NO_DOORS,
                };
        }
        return new LEVEL_DATA_MODIFICATION[0];
    }

    public static LEVEL_DATA_MODIFICATION[] getStdTypeMods(SUBLEVEL_TYPE type) {
        switch (type) {

            case COMMON:
                return new LEVEL_DATA_MODIFICATION[]{

                };
            case PRE_BOSS:
            return new LEVEL_DATA_MODIFICATION[]{
             DECREASE_ROOM_COUNT,
             DECREASE_SIZE,
            };
            case BOSS:
                return new LEVEL_DATA_MODIFICATION[]{
                 DECREASE_ROOM_COUNT,
                 DECREASE_ROOM_COUNT,
                 DECREASE_SIZE,
                };
        }
        return new LEVEL_DATA_MODIFICATION[0];
    }

    public static Object[] getModArgs(LEVEL_DATA_MODIFICATION[] mods,
                                      LOCATION_TYPE_GROUP group) {

        Object[] args = new Object[mods.length];
        for (int i = 0; i < mods.length; i++) {
            Integer n = 1;
            //            TODO RandomWizard.getRandomFloatBetween()
            switch (mods[i]) {

                case LENGTHEN_MAIN_PATHS:
                    break;
                case LENGTHEN_BONUS_PATHS:
                    break;
                case SHORTEN_MAIN_PATHS:
                    break;
                case SHORTEN_BONUS_PATHS:
                    break;

                case DOUBLE_MAIN_PATHS:
                    break;
                case HALF_BONUS_PATHS:
                    break;
                case DOUBLE_BONUS_PATHS:
                    break;

                case INCREASE_SIZE:
                    break;
                case INCREASE_ROOM_COUNT:
                    break;
                case DECREASE_SIZE:
                    break;
                case DECREASE_ROOM_COUNT:
                    break;


                case NO_FILL:
                    n = 0;
                case DOUBLE_FILL:
                    if (n != 0)
                        n = 2;
                case HALF_FILL:
                    args[i] = getFillerMod(group, n);
                    break;

                case NO_ROOM_CHANCE:
                    n = 0;
                case DOUBLE_ROOM_CHANCE:
                    if (n != 0)
                        n = 2;
                case HALF_ROOM_CHANCE:
                    args[i] = getRoomMod(group, n);
                    break;

            }
        }

        return args;
    }

    private static Object getFillerMod(LOCATION_TYPE_GROUP locationType, Integer n) {
        switch (locationType) {
            case SURFACE:
            case NATURAL_SURFACE:
                switch (n) {
                    case 0:
                        return ROOM_CELL.WALL_WITH_LIGHT_OVERLAY;
                    case 1:
                        return ROOM_CELL.DESTRUCTIBLE;
                    case 2:
                        return ROOM_CELL.LIGHT_EMITTER;
                }
                break;
            case WIDE:
                switch (n) {
                    case 0:
                        return null;
                    case 1:
                        return ROOM_CELL.CONTAINER;
                    case 2:
                        return ROOM_CELL.DESTRUCTIBLE;
                }
            case NARROW:
                switch (n) {
                    case 0:
                        return ROOM_CELL.DESTRUCTIBLE;
                    case 1:
                        return ROOM_CELL.LIGHT_EMITTER;
                    case 2:
                        return ROOM_CELL.WALL_WITH_LIGHT_OVERLAY;
                }
            case NATURAL:
                switch (n) {
                    case 0:
                        return ROOM_CELL.SPECIAL_CONTAINER;
                    case 1:
                        return ROOM_CELL.DESTRUCTIBLE;
                    case 2:
                        return ROOM_CELL.WALL_WITH_DECOR_OVERLAY;
                }
        }
        return null;
    }

    private static Object getRoomMod(LOCATION_TYPE_GROUP locationType, Integer n) {
        switch (locationType) {
            case SURFACE:
                break;
            case WIDE:
                break;
            case AVERAGE:
                break;
            case NARROW:
                break;
            case NATURAL:
                break;
            case NATURAL_SURFACE:
                switch (n) {
                    case 0:
                        return ROOM_CELL.SPECIAL_CONTAINER;
                    case 1:
                        return ROOM_CELL.DESTRUCTIBLE;
                    case 2:
                        return ROOM_CELL.WALL_WITH_DECOR_OVERLAY;
                }
                return ROOM_TYPE.SECRET_ROOM;
        }
        return null;

    }

    public static void applyMod(float randomizationMod, LevelData data, LEVEL_DATA_MODIFICATION mod, Object arg) {
        float n = 0.5f;
        switch (mod) {
            case NO_ROOM_CHANCE:
                n = 0;
            case DOUBLE_ROOM_CHANCE:
                if (n != 0)
                    n = 2;
            case HALF_ROOM_CHANCE:
                if (arg == null)
                    return;
                LEVEL_VALUES val = LevelData.getROOM_COEF((ROOM_TYPE) arg);
                data.setValue(val, Math.round(data.getIntValue(val)
                 * getMod(n, randomizationMod)) + "");
                break;

            case NO_FILL:
                n = 0;
            case DOUBLE_FILL:
                if (n != 0)
                    n = 2;
            case HALF_FILL:
                if (arg == null)
                    return;
                val = LevelData.getFillCoefValue((ROOM_CELL) arg);
                data.setValue(val, Math.round(data.getIntValue(val)
                 * getMod(n, randomizationMod)) + "");
                break;

            case NO_RANDOM_EXITS:
                data.setValue(LEVEL_VALUES.RANDOM_EXIT_CHANCE, "0");
                break;
            case NO_RANDOM_ROTATIONS:
                data.setValue(LEVEL_VALUES.RANDOM_ROTATION_CHANCE, "0");
                break;
            case NO_LINKLESS:
                data.setValue(LEVEL_VALUES.CHANCE_LINKLESS_MOD, "0");
                break;
            case NO_LINKS:
                data.setValue(LEVEL_VALUES.CHANCE_LINKLESS, "100");
                break;
            case NO_DOORS:
                data.setValue(LEVEL_VALUES.DOOR_CHANCE_MOD, "0");
                break;


            case WRAP_ROOMS:
                break;
            case WRAP_TYPE:
                break;

            case LENGTHEN_MAIN_PATHS:
                n = 2;
            case SHORTEN_MAIN_PATHS:
                if (arg instanceof Float)
                    n = (float) arg;
                data.setValue(LEVEL_VALUES.MAIN_PATH_LENGTH,
                 Math.round(data.getIntValue(LEVEL_VALUES.MAIN_PATH_LENGTH) * n) + "");

                break;

            case LENGTHEN_BONUS_PATHS:
                n = 2;
            case SHORTEN_BONUS_PATHS:
                if (arg instanceof Float)
                    n = (float) arg;
                data.setValue(LEVEL_VALUES.BONUS_PATH_LENGTH,
                 Math.round(data.getIntValue(LEVEL_VALUES.BONUS_PATH_LENGTH)
                  * getMod(n, randomizationMod)) + "");

                break;

            case DOUBLE_BONUS_PATHS:
                n = 2;
            case HALF_BONUS_PATHS:
                if (arg instanceof Float)
                    n = (float) arg;
                data.setValue(LEVEL_VALUES.BONUS_PATHS,
                 Math.round(data.getIntValue(LEVEL_VALUES.BONUS_PATHS) * getMod(n, randomizationMod)) + "");

                break;

            case DOUBLE_MAIN_PATHS:
                n = 2;
                if (arg instanceof Float)
                    n = (float) arg;
                data.setValue(LEVEL_VALUES.MAIN_PATHS,
                 Math.round(data.getIntValue(LEVEL_VALUES.MAIN_PATHS) * getMod(n, randomizationMod)) + "");
                break;

            case HALF_ZONES:
                data.setValue(LEVEL_VALUES.ZONES,
                 Math.round(data.getIntValue(LEVEL_VALUES.ZONES) * n) + "");

                break;
            case SINGLE_ZONE:
                data.setValue(LEVEL_VALUES.ZONES, 1 + "");

                break;
            case INCREASE_SIZE:
                n = 2;
            case DECREASE_SIZE:
                if (arg instanceof Float)
                    n = (float) arg;
                data.setValue(LEVEL_VALUES.SIZE_MOD,
                 Math.round(data.getIntValue(LEVEL_VALUES.SIZE_MOD) * getMod(n, randomizationMod)) + "");

                break;
            case INCREASE_ROOM_COUNT:
                n = 2;
            case DECREASE_ROOM_COUNT:
                if (arg instanceof Float)
                    n = (float) arg;
                data.setValue(LEVEL_VALUES.ROOM_COUNT_MOD,
                 Math.round(data.getIntValue(LEVEL_VALUES.SIZE_MOD) * getMod(n, randomizationMod)) + "");
                break;
        }
    }

    private static float getMod(float n, float randomizationMod) {
        if (n == 0) {
            return 0;
        }
        if (n > 1)
            return 1 + (n - 1) * randomizationMod;
        return (1 - (1 - n) * randomizationMod);
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
            case TOWER:
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
         ROOM_CELL.INDESTRUCTIBLE.getSymbol() +
          RoomModelTransformer.WRAP_SEPARATOR +
          ROOM_CELL.FLOOR.getSymbol());
        data.setValue(LEVEL_VALUES.VOID_CELL_TYPE,
         ROOM_CELL.FLOOR.getSymbol());
        data.setValue(LEVEL_VALUES.WRAP_ROOMS, "2");
        data.setValue(LEVEL_VALUES.SIZE_MOD,
         data.getIntValue(LEVEL_VALUES.SIZE_MOD)*2 + "");
        data.setValue(LEVEL_VALUES.ROOM_COUNT_MOD,
         data.getIntValue(LEVEL_VALUES.ROOM_COUNT_MOD)*2 + "");

        data.setValue(LEVEL_VALUES.SURFACE, "true");
    }

    private static ROOM_TEMPLATE_GROUP[] getTemplatesForLocationType(LOCATION_TYPE locationType) {
        if (LevelGenerator.TEST_MODE)
            locationType = LevelGenerator.TEST_LOCATION_TYPE;
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
            case TOWER:
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
                        return Math.round(18 * roomMod);
                    case PRE_BOSS:
                    case BOSS:
                        return Math.round(12 * roomMod);
                }
            case minRooms:
                switch (sublevelType) {
                    case COMMON:
                        return Math.round(12 * roomMod);
                    case PRE_BOSS:
                    case BOSS:
                        return Math.round(6 * roomMod);
                }
            case minFillRatio:
                return 0.5f;
            case minDimensionRatio:
                switch (sublevelType) {
                    case COMMON:
                        return 0.4f;
                    case PRE_BOSS:
                        return 0.35f;
                    case BOSS:
                        return 0.25f;
                }
            case maxSquare:
                return (int) Math.pow((int) getReq(LEVEL_REQUIREMENTS.maxDimension, sublevelType, type, data), 2)
                 * 5 / 4;
            case maxDimension:
                //level.getModel().getData().getROOM_COEF()
                switch (sublevelType) {
                    case COMMON:
                        return Math.round(60 * sizeMod);
                    case PRE_BOSS:
                    case BOSS:
                        return Math.round(45 * sizeMod);
                }
        }
        return null;
    }

    public static void randomize(float randomizationMod, Boolean random, LevelData data, GenerationStats stats) {
        LOCATION_TYPE_GROUP group = data.getLocationType().getGroup();
        LEVEL_DATA_MODIFICATION[] mods =  getRandomizedMods(group, random);
        Object[] args = getModArgs(mods, group);
        int i = 0;
        for (LEVEL_DATA_MODIFICATION mod : mods) {
            stats.modAdded(mod, args[i]);
            applyMod(randomizationMod, data, mod, args[i++]);
        }
        mods = getStdTypeMods(data.getSublevelType());
        args = getModArgs(mods, group);
        i = 0;
        for (LEVEL_DATA_MODIFICATION mod : mods) {
            stats.modAdded(mod, args[i]);
            applyMod(randomizationMod, data, mod, args[i++]);
        }

    }


    public enum LEVEL_REQUIREMENTS {
        minRooms, maxRooms, minFillRatio, minDimensionRatio, maxDimension, maxSquare;
    }

    //    public static LevelData generateDataViaDialog(ObjType place, ObjType dungeon){
    ////        DialogMaster.inputInt()
    //    }


}
