package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.game.battlecraft.logic.battle.universal.*;
import eidolons.game.battlecraft.logic.battle.universal.stats.BattleStatManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/8/2017.
 */
public class DungeonHandler<E extends DungeonWrapper> {

    protected DC_Game game;
    protected DungeonMaster<E> master;

    public DungeonHandler(DungeonMaster<E> master) {
        this.master = master;
        this.game = master.getGame();
    }

    public DC_Game getGame() {
        return master.getGame();
    }

    public DungeonMaster<E> getMaster() {
        return master;
    }

    public MetaGameMaster getMetaMaster() {
        return getGame().getMetaMaster();
    }

    public DungeonInitializer<E> getInitializer() {
        return master.getInitializer();
    }

    public DungeonBuilder getBuilder() {
        return master.getBuilder();
    }

    public Positioner<E> getPositioner() {
        return master.getPositioner();
    }

    public FacingAdjuster<E> getFacingAdjuster() {
        return master.getFacingAdjuster();
    }

    public PlayerManager getPlayerManager() {
        return master.getPlayerManager();
    }

    public BattleMaster getBattleMaster() {
        return master.getBattleMaster();
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

    public Battle getBattle() {
        return master.getBattle();
    }

    public Spawner<E> getSpawner() {
        return master.getSpawner();
    }

    public E getDungeon() {
        return master.getDungeonWrapper();
    }
}
