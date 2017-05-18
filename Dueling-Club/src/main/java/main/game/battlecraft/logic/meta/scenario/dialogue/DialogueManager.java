package main.game.battlecraft.logic.meta.scenario.dialogue;

import main.content.PROPS;
import main.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import main.game.battlecraft.logic.meta.universal.MetaGameHandler;
import main.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/14/2017.
 */
public class DialogueManager extends MetaGameHandler<ScenarioMeta> {
    public DialogueManager(MetaGameMaster master) {
        super(master);
    }

    public void startScenarioIntroDialogues() {
        String data = getMetaGame().getScenario().getProperty(PROPS.
         SCENARIO_INTRO_DIALOGUES);
        startDialogues(data);
    }

    public void startMissionIntroDialogues() {
//        Mission mission = getMetaGame().getScenario().getMission();
//        String data = mission.getProperty(PROPS.MISSION_INTRO_DIALOGUES);
//        startDialogues(data);

    }

    public void startDialogues(String data) {
        for (String substring : StringMaster.openContainer(data)) {
            GameDialogue dialogue=  DialogueFactory.getDialogue(substring, (ScenarioMetaMaster) master);
            startDialogue(dialogue);
        }
    }

    public void startDialogue(GameDialogue dialogue) {
        //if in game

//     getGame().getGameLoop().freeze();
//        try {
//            new DialogueWizard(dialogue).start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        finally {
//            getGame().getGameLoop().unfreeze();
//        }
    }

}
