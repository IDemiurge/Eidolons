package logic.calculation.damage;

import elements.EntityRef;
import elements.content.stats.UnitParam;
import framework.entity.Entity;
import org.apache.commons.lang3.tuple.Pair;

import static elements.content.enums.types.CombatTypes.*;

/**
 * Created by Alexander on 8/21/2023
 */
public class DamageDealer {
    public static boolean deal(DamageCalcResult result) {
        EntityRef ref = result.getRef();
        // ref.get("target").getInt("hp").var
        for (Pair<DamageType, Integer> pair : result.getDamageDealt()) {
            if (!dealDamage(ref, pair.getLeft().isHp(), pair.getRight())) {
                // interrupted = true;
                break;
            }
        }

        return !ref.get("target").isDead();
    }

    private static boolean dealDamage(EntityRef ref, boolean hp, Integer damage) {
        Entity target = ref.get("target");
        UnitParam
                value = hp ? UnitParam.Hp : target.getBoolean(UnitProp.Pure) ? UnitParam.Faith : UnitParam.Sanity,
                soulBlocker = target.getBoolean(UnitProp.Pure) ? UnitParam.Faith : UnitParam.Sanity;

        boolean threshold_condition = target.getInt(value) <= 0; //death's door, Madness/Nigredo

        if (!threshold_condition || hp) {

            int buffer = target.getInt(value = hp ? UnitParam.Armor : soulBlocker);
            if (buffer > 0) {
                int remainder = damage - buffer;
                if (remainder < 0) {
                    /* Absorbed by Armor/Faith/Sanity */
                    // target.addIntValue(value, -damage);
                    return true;
                }
                target.setValue(value, 0);
                target.setValue(value + "_broken", true);
            }
        }
        value = hp ? UnitParam.Hp : UnitParam.Soul;
        Integer current = target.getInt(value);

        boolean lethal = false;//checkLethal(ref);
        boolean canKill = threshold_condition || lethal;


        // target.addIntValue(value, -damage);
        int excessDamage = damage - current;
        // if (excessDamage> threshold) kill()  => Can a single blow kill? With LETHAL perk
        // WoundRule woundRule = getWoundRule(hp, ref);
        // woundRule.apply(excessDamage, ref); //will also set to Death's Door or Madness/Nigredo

         return true;
}


}
