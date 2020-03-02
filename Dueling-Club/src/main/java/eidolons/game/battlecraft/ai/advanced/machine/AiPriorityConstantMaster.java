package eidolons.game.battlecraft.ai.advanced.machine;

import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import main.content.values.parameters.PARAMETER;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 7/31/2017.
 */
public class AiPriorityConstantMaster extends AiHandler {
    private static final String PARAM_PREFIX = "PARAM_";
    private static final String PARAM_MOD_PREFIX = "PARAM_MOD_";
    AiConst[] techConsts = {

    };
    private HashMap<String, AiConst> aiConstMap;
    Map<AiConstant, Float> map;

    public AiPriorityConstantMaster(AiMaster master) {
        super(master);
        initMap();
    }

    public float getParamPriority(PARAMETER p) {
        return getConstValue(getConstForParam(p));
    }

    public float getParamModPriority(PARAMETER p) {
        return getConstValue(getConst(PARAM_MOD_PREFIX + p.name()));
    }

    public int getConstInt(AiConst aiConst) {
        return (int) getConstValue(aiConst);
    }

    public float getConstValue(AiConst aiConst) {
        return map.get(aiConst);
    }

    public AiConst getConstForParam(PARAMETER param) {
        return getConst(PARAM_PREFIX + param.name());
    }

    public AiConst getConst(String s) {
        //TODO improve!
        return aiConstMap.get(s.toUpperCase());
//        return AiConst.valueOf(s.toUpperCase());
    }

    public void initMap() {
        aiConstMap = new HashMap<>();
        for (AiConst sub : AiConst.values()) {
            aiConstMap.put(sub.name(), sub);
            map.put(sub , sub.getDefValue());
        }
    }

    public interface AiConstant {

    }

}
