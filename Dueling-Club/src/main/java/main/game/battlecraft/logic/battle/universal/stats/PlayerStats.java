package main.game.battlecraft.logic.battle.universal.stats;

import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.battlecraft.logic.battle.universal.stats.BattleStatManager.STAT;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 8/3/2017.
 */
public class PlayerStats {
    DC_Player player;
    Map<Unit, Integer> deathsMap= new HashMap<>();
    Map<STAT, Integer> statsMap= new HashMap<>();

    public PlayerStats(DC_Player player) {
        this.player=player;
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
