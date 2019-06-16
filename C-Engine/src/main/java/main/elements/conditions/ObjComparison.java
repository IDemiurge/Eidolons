package main.elements.conditions;


import main.data.ability.OmittedConstructor;
import main.entity.Ref;
import main.entity.obj.Obj;

import java.util.function.Supplier;

public class ObjComparison extends ConditionImpl {
    protected Obj obj;
    protected String arg1;
    protected String arg2;
    protected Integer id;
    protected Supplier<Integer> idSupplier;

    public ObjComparison(String arg1, String arg2) {
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    @OmittedConstructor
    public ObjComparison(Supplier<Integer> idSupplier, String arg1) {
        this.arg1 = arg1;
        this.idSupplier = idSupplier;
    }

    @OmittedConstructor
    public ObjComparison(Integer id, String arg1) {
        this.arg1 = arg1;
        this.id = id;
    }

    @OmittedConstructor
    public ObjComparison(Obj obj, String arg1) {
        this.arg1 = arg1;
        this.obj = obj;
    }

    // example: dispel buffs on target: /*
    /*
     * ability 1: select target (emply ability) ability 2: targeting: auto
     * (filter(conditions: 1) obj_class ==buff 2) idCondition:
     * ABILITY_1(target)==MATCH_TARGET(buff's basis)) effect: saving throw
     * effect: caster roll: ("SOURCE", "SP" + "SOURCE", "INT" + "SOURCE", target
     * roll: ("TARGET_SOURCE", "SP" + "ABILITY_1", "WIL" + "TARGET_SPELL", "SD")
     *
     * (note: these abilities get ref from saving throw effect) success: null
     * fail: ability(targeting: fixed(TARGET); effect: destroyObjEffect
     */
    @Override
    public boolean check(Ref ref) {

        Integer id1 = ref.getId(arg1);
        if (id1 == null) {
            id1 = ref.getId(arg1);
        }
        Integer id2 = ref.getId(arg2);
        if (id2 == null) {
            id2 = ref.getId(arg2);
        }

        if (idSupplier != null)
            id = idSupplier.get();
        if (id != null) {
            id1 = id;
        }
        if (id != null) {
            obj = ref.getGame().getObjectById(id1);
        } else if (arg2 != null) {
            obj = ref.getGame().getObjectById(id2);
        }
        setTrue(ref.getGame().getObjectById(id) == obj);
        return isTrue();
    }
}
