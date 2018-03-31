package eidolons.game.battlecraft.ai.advanced.machine.train;

import eidolons.game.battlecraft.ai.advanced.machine.train.AiTrainingParameters.TRAINING_CRITERIA_MODS;

/**
 * Created by JustMe on 8/9/2017.
 */
public class CriteriaMod {
    TRAINING_CRITERIA_MODS mod;
    float factor;

    public CriteriaMod(TRAINING_CRITERIA_MODS mod, float factor) {
        this.mod = mod;
        this.factor = factor;
    }

    public TRAINING_CRITERIA_MODS getMod() {
        return mod;
    }

    public float getFactor() {
        return factor;
    }
}
