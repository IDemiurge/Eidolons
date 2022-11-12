package eidolons.game.battlecraft.rules.saves;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;

public class SavesMaster {

    /**
     * specifically for saving rolls vs CONST VALUE
     * @param saveType
     * @param target
     * @param value
     * @return
     */
    public static boolean savingThrow(SaveType saveType, BattleFieldObject target, Integer value) {

        return false;
    }

    public enum SaveType {
        Might, Reflex, Grit, Wit, Spirit, Luck
    }

    // public void processActionSaves(DC_ActiveObj action, Unit target) {
    //     SaveType save = getSaveType(action, target);
    //     if (save == null) return;
    //     while (true) {
    //         Ref eRef = new Ref(target);
    //         eRef.setArg(save);
    //         new Event(Event.STANDARD_EVENT_TYPE.SAVE_BEING_MADE, eRef).fire();
    //         if (save == eRef.getArg())
    //             break;
    //         save = eRef.getArg();
    //     }
    //     if (save == null) return;
    //
    //     String sVal = String.valueOf(getDC_Value(action));
    //     String tVal = String.valueOf(getTargetValue(action));
    //
    //     GenericEnums.DieType dieType = d20;
    //     GenericEnums.RollType type = getRollType(save);
    //     Roll roll = new Roll(type, dieType, sVal, tVal);
    //     BattleFieldObject source = action.getOwnerObj();
    //     Ref ref = new Ref(source, target);
    //     boolean result = RollMaster.roll(roll, ref);
    //     //swap? source vs target
    //     if (result) {
    //         new Event(Event.STANDARD_EVENT_TYPE.SAVE_MADE, eRef).fire();
    //
    //     } else {
    //         new Event(Event.STANDARD_EVENT_TYPE.SAVE_FAILED, eRef).fire();
    //
    //     }
    // }

    private SaveType getSaveType(ActiveObj action, Unit target) {
        //check substitution
        return null;
    }

}
