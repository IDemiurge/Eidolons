package main.game.battlecraft.logic.meta.scenario.dialogue;

import main.content.PROPS;
import main.game.battlecraft.logic.battle.mission.Mission;
import main.game.battlecraft.logic.meta.MetaGameHandler;
import main.game.battlecraft.logic.meta.MetaGameMaster;
import main.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/14/2017.
 */
public class DialogueManager extends MetaGameHandler<ScenarioMeta> {
    public DialogueManager(MetaGameMaster master) {
        super(master);
    }


    public void startDialogue() {
        Mission mission = null;
        String data = null;
        if (mission==null )
            data =
        getMetaGame().getScenario().getProperty(PROPS.SCENARIO_DIALOGUE_DATA);
//   entity!     Mission

        for(String substring: StringMaster.openContainer( data )){
            startDialogue(substring);

        }
    }

    public void startDialogue(String data) {
//        DataManager.getType(data, MACRO_OBJ_TYPES.DIALOGUE);
        //freeze game if necessary - call gameLoop
        //change stage
        //run to end or
//        nextScene();



    }
}
