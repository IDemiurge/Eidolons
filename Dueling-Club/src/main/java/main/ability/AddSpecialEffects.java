package main.ability;

import main.ability.effects.Effect;
import main.ability.effects.containers.AbilityEffect;
import main.ability.effects.oneshot.MicroEffect;
import main.entity.obj.DC_Obj;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.LogMaster;

public class AddSpecialEffects extends MicroEffect {
    private Effect effects;
    private SPECIAL_EFFECTS_CASE case_type;
    private String caseName;
    private String abilName;

    public AddSpecialEffects(SPECIAL_EFFECTS_CASE case_type, Effect effects) {
        this.effects = effects;
        this.case_type = case_type;
        main.system.auxiliary.LogMaster.log(LogMaster.CORE_DEBUG, "add-special effect");

    }

    public AddSpecialEffects(String case_type, String abilName) {
        this.caseName = case_type;
        this.abilName = abilName;
    }

    @Override
    public boolean applyThis() {
        if (case_type == null) {
            case_type = new EnumMaster<SPECIAL_EFFECTS_CASE>().retrieveEnumConst(
                    SPECIAL_EFFECTS_CASE.class, caseName);
        }
        if (effects == null) {
            effects = new AbilityEffect(abilName).getEffects();
        }
        if (!(ref.getTargetObj() instanceof DC_Obj))
            return false;
        DC_Obj targetObj = (DC_Obj) ref.getTargetObj();

        targetObj.addSpecialEffect(case_type, effects);

        return true;
    }

    public Effect getEffects() {
        return effects;
    }

    public SPECIAL_EFFECTS_CASE getCase_type() {
        return case_type;
    }

    public String getCaseName() {
        return caseName;
    }

    public String getAbilName() {
        return abilName;
    }

}
