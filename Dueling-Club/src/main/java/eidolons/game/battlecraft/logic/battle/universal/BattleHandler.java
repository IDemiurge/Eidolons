package eidolons.game.battlecraft.logic.battle.universal;

import eidolons.game.battlecraft.logic.battle.encounter.EncounterAdjuster;
import eidolons.game.battlecraft.logic.battle.encounter.EncounterSpawner;
import eidolons.game.battlecraft.logic.battle.universal.stats.BattleStatManager;
import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.core.game.DC_Game;

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

    public DC_Game getGame() {
        return game;
    }

    public BattleMaster<E> getMaster() {
        return master;
    }

    public E getBattle() {
        return master.getBattle();
    }

    public PlayerManager getPlayerManager() {
        return master.getPlayerManager();
    }

    public Positioner getPositioner() {
        return master.getPositioner();
    }

    public Spawner getSpawner() {
        return master.getSpawner();
    }
    public EncounterSpawner getEncounterSpawner() {
        return master.getEncounterSpawner();
    }
    public EncounterAdjuster getEncounterAdjuster() {
        return master.getEncounterAdjuster();
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
