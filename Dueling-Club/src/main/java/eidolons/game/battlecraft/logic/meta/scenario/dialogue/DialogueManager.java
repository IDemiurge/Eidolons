package eidolons.game.battlecraft.logic.meta.scenario.dialogue;

import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

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
        for (String substring : StringMaster.open(data)) {
//            GameDialogue dialogue=  getDialogueFactory().getDialogue(substring, (ScenarioMetaMaster) master);
//            startDialogue(dialogue);
        }
    }

    public void startDialogue(GameDialogue dialogue) {
        //if in game

        getGame().getGameLoop().setPaused(true);
        try {
            new DialogueWizard(dialogue).start();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_LOOP_PAUSE_DONE, true);
        }
    }

}
