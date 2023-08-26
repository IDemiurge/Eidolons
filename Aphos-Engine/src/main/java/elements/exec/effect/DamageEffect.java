package elements.exec.effect;

import elements.content.enums.types.CombatTypes;
import elements.exec.EntityRef;
import elements.exec.effect.framework.EffectResult;
import logic.calculation.damage.DamageCalc;
import logic.calculation.damage.DamageCalcResult;
import logic.calculation.damage.DamageDealer;

/**
 * Created by Alexander on 8/21/2023
 * what are the cases when we wanna deal damage w/o attack check?
 * > Periodic?
 * >
 *
 * should this ADD damage instead?
 * Otherwise, this matreshka is for naught!
 * Maybe damage should not have a map in fact...
 *
 */
public class DamageEffect extends Effect{

    private final CombatTypes.DamageType type;
    private final Object formula;

    public DamageEffect(CombatTypes.DamageType type, Object formula) {
        this.type = type;
        this.formula = formula;
    }

    @Override
    public String[] getArgNames() {
        return new String[0];
    }

    protected void applyThis(EntityRef ref){
        //TODO
        int amount = system.math.Formula.getInt(formula); //formula.getInt(ref);
        ref.setValueInt(amount);
        ref.setDamageType(type);
        DamageCalcResult result = new DamageCalc(ref).calculate(false);
       DamageDealer.deal(result);
        // boolean dead =!ref.get("target").isDead()
        // result.add("killed", true)
    }
}
