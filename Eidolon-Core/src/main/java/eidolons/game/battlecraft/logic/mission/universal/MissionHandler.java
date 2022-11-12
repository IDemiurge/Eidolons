package eidolons.game.battlecraft.logic.mission.universal;

import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.battlecraft.logic.mission.encounter.EncounterAdjuster;
import eidolons.game.battlecraft.logic.mission.encounter.EncounterSpawner;
import eidolons.game.battlecraft.logic.mission.universal.stats.MissionStatManager;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/7/2017.
 */
public class MissionHandler<E extends DungeonSequence> {

    protected DC_Game game;
    protected MissionMaster<E> master;

    public MissionHandler(MissionMaster<E> master) {
        this.master = master;
        this.game = master.getGame();
    }

    public DC_Game getGame() {
        return game;
    }

    public MissionMaster<E> getMaster() {
        return master;
    }

    public E getMission() {
        return master.getMission();
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

    public MissionOptionManager getOptionManager() {
        return master.getOptionManager();
    }

    public MissionStatManager getStatManager() {
        return master.getStatManager();
    }

    public MissionConstructor getConstructor() {
        return master.getConstructor();
    }

}
