package eidolons.game.battlecraft.logic.mission.universal.stats;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.battlecraft.logic.mission.universal.stats.BattleStats.BATTLE_STATS;
import eidolons.game.battlecraft.logic.mission.universal.stats.MissionStatManager.PLAYER_STATS;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import main.data.XLinkedMap;
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
    private Integer glory=0;
    private DequeImpl<Unit> slainSummonedAllies = new DequeImpl<>();
    private DequeImpl<Unit> slainSummonedEnemies = new DequeImpl<>();
    private DequeImpl<Unit> slainEnemyUnits = new DequeImpl<>();
    private DequeImpl<Unit> slainPlayerUnits = new DequeImpl<>();
    private DequeImpl<Unit> destroyedObjects = new DequeImpl<>();

    private Map<Unit, UnitStats> unitStatMap = new XLinkedMap<>();
    private Map<DC_Player, PlayerStats> playerStatMap = new XLinkedMap<>();
    private Map<String, Integer> mainStatMap = new XLinkedMap<>();

    DC_Game game;

    public BattleStats(DC_Game game) {
        this.game = game;
    }

    public int getLevel() {
        return getIntValue(BATTLE_STATS.LEVEL);

    }

    @Override
    public String toString() {
        String stats = "";
        Map<String, Integer> map = getUnitStats(Eidolons.getMainHero()).getGeneralStats();
        for (String s : map.keySet()) {
            stats+="\n" + s + ": " + map.get(s);
        }
        return stats;
    }

    public Map<String, Integer> getMainStatMap() {
        return mainStatMap;
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
    public void addGlory(Integer glory) {
        this.glory += glory;
        game.getLogManager().log(
                "Glory " +
                        (glory>0 ? "gained" : "lost") +
                        ": " + glory + ", total " + this.glory);
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

    public UnitStats getHeroStats() {
        return game.getMissionMaster().getStatManager().
         getStats().getUnitStats(Eidolons.getMainHero());
    }
    private PlayerStats getPlayerStats() {
        return game.getMissionMaster().getStatManager().
         getStats().getPlayerStats(game.getPlayer(true));
    }

    private Integer getPlayerStat(PLAYER_STATS stat) {
        return getPlayerStats().getStatsMap().get(stat);
    }

    public Integer getUnitsSlain() {
        return getPlayerStat(PLAYER_STATS.ALLY_ENEMIES_KILLED);
    }

    public Integer getALLIES_DIED() {
        return getPlayerStat(PLAYER_STATS.ALLIES_DIED);
    }

    public Integer getDAMAGE_DEALT() {
        return getPlayerStat(PLAYER_STATS.ALLIES_DAMAGE_DEALT);
    }

    public Integer getDAMAGE_TAKEN() {
        return getPlayerStat(PLAYER_STATS.ALLIES_DAMAGE_TAKEN);
    }

    public enum BATTLE_STATS {
        LEVEL, ROUND, PLAYER_STARTING_PARTY,
    }

}
