package eidolons.game.battlecraft.logic.battle.universal;

import eidolons.game.battlecraft.logic.battle.encounter.EncounterAdjuster;
import eidolons.game.battlecraft.logic.battle.encounter.EncounterSpawner;
import eidolons.game.battlecraft.logic.battle.universal.stats.BattleStatManager;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/7/2017.
 */
public abstract class BattleMaster<E extends Battle> {

    protected E battle;
    protected BattleOptionManager optionManager;
    protected BattleStatManager statManager;
    protected BattleConstructor constructor;
    protected BattleOutcomeManager outcomeManager;
    protected PlayerManager playerManager;
    protected ScriptManager scriptManager;
    protected EncounterSpawner encounterSpawner;
    protected EncounterAdjuster encounterAdjuster;
    protected DC_Game game;

    public BattleMaster(DC_Game game) {
        this.game = game;
        this.battle = createBattle();
        this.optionManager = createOptionManager();
        this.statManager = createStatManager();
        game.getManager().setStatMaster(statManager);
        this.constructor = createConstructor();
        this.outcomeManager = createOutcomeManager();
        this.playerManager = createPlayerManager();
        scriptManager= createScriptManager();

        encounterSpawner = new EncounterSpawner(this);
        encounterAdjuster = new EncounterAdjuster(this);
    }

    protected abstract ScriptManager createScriptManager();

    public void init() {
        playerManager.initializePlayers();
        getConstructor().init();
//        optionManager.initialize();
    }

    public void startGame() {
    }

    public void combatStarted() {
    //diff between petty engagements and encounter-battle 
    
        //maybe it's from the aggro master - apart from individual aggro, there might be more...
        /*
        if leader gets aggro - it's Encounter
        
        encounter aggro params! 
        linked to their behavior perhaps 
        
        
        
        
         */
    }

    protected abstract E createBattle();

    protected PlayerManager<E> createPlayerManager() {
        return new PlayerManager<E>(this);
    }

    protected abstract BattleOutcomeManager<E> createOutcomeManager();

    protected abstract BattleConstructor<E> createConstructor();

    protected abstract BattleStatManager<E> createStatManager();

    protected abstract BattleOptionManager<E> createOptionManager(); //<E>

    public BattleOptionManager getOptionManager() {
        return optionManager;
    }

    public BattleStatManager getStatManager() {
        return statManager;
    }

    public BattleConstructor getConstructor() {
        return constructor;
    }

    public BattleOutcomeManager getOutcomeManager() {
        return outcomeManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public E getBattle() {
        return battle;
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

    public Dungeon getDungeon() {
        return getDungeonMaster().getDungeonWrapper().getDungeon();
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
