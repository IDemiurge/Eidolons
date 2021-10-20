package eidolons.game.battlecraft.logic.mission.universal;

import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Floor;
import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.mission.encounter.EncounterAdjuster;
import eidolons.game.battlecraft.logic.mission.encounter.EncounterSpawner;
import eidolons.game.battlecraft.logic.mission.universal.stats.MissionStatManager;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/7/2017.
 */
public abstract class MissionMaster<E extends DungeonSequence> {

    protected E mission;
    protected MissionOptionManager optionManager;
    protected MissionStatManager statManager;
    protected MissionConstructor constructor;
    protected PlayerManager playerManager;
    protected ScriptManager scriptManager;
    protected EncounterSpawner encounterSpawner;
    protected EncounterAdjuster encounterAdjuster;
    protected DC_Game game;

    public MissionMaster(DC_Game game) {
        this.game = game;
        this.optionManager = createOptionManager();
        this.statManager = createStatManager();
        game.getManager().setStatMaster(statManager);
        this.constructor = createConstructor();
        this.playerManager = createPlayerManager();
        scriptManager= createScriptManager();

        encounterSpawner = new EncounterSpawner(this);
        encounterAdjuster = new EncounterAdjuster(this);
    }

    public Floor getFloor() {
        return getMission().getFloor();
    }

    protected abstract ScriptManager createScriptManager();

    public void init() {
        this.mission = createMission();
        playerManager.initializePlayers();
        getConstructor().init();
//        optionManager.initialize();
    }

    public void startGame() {
    }

    protected abstract E createMission();

    protected PlayerManager<E> createPlayerManager() {
        return new PlayerManager<>(this);
    }

    protected abstract MissionConstructor<E> createConstructor();

    protected abstract MissionStatManager<E> createStatManager();

    protected abstract MissionOptionManager<E> createOptionManager(); //<E>

    public MissionOptionManager getOptionManager() {
        return optionManager;
    }

    public MissionStatManager getStatManager() {
        return statManager;
    }

    public MissionConstructor getConstructor() {
        return constructor;
    }


    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public E getMission() {
        return mission;
    }

    public DungeonMaster getDungeonMaster() {
        return game.getDungeonMaster();
    }

    public MetaGameMaster getMetaMaster() {
        return game.getMetaMaster();
    }

    public Positioner getPositioner() {
        return getDungeonMaster().getPositioner();
    }

    public Spawner getSpawner() {
        return getDungeonMaster().getSpawner();
    }


    public DC_Game getGame() {
        return game;
    }

    public EncounterSpawner getEncounterSpawner() {
        return encounterSpawner;
    }

    public EncounterAdjuster getEncounterAdjuster() {
        return encounterAdjuster;
    }

    public ScriptManager getScriptManager() {
        return scriptManager;
    }
}
