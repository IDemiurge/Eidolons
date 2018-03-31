package eidolons.entity.obj;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
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
                break;
            case PARTY:
                break;
            case RANGED:
                return unit.getRangedWeapon();
        }
        return null;
    }

    public Obj getObj(ActionKey key, DC_ActiveObj unit) {

        switch (key) {
            case SOURCE:
                return unit.getOwnerObj();
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
