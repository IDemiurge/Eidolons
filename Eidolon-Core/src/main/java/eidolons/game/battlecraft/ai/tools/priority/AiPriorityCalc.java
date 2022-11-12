package eidolons.game.battlecraft.ai.tools.priority;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;

import java.util.LinkedHashMap;
import java.util.Map;

public class AiPriorityCalc {
    ActionSequence sequence;
    UnitAI ai;
    AiCalcData data;

    Map<String, Float> multipliers = new LinkedHashMap<>();
    Map<String, Float> bonuses = new LinkedHashMap<>();

    public AiPriorityCalc(ActionSequence sequence, UnitAI ai, AiCalcData data) {
        this.sequence = sequence;
        this.ai = ai;
        this.data = data;
    }

    public Map<String, Float> getMultipliers() {
        return multipliers;
    }

    public Map<String, Float> getBonuses() {
        return bonuses;
    }

    public AiPriorityCalc     addBonus(String id, Float f){
        bonuses.put(id, f);
        return this;
    }
    public AiPriorityCalc     addMultiplier(String id, Float f){
        multipliers.put(id, f);
        return this;
    }
}
