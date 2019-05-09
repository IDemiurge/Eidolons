package eidolons.ability.effects.containers.customtarget;

import eidolons.ability.ActivesConstructor;
import eidolons.entity.active.DC_ActiveObj;
import main.ability.effects.Effect;
import main.ability.effects.container.SpecialTargetingEffect;
import main.content.C_OBJ_TYPE;
import main.data.ability.AE_ConstrArgs;
import main.elements.conditions.Conditions;
import main.elements.conditions.NotCondition;
import main.elements.conditions.NumericCondition;
import main.elements.targeting.AutoTargeting;
import main.entity.Ref.KEYS;
import main.system.entity.ConditionMaster;
import main.system.math.Formula;

public class ZoneEffect extends SpecialTargetingEffect
// getEffectsStage() setEffect() etc
{
    private Formula radius;

    // private Effect effects;
    // private Boolean allyOrEnemyOnly;
    // private boolean notSelf = false;
    // private Conditions filteringConditions; // TODO !
    // private AutoTargeting targeting;

    @AE_ConstrArgs(argNames = {"effects", "radius", "allyOrEnemyOnly", "notSelf",
     "reductionFormula"})
    public ZoneEffect(Effect effects, Formula radius, Boolean allyOrEnemyOnly, Boolean notSelf,
                      String reductionFormula) {
        this.effects = effects;
        this.radius = radius;
        this.allyOrEnemyOnly = allyOrEnemyOnly;
        this.notSelf = notSelf;
        if (reductionFormula != null) {
            this.reductionFormula = new Formula(reductionFormula);
        }
        effects.setReconstruct(true);
    }

    @AE_ConstrArgs(argNames = {"effects", "radius", "allyOrEnemyOnly", "notSelf"})
    public ZoneEffect(Effect effects, Formula radius, Boolean allyOrEnemyOnly, Boolean notSelf) {
        this(effects, radius, allyOrEnemyOnly, notSelf, null);
    }

    public ZoneEffect(Effect effects, Formula radius) {
        this(effects, radius, null, true);
    }

    public void initTargeting() {
        Conditions conditions = new Conditions();
        int spell_radius = radius.getInt(ref);
//        if (spell_radius == 0) {
//            spell_radius = ref.getObj(KEYS.ACTIVE.name()).getIntParam(G_PARAMS.RADIUS);
//        }
        NumericCondition condition = ConditionMaster.getDistanceFilterCondition(KEYS.TARGET.name(), spell_radius);
        conditions
         .add(condition);
        if (spell_radius == 0)   //just on same cell
            condition.setStrict(false);
        conditions.add(ConditionMaster.getNotDeadCondition()); // TODO really???

        if (notSelf) {
            conditions.add(new NotCondition(ConditionMaster.getSelfFilterCondition()));
        }
        if (allyOrEnemyOnly != null) // TODO target filtering - targeting
        // modifiers?
        {
            conditions.add(allyOrEnemyOnly ? ConditionMaster.getAllyCondition() : ConditionMaster
             .getEnemyCondition());
        }
        // legacy?
        // if (allyOrEnemyOnly==null)
        // conditions.add(ConditionMaster.getEnemyCondition());
        // else if (!allyOrEnemyOnly) {
        // conditions.add(allyOrEnemyOnly? ConditionMaster.getAllyCondition() :
        // ConditionMaster.getEnemyCondition());
        // }
//        if (effects.getSpell() != null)
//            if (effects.getSpell().checkBool(STD_BOOLS.APPLY_THRU))
        this.targeting = new AutoTargeting(conditions, C_OBJ_TYPE.BF);

        if (targeting == null) {
            this.targeting = new AutoTargeting(conditions, C_OBJ_TYPE.BF_OBJ);
        }
        if (ref.getActive() instanceof DC_ActiveObj) {
            ActivesConstructor.addTargetingMods(targeting, (DC_ActiveObj) ref.getActive());
        }
        conditions = targeting.getFilter().getConditions();
        setFilteringConditions(conditions);
    }

    // @Override
    // public boolean applyThis() {
    //
    // initTargeting();
    // this.targeting.select(ref);
    // return effects.apply(ref);
    // }

    @Override
    public Effect getEffect() {
        return effects;
    }

}
