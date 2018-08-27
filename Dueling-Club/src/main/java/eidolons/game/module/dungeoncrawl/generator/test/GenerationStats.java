package eidolons.game.module.dungeoncrawl.generator.test;

import eidolons.game.module.dungeoncrawl.generator.test.GenerationStats.GEN_STAT;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.data.DataUnit;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 8/2/2018.
 */
public class GenerationStats extends DataUnit<GEN_STAT>{
    LOCATION_TYPE locationType;
    SUBLEVEL_TYPE type;

    public GenerationStats(LOCATION_TYPE locationType, SUBLEVEL_TYPE type) {
        this.locationType = locationType;
        this.type = type;
    }

    public void addCount(GEN_STAT stat, String val) {
        GEN_STAT mapVal = new EnumMaster<GEN_STAT>().retrieveEnumConst(GEN_STAT.class, stat + "_MAP");
        WeightMap<String> map = new WeightMap<>( getValue(mapVal), String.class);
        MapMaster.addToIntegerMap(map, val, 1);
        setValue(mapVal, map.toString());
        addValue(stat, val);

    }

    @Override
    public String toString() {
        String data = "Generation stats for "+type+" "+locationType+":\n";
        for (String v : getValues().keySet()) {
            data += v + "="
             + values.get(v) + "\n";
        }
        return data;
    }

    public enum GEN_STAT {
        PASS_PERCENTAGE,
        CRITICAL_FAIL_PERCENTAGE,
        FAIL_REASONS,
        FAIL_REASONS_MAP,

        /*
        info on the whole gen process for a batch
        averages
        max/min
        sums

what will I want to look for?

quality criteria:
balance of template/room types
(substitution success)

graph adherence

corner room types

distances

useful info:
rooms added via finalizer


         */

        AVRG_ROOM_TYPES,
        AVRG_EXITS_DISTANCE,
        AVRG_ZONE_BALANCE,
        AVRG_FILL_RATIO,
        AVRG_RATE, SUCCESS_RATE,

    }
    public int rate() {
        return 0;
    }
}
