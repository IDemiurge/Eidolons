package logic.calculation.damage;

import elements.exec.EntityRef;
import elements.stats.UnitParam;
import elements.stats.UnitProp;
import framework.entity.field.FieldEntity;
import logic.rules.combat.wounds.Wounds;

import static combat.sub.BattleManager.combat;
import static elements.content.enums.types.CombatTypes.*;
import static system.log.SysLog.printOut;

/**
 * Created by Alexander on 8/21/2023
 * <p>
 * How to produce the right kind of Events that we could process visually in a transparent and effective way? E.g. how
 * much damage was absorbed etc
 */
public class DamageDealer {

    public static DamageResult deal(DamageCalcResult calc) {
        DamageResult result = new DamageResult();
        EntityRef ref = calc.getRef();

        MultiDamage multiDamage = calc.getDamageToDeal();
        for (Damage damage : multiDamage.getDamageList()) {
            if (!dealDamage(ref, damage.getType().isHp(), damage.getAmount(), result)) {
                // interrupted = true; in what case?
                break;
            }
        }

        return result;
    }

    private static boolean dealDamage(EntityRef ref, boolean hp, Integer damage, DamageResult result) {
        FieldEntity target = (FieldEntity) ref.get("target");
        return dealDamage(target, ref, hp, damage, result);
    }

    private static boolean dealDamage(FieldEntity target, EntityRef ref, boolean hp, Integer damage, DamageResult result) {
        UnitParam reducedValue = hp ? UnitParam.Health : target.isTrue(UnitProp.Pure) ? UnitParam.Faith : UnitParam.Sanity;
        UnitParam soulBlocker = target.isTrue(UnitProp.Pure) ? UnitParam.Faith : UnitParam.Sanity;

        boolean threshold_condition = target.getInt(reducedValue) <= 0; //death's door, Madness/Nigredo

        //TODO
        int block = Integer.MAX_VALUE;
        if (hp) {
            // BlockType blockType =BlockType.Unblockable;
            BlockType blockType = BlockType.Melee;
            if (ref.get("attack") != null) {
                blockType = ref.get("attack").getEnum("block type", BlockType.class);
            }
            if (blockType == BlockType.Unblockable)
                block = 0;
            else {
                UnitParam blockParam = blockType.getParam();
                block = blockParam == null ? 0 : ref.get("attacked").getInt(blockParam);
            }
        }

        if (!threshold_condition || hp) {
            UnitParam bufferValue = hp ? UnitParam.Armor : soulBlocker;
            int buffer = target.getInt(bufferValue);
            buffer = Math.min(buffer, block); // e.g. armor can block up to [block]

            if (buffer > 0) {
                int remainder = damage - buffer;
                int blocked = Math.min(buffer, damage);
                result.__log__("blocked", blocked);
                target.addCurValue(bufferValue, -buffer);
                result.__log__(bufferValue.getName()+" reduced", buffer);

                if (target.getInt(bufferValue) == 0) {
                    /* Buffer is broken */
                    target.setValue(bufferValue + "_broken", true);
                    result.__log__(bufferValue.getName()+" broken", true);
                }
                if (remainder <= 0) {
                    printOut("Absorbed by ", bufferValue);
                    /* Absorbed by Armor/Faith/Sanity */
                    // target.addIntValue(value, -damage);
                    return true;
                }
                if (!hp) { // FAITH / SANITY - BREAK RULE - incurs WOUNDS
                    //Q: cannot hit soul with same atk that was buffer-blocked?
                    //... that follows from the logic of Wounds
                    //TODO
                    int excessDamage = remainder;
                    Wounds.apply(excessDamage, reducedValue, ref);
                }
                damage = remainder;
            }
        }

        reducedValue = hp ? UnitParam.Health : UnitParam.Soul;
        Integer current = target.getInt(reducedValue);

        boolean lethal = false;//TODO checkLethal(ref);
        boolean canKill = threshold_condition || lethal;
        // -HP > 2*MAX_HP
        // if (damage > current) {
        //     if (canKill) {
        //         target.setValue(reducedValue, Integer.MIN_VALUE);
        //         combat().getEntities().kill(ref, hp);
        //         result.__log__("lethal", true);
        //         return false;
        //     }
        // }
        int excessDamage = damage - current;
        if (excessDamage > 0) {
            damage = current;
            // if (excessDamage> threshold) kill()  => Can a single blow kill? With LETHAL perk
            logic.rules.combat.wounds.Wounds.apply(excessDamage, reducedValue, ref);
        }

        target.addCurValue(reducedValue, -damage); //reduce hp or soul
        result.__log__(reducedValue.getName()+" reduced", damage);

        return true;
    }

}
