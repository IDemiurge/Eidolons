package eidolons.game.module.dungeoncrawl.generator.pregeneration;

import eidolons.game.module.dungeoncrawl.generator.pregeneration.PregeneratorData.PREGENERATOR_VALUES;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.system.data.DataUnit;

/**
 * Created by JustMe on 8/18/2018.
 */
public class PregeneratorData extends DataUnit<PREGENERATOR_VALUES> {
    SUBLEVEL_TYPE[] sublevelTypes;
    LOCATION_TYPE[] locationTypes;

    public PregeneratorData(String text, SUBLEVEL_TYPE[] sublevelTypes, LOCATION_TYPE[] locationTypes) {
        super(text);
        this.sublevelTypes = sublevelTypes;
        this.locationTypes = locationTypes;
    }

    public enum PREGENERATOR_VALUES {
        ATTEMPTS,
        QUALITY_LEVEL,
        GLOBAL_RANDOMNESS,
        LEVELS_REQUIRED,

/*
reqs
flags
data 'offsets'
location/subtype

 */
    }
}
