package main.game.battlecraft.ai.advanced.machine.train;

import main.entity.type.ObjType;
import main.game.battlecraft.ai.advanced.machine.PriorityProfile;
import main.game.core.game.DC_Game;
import main.game.core.game.GameFactory.GAME_SUBCLASS;
import main.game.core.launch.GameLauncher;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 7/31/2017.
 */
public class AiTrainer {


    public AiTrainingResult train(
     //type at coordinates? or for all of same type?
     PriorityProfile profile,
     AiTrainingCriteria criteria,
     AiTrainingParameters parameters) {
        ObjType trainee = parameters.getTraineeType();

        initTrainingParameters(parameters);
        runAiScenario(parameters.getPresetPath());
        DC_Game.game.getAiManager().getPriorityProfileManager().setPriorityProfile(trainee, profile);
        boolean result = (boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.GAME_FINISHED);
        return evaluateResult(profile, parameters, criteria);
    }

    private void initTrainingParameters(AiTrainingParameters parameters) {
//        loadGame(parameters.get)
        parameters.getRoundsMax();
        parameters.getTraineeType();
    }

    private void runAiScenario(String presetPath) {
        if (AiTrainingRunner.evolutionTestMode)
            return;
        new GameLauncher(GAME_SUBCLASS.TEST).launchPreset(presetPath);

    }

    private AiTrainingResult evaluateResult(PriorityProfile profile, AiTrainingParameters parameters, AiTrainingCriteria criteria) {
        AiTrainingResult result = new AiTrainingResult(profile, parameters, criteria);
        try {
            result.construct();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
