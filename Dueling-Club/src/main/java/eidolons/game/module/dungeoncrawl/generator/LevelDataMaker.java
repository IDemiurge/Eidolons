package eidolons.game.module.dungeoncrawl.generator;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.system.data.DataUnitFactory;

import java.util.Arrays;
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
        return data;
    }

//    public static LevelData generateDataViaDialog(ObjType place, ObjType dungeon){
////        DialogMaster.inputInt()
//    }


}
