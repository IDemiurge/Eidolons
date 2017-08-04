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
    private static final String PARAM_MOD_PREFIX = "PARAM_MOD_";

    public AiPriorityConstantMaster(AiMaster master) {
        super(master);
    }


    public float getParamPriority(PARAMETER p) {
        return getConstValue(getConstForParam(p));
    }
    public float getParamModPriority(PARAMETER p) {
        return getConstValue(getConst(PARAM_MOD_PREFIX + p .name()));
    }

    public int getConstInt(AiConst aiConst) {
        return (int) getConstValue(aiConst);
    }

    public float getConstValue(AiConst aiConst) {
        return getProfile().getMap().get(aiConst);
    }

    public AiConst getConstForParam(PARAMETER param) {
        return getConst(PARAM_PREFIX + param.name());
    }
    public AiConst getConst(String s) {
        //TODO improve!
        return AiConst.valueOf(s.toUpperCase());
    }



    public float[] convertParameters(Map<AiConst, Float> map) {

        return new float[0];
    }

}
