package logic.calculation.damage;

import elements.exec.EntityRef;
import elements.stats.ActionParam;
import elements.stats.UnitParam;
import framework.entity.field.FieldEntity;
import framework.entity.field.Unit;
import framework.entity.sub.UnitAction;
import main.system.auxiliary.RandomWizard;

import java.util.LinkedHashMap;
import java.util.Map;

import static elements.content.enums.types.CombatTypes.*;

/**
 * Created by Alexander on 8/20/2023
 * <p>
 * For entire action? Aye... >>> What about cases where action is NULL?
 * <p>
 * calculate HP/ARmor/.. to be deducted Further trigger-reductions may apply on deal() step
 * <p>
 * Should work for soul too!
 */
public class DamageCalc {
    private EntityRef ref;
    private boolean precalc;
    private DamageCalcResult result;
    private RollGrade grade;
    // CalcResult  store result and calc process here

    public DamageCalc(EntityRef ref) {
        this.ref = ref;
    }
    // for precalc?
    @Deprecated
    public DamageCalcResult calculate(boolean precalc){
        this.precalc = precalc; //what side effects could there be?
        result = new DamageCalcResult(ref);

        MultiDamage damage = getDamage(grade);
        result.setDamageToDeal(damage);

        return result;
    }
    /*
    what happens when we deal dmg of 2 types in a single attack? each instance is blocked separately?
    but for an action...
     */
    public int getHpLost(Damage damage) {
        int armor = ref.get("attacked").getInt(UnitParam.Armor);
        int dr = ref.get("attacked").getInt(UnitParam.Damage_Reduction);
        BlockType blockType = ref.get("attack").getEnum("block type", BlockType.class);
        UnitParam blockParam = blockType.getParam();
        int block = blockParam == null ? 0 : ref.get("attacked").getInt(blockParam);
        int hpLost = 0;
        for (DamageType damageType : damage.getDamageMap().keySet()) {
            if (!damageType.isHp()) {
                continue;
            }
            int amount;
        }
        return hpLost;
    }

    public Damage getDamage(RollGrade grade) {
        UnitAction attack = (UnitAction) ref.get("action");

        Unit attacker = (Unit) ref.get("attacker");
        FieldEntity attacked = (FieldEntity) ref.get("attacked");
        if (grade == RollGrade.Miss) {
            // Events.fire(x)
            return new Damage();
        }
        int baseAmount = getBaseAmount(grade, attack);
        float mod = getUltimateModifier(attack, attacker, attacked);
        //random -


        Map<DamageType, Integer> map = new LinkedHashMap<>();
        return new Damage(map);
    }

    private int getBaseAmount(RollGrade grade, UnitAction attack) {
        ActionParam
                minValue = ActionParam.Min_Value,
                value = ActionParam.Base_Value,
                maxValue = ActionParam.Max_Value;
        switch (grade) {
            case Ultimate:
            case Max:
                return attack.getInt(maxValue);
            case Rnd_Avrg_Max:
                return random(attack.getInt(value), attack.getInt(maxValue));
            case Avrg:
                return attack.getInt(value);
            case Rnd_Min_Avrg:
                return random(attack.getInt(minValue), attack.getInt(value));
            case Min:
                return attack.getInt(minValue);
        }
        return 0;
    }

    private int random(int anInt, int anInt1) {
        return RandomWizard.getRandomIntBetween(anInt, anInt1);
    }


    private float getUltimateModifier(UnitAction attack, Unit attacker, FieldEntity attacked) {
        return 2;
    }

    public void setGrade(RollGrade grade) {
        this.grade = grade;
    }

    public RollGrade getGrade() {
        return grade;
    }
}
