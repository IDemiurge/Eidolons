package main.game.battlecraft.logic.battle.universal.stats;

import main.content.values.parameters.PARAMETER;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battle.universal.stats.BattleStatManager.COMBAT_STATS;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 8/3/2017.
 */
public class UnitStats {
    Map<PARAMETER, Integer> allyModMap = new HashMap<>();
    Map<PARAMETER, Integer> enemyModMap = new HashMap<>();
    private Unit unit;
    private Map<Unit, Integer> killsMap = new HashMap<>();
    private Map<COMBAT_STATS, Integer> statMap = new HashMap<>();


    public UnitStats(Unit unit) {
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }

    public Map<Unit, Integer> getKillsMap() {
        return killsMap;
    }

    public Map<COMBAT_STATS, Integer> getStatMap() {
        return statMap;

    }

    public Map<PARAMETER, Integer> getAllyModMap() {
        return allyModMap;
    }

    public Map<PARAMETER, Integer> getEnemyModMap() {
        return enemyModMap;
    }
}
