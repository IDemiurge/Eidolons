package eidolons.game.module.dungeoncrawl.generator.test;

import eidolons.game.module.dungeoncrawl.generator.test.GenerationStats.GEN_STAT;
import main.system.data.DataUnit;

/**
 * Created by JustMe on 8/2/2018.
 */
public class GenerationStats extends DataUnit<GEN_STAT>{
    public enum GEN_STAT {
        PASS_PERCENTAGE,
        CRITICAL_FAIL_PERCENTAGE,

    }
    public int rate() {
        return 0;
    }
}
