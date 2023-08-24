package wip_from_core.condition;

import main.data.ability.OmittedConstructor;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.log.LogMaster;

public class RefCondition extends ConditionImpl {
    protected String id1;
    protected String id2;
    protected boolean negative = false;

    @OmittedConstructor
    public RefCondition(KEYS ref1, KEYS ref2, Boolean negative) {
        this.id1 = ref1.name();
        this.id2 = ref2.name();
        this.negative = negative;
    }

    // @OmittedConstructor
    public RefCondition(KEYS ref1, KEYS ref2) {
        this.id1 = ref1.name();
        this.id2 = ref2.name();
        this.negative = false;
    }

    public RefCondition(String ref1, String ref2) {
        this(ref1, ref2, false);
    }

    public RefCondition(String ref1, String ref2, Boolean negative) {
        this.id1 = ref1;
        this.id2 = ref2;
        this.negative = negative;

    }

    @Override
    public String toString() {
        return super.toString() + ": " + id1 + ((negative) ? " != " : " == ")
         + id2;
    }

    // example: dispel buffs on target: /*

    @Override
    public boolean check(Ref ref) {
        setTrue(false);
        try {
            Integer id = ref.getId(id1);
            if (id == null) {
                return negative;
            }
            Integer id_ = ref.getId(id2);
            if (id_ == null) {
                return negative;
            }
            setTrue((id.toString().equals(id_.toString())));
        } catch (Exception e) {
            LogMaster.log(1, toString() + " failed on "
             + ref);
        }
        if (negative) {
            setTrue(!isTrue());
        }
        return isTrue();
    }

}
