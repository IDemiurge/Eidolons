package main.game.battlecraft.logic.battle.universal;

import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battle.universal.BattleStats.BATTLE_STATS;
import main.system.data.DataUnit;
import main.system.datatypes.DequeImpl;

/**
 * Created by JustMe on 5/7/2017.
 */
public class BattleStats extends DataUnit<BATTLE_STATS> {
    // NEW: make it useful dynamically (keep record) and statically (save
    // record)

    private Boolean outcome;
    private Integer glory;
    private DequeImpl<Unit> slainSummonedAllies = new DequeImpl<>();
    private DequeImpl<Unit> slainSummonedEnemies = new DequeImpl<>();
    private DequeImpl<Unit> slainEnemyUnits = new DequeImpl<>();
    private DequeImpl<Unit> slainPlayerUnits = new DequeImpl<>();
    private DequeImpl<Unit> destroyedObjects = new DequeImpl<>();

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

    public DequeImpl<Unit> getSlainSummonedAllies() {
        return slainSummonedAllies;
    }

    public DequeImpl<Unit> getSlainSummonedEnemies() {
        return slainSummonedEnemies;
    }

    public DequeImpl<Unit> getSlainEnemyUnits() {
        return slainEnemyUnits;
    }

    public DequeImpl<Unit> getSlainPlayerUnits() {
        return slainPlayerUnits;
    }

    public DequeImpl<Unit> getDestroyedObjects() {
        return destroyedObjects;
    }

    public enum BATTLE_STATS {
        LEVEL, ROUND, PLAYER_STARTING_PARTY,
    }

}
