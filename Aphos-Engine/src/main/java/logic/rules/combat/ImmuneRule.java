package logic.rules.combat;

import elements.content.enums.types.CombatTypes;
import elements.stats.UnitProp;
import framework.entity.field.FieldEntity;
import framework.entity.field.Unit;

/**
 * Created by Alexander on 10/24/2023
 */
public class ImmuneRule {
    public static boolean checkImmune(FieldEntity target, CombatTypes.DamageType type, Unit source) {
        //penetration
        return target.checkContainerProp(UnitProp.Immune, type.toString());
        //check bonus prop - if some FX gives Immune on top of base ! Can it?..
    }

}
