package elements.exec.effect.attack;

import elements.content.enums.types.CombatTypes;
import elements.exec.EntityRef;
import logic.calculation.damage.*;

/**
 * Created by Alexander on 8/25/2023
 */
public class DamageAttackEffect extends AttackEffect {
    @Override
    public void process(CombatTypes.RollGrade grade, EntityRef ref) {

        ref.setDamageType(data.getEnum( "damage_type", CombatTypes.DamageType.class));
        DamageCalc damageCalc = new DamageCalc(ref);
        damageCalc.setGrade(grade);
        DamageCalcResult result = damageCalc.calculate(false);
        if (result.isMiss()){
            // handleMiss();
        }
        // effectResult.addAll(result.getData());
        DamageResult finalResult = DamageDealer.deal(result);

        // effectResult.addAll(finalResult.getData());
    }

    @Override
    public void applyThis(EntityRef ref) {
        //different grade vs each target!
        super.applyThis(ref);
    }

    @Override
    public String[] getArgNames() {
        return new String[]{"damage_type"};
    }
}
