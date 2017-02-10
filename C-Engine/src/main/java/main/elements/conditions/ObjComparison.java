package main.elements.conditions;


import main.entity.obj.Obj;

public class ObjComparison extends ConditionImpl {
    protected String arg1;
    protected String arg2;
    protected Integer id;

    public ObjComparison(String arg1, String arg2) {
        this.arg1 = arg1;
        this.arg2 = arg2;
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
    public boolean check() {

        Obj obj;
        if (id != null) {
            obj = ref.getGame().getObjectById(id);
        } else {
            obj = ref.getGame().getObjectById(ref.getId(arg2));
        }
        setTrue(ref.getGame().getObjectById(ref.getId(arg1)) == obj);
        return isTrue();
    }
}
