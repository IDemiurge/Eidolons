package main.game.battlecraft.ai.advanced.machine;

import main.content.values.parameters.PARAMETER;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.generic.AiMaster;

import java.util.Map;

/**
 * Created by JustMe on 7/31/2017.
 */
public class AiPriorityConstantMaster extends AiHandler {
AiConst[] techConsts={

};
    private static final String PARAM_PREFIX = "PARAM_";

    public AiPriorityConstantMaster(AiMaster master) {
        super(master);
    }


    public float getParamPriority(PARAMETER p) {
        AiConst aiConst = getConst(PARAM_PREFIX + p.name());
        return getConstValue(aiConst);
    }

    public int getConstInt(AiConst aiConst) {
        return (int) getConstValue(aiConst);
    }

    public float getConstValue(AiConst aiConst) {
        return getProfile().getMap().get(aiConst);
    }

    public AiConst getConst(String s) {
        //TODO improve!
        return AiConst.valueOf(s);
    }



    public float[] convertParameters(Map<AiConst, Float> map) {

        return new float[0];
    }
}
