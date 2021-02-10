package eidolons.game.battlecraft.ai.elements.actions;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_QuickItemAction;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import main.entity.Ref;

/**
 * Created by JustMe on 3/3/2017.
 */
public class AiActionFactory {
    public static DC_UnitAction getUnitAction(Unit unit, String name) {
        return unit.getAction(name, true);
    }

    public static Action newAction(DC_ActiveObj action, Ref ref) {
        if (action instanceof DC_QuickItemAction) {
            DC_QuickItemAction itemActiveObj = (DC_QuickItemAction) action;
            return new AiQuickItemAction(itemActiveObj.getItem(), ref);
        }
        // my change!

        return new Action(action, ref);
    }

    public static Action newAction(String string, UnitAI ai) {
        return newAction(ai.getUnit().getActionOrSpell(string), ai.getUnit().getRef());
    }
}
