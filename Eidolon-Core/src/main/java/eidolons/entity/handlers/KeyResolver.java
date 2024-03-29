package eidolons.entity.handlers;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import main.entity.obj.Obj;
import main.game.logic.action.context.Context.IdKey;

/**
 * Created by JustMe on 4/5/2017.
 */
public class KeyResolver {
    public DC_Obj getObj(IdKey key, Unit unit) {
        switch (key) {
            case WEAPON:
                return unit.getActiveWeapon(false);
            case ARMOR:
                return unit.getArmor();
            case OFFHAND:
                return unit.getActiveWeapon(true);
            case SUMMONER:
            case PARTY:
                break;
            case RANGED:
                return unit.getRangedWeapon();
        }
        return null;
    }

    public Obj getObj(ActionKey key, ActiveObj unit) {

        switch (key) {
            case SOURCE:
                return unit.getOwnerUnit();
            case TARGET:
                return unit.getTargetObj();
        }
        return null;
    }

    public enum ActionKey {
        SOURCE,
        TARGET,



    }
}
