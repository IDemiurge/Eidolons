package eidolons.ability.effects.special.meta;

import eidolons.ability.effects.DC_Effect;
import eidolons.game.battlecraft.logic.meta.scenario.ObjectiveMaster.OBJECTIVE_TYPE;

public class ObjectiveEffect extends DC_Effect {
    String objectiveData;
    private OBJECTIVE_TYPE type;

    public ObjectiveEffect(OBJECTIVE_TYPE type, String objectiveData) {
        this.type = type;
        this.objectiveData = objectiveData;
    }

    public boolean applyThis() {
        //ObjectiveMaster.initObjectiveTrigger(getGame(), type, objectiveData, null);
        return true;
    }

}
