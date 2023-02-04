package logic.v2.ai.generic.action;

import logic.v2.entity.UnitRef;

/**
 * Created by Alexander on 1/22/2023
 */
public class AiTargetingFixed implements IAiTargeting {
    private UnitRef fixed;

    public AiTargetingFixed(UnitRef fixed) {
        this.fixed = fixed;
    }

    @Override
    public UnitRef getTarget() {
        return fixed;
    }
}
