package main.client.battle;

import main.client.battle.Battle.BATTLE_STATS;
import main.system.net.data.DataUnit;

public class Battle extends DataUnit<BATTLE_STATS> {
    // NEW: make it useful dynamically (keep record) and statically (save
    // record)

    private Boolean outcome;
    private Integer glory;

    public int getLevel() {
        return getIntValue(BATTLE_STATS.LEVEL);

    }

    public int getRound() {
        return getIntValue(BATTLE_STATS.ROUND);
    }

    public Boolean getOutcome() {
        return outcome;
    }

    public void setOutcome(Boolean outcome) {
        this.outcome = outcome;
    }

    public Integer getGlory() {
        return glory;
    }

    public void setGlory(Integer glory) {
        this.glory = glory;
    }

    public enum BATTLE_STATS {
        LEVEL, ROUND, PLAYER_STARTING_PARTY,
    }

}
