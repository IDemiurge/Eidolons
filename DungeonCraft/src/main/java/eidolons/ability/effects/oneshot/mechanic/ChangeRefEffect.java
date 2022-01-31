package eidolons.ability.effects.oneshot.mechanic;

import eidolons.entity.active.ActiveObj;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.data.ability.AE_ConstrArgs;
import main.entity.Ref;
import main.entity.Ref.KEYS;

public class ChangeRefEffect extends MicroEffect implements OneshotEffect {
    Boolean add_remove;
    KEYS keyToAddOrRemoveForRef;
    KEYS keyWhoseRefToModify;

    KEYS[] autoCheckedKeysAdd = {KEYS.TARGET, KEYS.RANGED, KEYS.SOURCE};
    KEYS[] autoCheckedKeysRemove = {KEYS.TARGET, KEYS.RANGED, KEYS.SOURCE};

    @AE_ConstrArgs(argNames = {"add_remove", "keyToAddOrRemoveForRef"})
    public ChangeRefEffect(Boolean add_remove, KEYS keyToAddOrRemoveForRef) {
        this(keyToAddOrRemoveForRef, add_remove, KEYS.TARGET);
    }

    @AE_ConstrArgs(argNames = {"keyWhoseRefToModify", "add_remove", "keyToAddOrRemoveForRef?"})
    public ChangeRefEffect(KEYS keyWhoseRefToModify, Boolean add_remove, KEYS keyToAddOrRemoveForRef) {
        this.add_remove = add_remove;
        this.keyWhoseRefToModify = keyWhoseRefToModify;
        this.keyToAddOrRemoveForRef = keyToAddOrRemoveForRef;
    }

    @Override
    public boolean applyThis() {
        Ref REF = this.ref;    // so I need the active to have ref with the ammo as AMMO, not weapon?
        if (keyWhoseRefToModify != null) {
            REF = REF.getObj(keyWhoseRefToModify).getRef();
        } else {
//            for (KEYS key :
//             add_remove ? autoCheckedKeysAdd : autoCheckedKeysRemove)
//            {
//                if (REF.getObj(key)!=null )
//                if (REF.getObj(key).getRef().getId(this.keyToAddOrRemoveForRef) != null) {
//                    REF = ref.getObj(key).getRef();
//                    break;
//                }
//            }
        }
        Integer id = REF.getId(keyToAddOrRemoveForRef);
        if (id == null) {
            if (ref.getActive() instanceof ActiveObj) {
                ActiveObj activeObj = (ActiveObj) ref.getActive();
                if (activeObj.getParentAction() != null) {
                    id = activeObj.getParentAction().getRef().getId(keyToAddOrRemoveForRef);
                }
            }
        }
        if (add_remove) {
            REF.getTargetObj().getRef().setID(keyToAddOrRemoveForRef, id);
        } else {
            REF.removeValue(keyToAddOrRemoveForRef);
        }
        // ref.getTargetObj().getRef().setValue(key, null); ???

        return true;
    }

}
