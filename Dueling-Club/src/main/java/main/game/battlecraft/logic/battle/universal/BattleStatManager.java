package main.game.battlecraft.logic.battle.universal;

import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.core.game.StatMaster;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

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

    public void unitDies(Unit killed) {
    }

    public BattleStats getStats() {
        return stats;
    }

    @Override
    public void eventBeingHandled(Event event) {
        if (event.getType() instanceof STANDARD_EVENT_TYPE) {
            event.getRef().getAmount();

            switch ((STANDARD_EVENT_TYPE) event.getType()){
                case UNIT_HAS_BEEN_KILLED:
//                    unitDies();
                    stat(COMBAT_STATS.UNITS_SLAIN,
                     event.getRef().getSourceObj(),
                     event.getRef().getTargetObj());
            }

        }
    }

    private void stat(STAT stat, Obj sourceObj, Obj targetObj) {
    }
public interface STAT{

}
    public enum COMBAT_STATS implements STAT{
        ACTION_USED,
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

}
