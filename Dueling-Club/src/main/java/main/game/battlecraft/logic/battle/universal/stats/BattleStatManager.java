package main.game.battlecraft.logic.battle.universal.stats;

import main.content.ContentManager;
import main.content.values.parameters.PARAMETER;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battle.universal.Battle;
import main.game.battlecraft.logic.battle.universal.BattleHandler;
import main.game.battlecraft.logic.battle.universal.BattleMaster;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.core.game.StatMaster;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.event.EventType;
import main.system.auxiliary.data.MapMaster;

import java.util.Map;

/**
 * Created by JustMe on 5/7/2017.
 * <p>
 * in demo, what stats will we want?
 * not only battle stats...
 * Usability stats!
 */
public class BattleStatManager<E extends Battle> extends BattleHandler<E> implements StatMaster {
    BattleStats stats;

    public BattleStatManager(BattleMaster master) {
        super(master);
    }

    private void attachStatWatcher() {

    }

    private void initializeBattle() {
        stats = new BattleStats();
//        battle.setValue(BATTLE_STATS.PLAYER_STARTING_PARTY, game.getPlayerParty());
//        battle.setValue(BATTLE_STATS.LEVEL, getBattleLevel() + "");
//        battle.setValue(BATTLE_STATS.ROUND, "1");

    }


    public BattleStats getStats() {
        return stats;
    }

    @Override
    public void eventBeingHandled(Event event) {
        Unit source = (Unit) event.getRef().getSourceObj();
        Unit target = (Unit) event.getRef().getSourceObj();
        if (event.getType() instanceof STANDARD_EVENT_TYPE) {
            event.getRef().getAmount();

            switch ((STANDARD_EVENT_TYPE) event.getType()) {

                case UNIT_HAS_BEEN_KILLED:
                    unitKilled(source, target);
                    modifyPlayerStat(COMBAT_STATS.UNITS_SLAIN,
                     source.getOwner(), 1);
                    modifyUnitStat(COMBAT_STATS.UNITS_SLAIN, source, 1);
            }

        } else {
            if (event.getType() instanceof EventType) {
                modifyUnitModStat(target.isHostileTo(source.getOwner()), (event.getType()).getArg()
                 , source, event.getRef().getAmount());

            }
        }
    }

    private void modifyUnitModStat(boolean hostile, String stat, Unit sourceObj, int mod) {
        PARAMETER p = ContentManager.getPARAM(stat);
        Map<PARAMETER, Integer> map = hostile ? stats.getUnitStats(sourceObj).getEnemyModMap()
         : stats.getUnitStats(sourceObj).getAllyModMap();
        MapMaster.addToIntegerMap(
         map, p, mod);

    }

    private void modifyPlayerStat(COMBAT_STATS stat, DC_Player owner, int i) {
        MapMaster.addToIntegerMap(
         stats.getPlayerStats(owner).getStatsMap(), stat, i);
    }

    public void unitKilled(Unit killer, Unit killed) {
        stats.getUnitStats(killer).getKillsMap().put(killed, getGame().getState().getRound());
    }

    private void modifyUnitStat(STAT stat, Unit sourceObj, int mod) {
        MapMaster.addToIntegerMap(
         stats.getUnitStats(sourceObj).getStatMap(), stat, mod);
    }

    private void stat(STAT stat, Obj sourceObj, Obj targetObj) {

    }

    public enum PLAYER_STATS implements STAT {
        UNITS_DIED,
        UNITS_KILLED,

    }
        public enum COMBAT_STATS implements STAT {
        ACTION_USED,
        ALLY_ENDURANCE_MODIFIED,
        DAMAGE_CREATED,
        DAMAGE_DEALT,
        UNITS_SLAIN,
    }

    public enum STAT_WATCHER {
        COMBAT,
        USABILITY,
        AI,

    }

    // use AspectJ !
    public enum USABILITY_STATS {
        INFO_PANEL_OPENED,
        RADIAL_MENU_OPENED,
        GAME_MENU,
        OPTIONS_MENU,
        MAIN_MENU,

        BOTTOM_SPELL_CLICKED,


    }

    public interface STAT {

    }

}
