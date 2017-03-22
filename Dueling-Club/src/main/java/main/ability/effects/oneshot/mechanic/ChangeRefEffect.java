package main.ability.effects.oneshot.mechanic;

import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;

public class ChangeRefEffect extends MicroEffect implements OneshotEffect {
    Boolean add_remove;
    KEYS key;
    KEYS keyToAddTo;

    KEYS[] autoCheckedKeysAdd = { KEYS.TARGET, KEYS.RANGED,KEYS.SOURCE};
    KEYS[] autoCheckedKeysRemove = { KEYS.TARGET, KEYS.RANGED,KEYS.SOURCE};

    public ChangeRefEffect(Boolean add_remove, KEYS key) {
        this.add_remove = add_remove;
        this.key = key;
    }
    public ChangeRefEffect(KEYS keyToAddTo,Boolean add_remove,  KEYS keyToAdd) {
        this.add_remove = add_remove;
        this.key = key;
    }

    @Override
    public boolean applyThis() {
    Ref REF=this.ref;    // so I need the active to have ref with the ammo as AMMO, not weapon?
        if (keyToAddTo != null) {
            REF = REF.getObj(keyToAddTo).getRef();
        } else {
//            if (!add_remove) //TODO
//            for (KEYS key : autoCheckedKeys) { if (REF.getObj(key)!=null )
//                if (REF.getObj(key).getRef().getId(this.key) != null) {
//                    REF = ref.getObj(key).getRef(); break;
//                }
//            }
        }
        Integer id = REF.getId(key);
        if (id == null) {
            if (ref.getActive() instanceof DC_ActiveObj) {
                DC_ActiveObj activeObj = (DC_ActiveObj) ref.getActive();
                if (activeObj.getParentAction() != null) {
                    id = activeObj.getParentAction().getRef().getId(key);
                }
            }
        }
        if (add_remove) {
            REF.getTargetObj().getRef().setID(key, id);
        } else {
            REF.removeValue(key);
        }
        // ref.getTargetObj().getRef().setValue(key, null); ???

        return true;
    }

}
