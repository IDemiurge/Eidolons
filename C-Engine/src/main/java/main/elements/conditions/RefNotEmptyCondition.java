package main.elements.conditions;

import main.entity.Ref;
import main.system.auxiliary.log.LogMaster;

public class RefNotEmptyCondition extends MicroCondition {
    private String obj;
    private String key;

    public RefNotEmptyCondition(String ref1, String ref2) {
        this.obj = ref1;
        this.key = ref2;

    }

    public RefNotEmptyCondition(String ref1, String ref2, Boolean negative) {

    }

    @Override
    public String toString() {
        return super.toString() + ": " + obj + "'s " + key;
    }

    @Override
    public boolean check(Ref ref) {
        if (ref.getObj(obj) !=null ){
            return ref.getObj(obj).getRef().getObj(key) != null;
        }
        LogMaster.log(1, toString() + " failed on "
         + ref);
        return false;
    }
}
