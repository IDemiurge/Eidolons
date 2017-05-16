package main.game.battlecraft.logic.meta.scenario;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.game.battlecraft.logic.dungeon.DungeonData.DUNGEON_VALUE;
import main.game.battlecraft.logic.meta.*;
import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import main.game.core.game.ScenarioGame;

/**
 * Created by JustMe on 5/13/2017.
 */
public class ScenarioMetaMaster extends MetaGameMaster<ScenarioMeta > {

    DialogueManager dialogueManager;
    public ScenarioMetaMaster(String data) {
        super(data);
        dialogueManager = new DialogueManager(this);
    }
    /*
        on clicking a mission...
        full loading 
        > create dungeon(s)
        > create party units (maybe  place them at once)
         */

    @Override
    public void gameStarted() {
//        scenario.getScenario().
       String missionName= getPartyManager().getParty().getNextMission();

        String levelPath = DataManager.getType(missionName, DC_TYPE.MISSIONS).
         getProperty(PROPS.MISSION_FILE_PATH);
        getGame().getDataKeeper().getDungeonData().setValue(DUNGEON_VALUE.PATH,
         levelPath);

        super.gameStarted();
    }

    public   void loadMission(String data){
         getGame().getDungeonMaster().init(); //create dungeon

        getGame().getBattleMaster().init(); //create Mission
        //game.load()
        //precombat?
        getGame().start(true); //game loop??
        getMetaGame().getScenario().next();
//        getPartyManager().getParty().getIntParam(mission_index);
//        Mission mission= (Mission) getGame().getBattleMaster().getBattle();
//        getMetaGame().getScenario().setMission(mission);
        //for Mission
        getDialogueManager().startDialogue(); //block game?

    }
    @Override
    protected ScenarioGame createGame() {
        return new ScenarioGame(this);
    }

    @Override
    protected PartyManager<ScenarioMeta> createPartyManager() {
        return new ScenarioPartyManager(this);
    }

    @Override
    protected MetaDataManager<ScenarioMeta> createMetaDataManager() {
        return new ScenarioMetaDataManager(this);
    }

    @Override
    protected ShopManager<ScenarioMeta> createShopManager() {
//        return new Scenariosh;
        return null;
    }

    @Override
    protected MetaInitializer<ScenarioMeta> createMetaInitializer() {
        return new ScenarioInitializer(this);
    }
    public DialogueManager getDialogueManager() {
        return dialogueManager;
    }
}
