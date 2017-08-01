package main.game.battlecraft.ai.advanced.machine.train;

import main.game.battlecraft.rules.RuleMaster.RULE_SCOPE;

/**
 * Created by JustMe on 7/31/2017.
 * Should define the whole preset that is being used for training
 * Used for initializing the game
 *
 *
 */
public class AiTrainingParameters {

Integer roundsMax;
    String presetPath; //TODO OR SAVE FILE!
    RULE_SCOPE ruleScope;
    boolean deterministic;

    public AiTrainingParameters(String[] arg) {


    }


    public enum STANDARD_TRAINING_PARAMETERS{
        DEMO_BATTLE(""),
        ;
        String presetPath;

        STANDARD_TRAINING_PARAMETERS(String presetPath) {
            this.presetPath = presetPath;
        }
    }
}
