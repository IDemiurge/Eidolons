package elements.exec.effect;

import elements.exec.EntityRef;
import logic.calculation.damage.DamageCalc;
import logic.calculation.damage.DamageCalcResult;
import logic.calculation.damage.DamageDealer;

/**
 * Created by Alexander on 8/21/2023
 */
public class DamageEffect extends Effect{

    @Override
    public String[] getArgNames() {
        return new String[0];
    }

    public boolean apply(EntityRef ref){
        int amount = 0; //formula.getInt(ref);
        ref.setValueInt(amount);
        DamageCalcResult result = new DamageCalc(ref).calculate(false);
       boolean dead = DamageDealer.deal(result);
        if (dead){
            return false; //interrupts further effect chains
        }
        return true;
    }
}
