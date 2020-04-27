package eidolons.game.battlecraft.logic.mission.universal.stats;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.mission.universal.stats.MissionStatManager.COMBAT_STATS;
import main.content.values.parameters.PARAMETER;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by JustMe on 8/3/2017.
 */
public class UnitStats {
    Map<PARAMETER, Integer> allyModMap = new LinkedHashMap<>();
    Map<PARAMETER, Integer> enemyModMap = new LinkedHashMap<>();
    private Unit unit;
    private Map<Unit, Integer> killsMap = new LinkedHashMap<>();
    private Map<COMBAT_STATS, Integer> statMap = new LinkedHashMap<>();
    private Map<String, Integer> generalStats= new  LinkedHashMap<>();


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

    public Map<String, Integer> getGeneralStats() {
        return generalStats;
    }

}
