package logic.calculation.damage;

import elements.exec.EntityRef;
import elements.stats.UnitParam;
import elements.stats.UnitProp;
import framework.entity.Entity;
import org.apache.commons.lang3.tuple.Pair;

import static elements.content.enums.types.CombatTypes.*;

/**
 * Created by Alexander on 8/21/2023
 * <p>
 * How to produce the right kind of Events that we could process visually in a transparent and effective way? E.g. how
 * much damage was absorbed etc
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
                value = hp ? UnitParam.Hp : target.isTrue(UnitProp.Pure) ? UnitParam.Faith : UnitParam.Sanity,
                soulBlocker = target.isTrue(UnitProp.Pure) ? UnitParam.Faith : UnitParam.Sanity;

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
                if (!hp){
                    //TODO
                    // int excessDamage = damage - current;
                    // Wounds.apply(excessDamage, value, ref);
                }

                damage = remainder;
            }
        }


        value = hp ? UnitParam.Hp : UnitParam.Soul;
        Integer current = target.getInt(value);

        boolean lethal = false;//checkLethal(ref);
        boolean canKill = threshold_condition || lethal;

        if (damage > current) {
            if (canKill) {
                target.setValue(value, Integer.MIN_VALUE);
                return false;
            }
        }

        target.addCurValue(value, -damage); //reduce hp or soul

        int excessDamage = damage - current;
        // if (excessDamage> threshold) kill()  => Can a single blow kill? With LETHAL perk
        logic.rules.combat.wounds.Wounds.apply(excessDamage, value, ref);

        return true;
    }


}
