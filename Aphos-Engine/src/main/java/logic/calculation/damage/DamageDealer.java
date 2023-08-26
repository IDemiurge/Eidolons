package logic.calculation.damage;

import elements.exec.EntityRef;
import elements.stats.UnitParam;
import elements.stats.UnitProp;
import framework.entity.Entity;
import framework.entity.field.FieldEntity;
import logic.rules.combat.wounds.Wounds;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

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
            if (!dealDamage(ref, damage.getType().isHp(), damage.getAmount())) {
                // interrupted = true; in what case?
                break;
            }
        }

        return result;
    }

    private static boolean dealDamage(EntityRef ref, boolean hp, Integer damage) {
        FieldEntity target = (FieldEntity) ref.get("target");
        return dealDamage(target, ref, hp, damage);
    }

    private static boolean dealDamage(FieldEntity target, EntityRef ref, boolean hp, Integer damage) {
        UnitParam
                value = hp ? UnitParam.Hp : target.isTrue(UnitProp.Pure) ? UnitParam.Faith : UnitParam.Sanity,
                soulBlocker = target.isTrue(UnitProp.Pure) ? UnitParam.Faith : UnitParam.Sanity;

        boolean threshold_condition = target.getInt(value) <= 0; //death's door, Madness/Nigredo

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

                int buffer = target.getInt(value = hp ? UnitParam.Armor : soulBlocker);

                buffer = Math.min(buffer, block);

                if (buffer > 0) {
                    int remainder = damage - buffer;
                    if (remainder < 0) {
                        printOut("Absorbed by ", value);
                        /* Absorbed by Armor/Faith/Sanity */
                        // target.addIntValue(value, -damage);
                        return true;
                    }
                    target.addCurValue(value, -buffer);

                    if (target.getInt(value) == 0 )
                    {
                        /* Buffer is broken */
                        target.setValue(value + "_broken", true);
                    }
                    if (!hp) {
                        //cannot hit soul with same atk that was buffer-blocked?
                        //that follows from the logic of Wounds
                        //TODO
                        int excessDamage = remainder;
                        Wounds.apply(excessDamage, value, ref);
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
            int excessDamage = damage - current;
            if (excessDamage > 0) {
                damage = current;
                // if (excessDamage> threshold) kill()  => Can a single blow kill? With LETHAL perk
                logic.rules.combat.wounds.Wounds.apply(excessDamage, value, ref);
            }

            target.addCurValue(value, -damage); //reduce hp or soul


            return true;
        }

    }
