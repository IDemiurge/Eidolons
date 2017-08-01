package main.game.battlecraft.ai.advanced.machine.train;

import main.game.battlecraft.ai.elements.generic.AiMaster;

/**
 * Created by JustMe on 7/31/2017.
 */
public class AiTrainer {

    AiMaster master;

    public AiTrainer(AiMaster master) {
        this.master = master;
    }

    public AiTrainingResult train(float[] profile,
                                  AiTrainingCriteria criteria,
                                  AiTrainingParameters parameters) {
        setProfile(profile);
        initTrainingParameters(parameters);
        runAiScenario();
        return evaluateResult(criteria);
    }

    private AiTrainingResult evaluateResult(AiTrainingCriteria criteria) {
        return null;
    }

    private void runAiScenario() {
    }

    private void initTrainingParameters(AiTrainingParameters parameters) {
    }

    private void setProfile(float[] profile) {
        master.getPriorityProfileManager().setPriorityProfile(profile);
    }
}
