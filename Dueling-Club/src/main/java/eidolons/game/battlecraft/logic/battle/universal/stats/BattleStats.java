package eidolons.game.battlecraft.logic.battle.universal.stats;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import main.data.XLinkedMap;
import eidolons.game.battlecraft.logic.battle.universal.stats.BattleStats.BATTLE_STATS;
import main.system.data.DataUnit;
import main.system.datatypes.DequeImpl;

import java.util.Map;

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

    private Map<Unit, UnitStats> unitStatMap = new XLinkedMap<>();
    private Map<DC_Player, PlayerStats> playerStatMap = new XLinkedMap<>();

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

    public Map<Unit, UnitStats> getUnitStatMap() {
        return unitStatMap;
    }

    public Map<DC_Player, PlayerStats> getPlayerStatMap() {
        return playerStatMap;
    }

    public PlayerStats getPlayerStats(DC_Player player) {
        if (!getPlayerStatMap().containsKey(player)) {
            getPlayerStatMap().put(player, new PlayerStats(player));
        }
        return getPlayerStatMap().get(player);
    }

    public UnitStats getUnitStats(Unit unit) {
        if (!getUnitStatMap().containsKey(unit)) {
            getUnitStatMap().put(unit, new UnitStats(unit));
        }
        return getUnitStatMap().get(unit);
    }

    public enum BATTLE_STATS {
        LEVEL, ROUND, PLAYER_STARTING_PARTY,
    }

}
