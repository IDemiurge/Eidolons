package main.game.battlecraft.logic.battle.universal;

import main.entity.obj.unit.Unit;

/**
 * Created by JustMe on 5/7/2017.
 * <p>
 * in demo, what stats will we want?
 * not only battle stats...
 * Usability stats!
 */
public class BattleStatManager<E extends Battle> extends BattleHandler<E> {
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

    public enum COMBAT_STATS {
        ACTION_USED,
        DAMAGE_DEALT,
        UNITS_SLAIN,
    }

    public enum STAT_WATCHER {
        COMBAT,
        USABILITY,
        AI,

    }

    public enum USABILITY_STATS {
        INFO_PANEL_OPENED,
        RADIAL_MENU_OPENED,
        GAME_MENU,
        OPTIONS_MENU,
        MAIN_MENU,

        BOTTOM_SPELL_CLICKED,


    }

}
