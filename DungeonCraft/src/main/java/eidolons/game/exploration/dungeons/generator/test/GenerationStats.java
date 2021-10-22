package eidolons.game.exploration.dungeons.generator.test;

import eidolons.game.exploration.dungeons.generator.GeneratorEnums;
import eidolons.game.exploration.dungeons.generator.test.GenerationStats.GEN_STAT;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnit;

/**
 * Created by JustMe on 8/2/2018.
 */
public class GenerationStats extends DataUnit<GEN_STAT> {
    LOCATION_TYPE locationType;
    SUBLEVEL_TYPE type;

    public GenerationStats(LOCATION_TYPE locationType, SUBLEVEL_TYPE type) {
        this.locationType = locationType;
        this.type = type;
    }

    public void modAdded(GeneratorEnums.LEVEL_DATA_MODIFICATION mod, Object arg) {
        addValue(GEN_STAT.LEVEL_DATA_MODIFICATIONS,
         mod+ StringMaster.wrapInParenthesis(arg+""));
    }


    @Override
    public String toString() {
        String data = "Generation stats for " + type + " " + locationType + ":\n";
        for (String v : getValues().keySet()) {
            data += v + "="
             + values.get(v) + "\n";
        }
        return data;
    }

    public int rate() {
        return 0;
    }

    public enum GEN_STAT {
        PASS_PERCENTAGE,
        CRITICAL_FAIL_PERCENTAGE,
        FAIL_REASONS,
        FAIL_REASONS_MAP,
        LEVEL_DATA_MODIFICATIONS,
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
}
