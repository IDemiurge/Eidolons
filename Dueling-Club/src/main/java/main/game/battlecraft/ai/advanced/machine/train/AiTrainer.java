package main.game.battlecraft.ai.advanced.machine.train;

import main.entity.type.ObjType;
import main.game.battlecraft.ai.advanced.machine.PriorityProfile;
import main.game.battlecraft.ai.elements.generic.AiMaster;

/**
 * Created by JustMe on 7/31/2017.
 */
public class AiTrainer {

    AiMaster master;

    public AiTrainer(AiMaster master) {
        this.master = master;
    }

    public AiTrainingResult train(
     //type at coordinates? or for all of same type?
     PriorityProfile profile,
     AiTrainingCriteria criteria,
     AiTrainingParameters parameters) {
        ObjType trainee = parameters.getTraineeType();
        master.getPriorityProfileManager().setPriorityProfile(trainee, profile);
        initTrainingParameters(parameters);
        runAiScenario();
        return evaluateResult(criteria);
    }

    private void initTrainingParameters(AiTrainingParameters parameters) {
//        loadGame(parameters.get)
    }

    private void runAiScenario() {

    }

    private AiTrainingResult evaluateResult(AiTrainingCriteria criteria) {

        return null;
    }

}
