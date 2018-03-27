package main.ability.conditions;

import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.EnumMaster;

public class DamageTypeCondition extends MicroCondition {

    public static final String REF = "{EVENT_" + Ref.KEYS.DAMAGE_TYPE.name()
            + "}";
    private DAMAGE_TYPE dmg_type;
    private Boolean physical_magical;

    public DamageTypeCondition(Boolean physical_magical) {
        this.physical_magical = physical_magical;
    }

    public DamageTypeCondition(DAMAGE_TYPE dmg_type) {
        this.dmg_type = dmg_type;
    }

    public boolean check(Ref ref) {

        DAMAGE_TYPE event_damage_type = new EnumMaster<DAMAGE_TYPE>()
                .retrieveEnumConst(DAMAGE_TYPE.class, ref.getEvent().getRef()
                                .getValue(KEYS.DAMAGE_TYPE)
                        // new Property(REF) .getStr(ref)
                );

        if (physical_magical != null && event_damage_type != null) {
            if (physical_magical) {
                return !event_damage_type.isMagical();
            } else {
                return event_damage_type.isMagical();
            }
        }
        return event_damage_type == dmg_type;
    }

}
