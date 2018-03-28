package main.ability.effects.oneshot.unit;

import main.ability.InventoryTransactionManager;
import main.ability.effects.DC_Effect;
import main.ability.effects.OneshotEffect;
import main.content.PARAMS;
import main.content.enums.entity.UnitEnums;
import main.entity.obj.unit.Unit;
import main.system.math.Formula;

public class ResurrectEffect extends DC_Effect implements OneshotEffect {
    /*
     * re-equip items
     */
    private Formula endFormula;
    private Formula essFormula;
    private Formula focFormula;
    private Formula moraleFormula;
    private Formula actFormula;

    public ResurrectEffect() {

    }

    public boolean applyThis() {
        Unit target = (Unit) ref.getTargetObj();
        InventoryTransactionManager.equipOriginalItems(target, target);
        target.removeStatus(UnitEnums.STATUS.DEAD);

//        getGame().getManager(). getObjCreator().createUnit(target.getType())

        target.setParam(PARAMS.C_ENDURANCE, endFormula.getInt(ref));
        // perhaps modify instead, in case the lethal damage was exceeding
        target.setParam(PARAMS.C_TOUGHNESS, endFormula.getInt(ref));
        target.setParam(PARAMS.C_ESSENCE, essFormula.getInt(ref));
        target.setParam(PARAMS.C_FOCUS, focFormula.getInt(ref));
        target.setParam(PARAMS.C_MORALE, moraleFormula.getInt(ref));
        target.setParam(PARAMS.C_N_OF_ACTIONS, actFormula.getInt(ref));

        return false;
    }

}
