package main.game.battlecraft.logic.battle;

import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/7/2017.
 */
public class BattleHandler<E extends Battle> {

    protected DC_Game game;
    protected BattleMaster<E> master;

    public BattleHandler(BattleMaster<E> master) {
        this.master = master;
        this.game = master.getGame();
    }

    public BattleMaster<E> getMaster() {
        return master;
    }

    public E getBattle() {
        return master.getBattle();
    }

    public BattleOptionManager getOptionManager() {
        return master.getOptionManager();
    }

    public BattleStatManager getStatManager() {
        return master.getStatManager();
    }

    public BattleConstructor getConstructor() {
        return master.getConstructor();
    }

    public BattleOutcomeManager getOutcomeManager() {
        return master.getOutcomeManager();
    }
}
