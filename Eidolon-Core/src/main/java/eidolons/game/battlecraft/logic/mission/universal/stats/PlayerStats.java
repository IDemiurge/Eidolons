package eidolons.game.battlecraft.logic.mission.universal.stats;

import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.battlecraft.logic.mission.universal.stats.MissionStatManager.STAT;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 8/3/2017.
 */
public class PlayerStats {
    DC_Player player;
    Map<Unit, Integer> deathsMap = new HashMap<>();
    Map<STAT, Integer> statsMap = new HashMap<>();

    public PlayerStats(DC_Player player) {
        this.player = player;
    }

    public DC_Player getPlayer() {
        return player;
    }

    public Map<STAT, Integer> getStatsMap() {
        return statsMap;
    }

    public Map<Unit, Integer> getDeathsMap() {
        return deathsMap;
    }
}
