package eidolons.game.battlecraft.rules.saves;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;

public class SavesMaster {

    public enum SaveType {
        Fortitude, Reflex, Grit, Wit, Spirit, Luck
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

    private SaveType getSaveType(DC_ActiveObj action, Unit target) {
        //check substitution
        return null;
    }

}