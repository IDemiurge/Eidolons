package logic.rules.combat;

import elements.content.enums.types.CombatTypes;
import elements.exec.EntityRef;
import elements.stats.Counter;
import elements.stats.UnitProp;
import framework.entity.field.FieldEntity;
import framework.entity.field.Unit;

import static elements.content.enums.types.CombatTypes.*;

/**
 * Created by Alexander on 10/24/2023
 */
public class WardRule {
    public static boolean checkWard(FieldEntity target, DamageType type, Unit source) {
        //penetration
        boolean wardBroken = target.isTrue(getBrokenKey(type));
        if (wardBroken)
            return false;
        boolean hasWard = target.checkContainerProp(UnitProp.Wards, type.toString());
        //check bonus prop - if some FX gives ward on top of base ones! Can it?..
        if (!hasWard)
            return false;

        return breakWard(target, type, source);
    }
    public static boolean checkWardVsCounter(Counter counter, Unit unit, EntityRef ref) {
        DamageType damageType= switch (counter){
            case Poison -> DamageType.Poison;
            case Blaze -> DamageType.Fire;
            case Bleed -> DamageType.Bleed;
            default -> null;
        };
        if (damageType==null)
            return false;
        return checkWard(unit,damageType, ref.getSource());
    }

    private static boolean breakWard(FieldEntity target, DamageType type, Unit source) {
        target.setValuePersistent(getBrokenKey(type), true);
        //some overrides?
        return true;
    }

    public static String getBrokenKey(DamageType type) {
        return type.toString() + " Ward Broken";
    }

}
