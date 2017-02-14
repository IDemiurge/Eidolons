package main.ability.effects.oneshot;

import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;

public class ChangeRefEffect extends MicroEffect {
    Boolean add_remove;
    KEYS key;
    KEYS ref_key;

    KEYS[] autoCheckedKeys = {KEYS.SOURCE, KEYS.TARGET, KEYS.RANGED};

    public ChangeRefEffect(Boolean add_remove, KEYS key) {
        this.add_remove = add_remove;
        this.key = key;
    }

    @Override
    public boolean applyThis() {
        // so I need the active to have ref with the ammo as AMMO, not weapon?
        if (ref_key != null) {
            ref = ref.getObj(ref_key).getRef();
        } else {
            for (KEYS key : autoCheckedKeys) {
                if (ref.getObj(key).getRef().getId(this.key) != null) {
                    ref = ref.getObj(key).getRef();
                }
            }
        }
        Integer id = ref.getId(key);
        if (id == null) {
            if (ref.getActive() instanceof DC_ActiveObj) {
                DC_ActiveObj activeObj = (DC_ActiveObj) ref.getActive();
                if (activeObj.getParentAction() != null) {
                    id = activeObj.getParentAction().getRef().getId(key);
                }
            }
        }
        if (add_remove) {
            ref.getTargetObj().getRef().setID(key, id);
        } else {
            ref.setValue(key, null);
        }
        // ref.getTargetObj().getRef().setValue(key, null); ???

        return true;
    }

}
