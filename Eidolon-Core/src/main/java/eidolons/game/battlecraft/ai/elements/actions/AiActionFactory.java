package eidolons.game.battlecraft.ai.elements.actions;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.feat.active.QuickItemAction;
import eidolons.entity.feat.active.UnitAction;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import main.entity.Ref;

/**
 * Created by JustMe on 3/3/2017.
 */
public class AiActionFactory {
    public static UnitAction getUnitAction(Unit unit, String name) {
        return unit.getAction(name, true);
    }

    public static AiAction newAction(ActiveObj action, Ref ref) {
        if (action instanceof QuickItemAction) {
            QuickItemAction itemActiveObj = (QuickItemAction) action;
            return new AiQuickItemAction(itemActiveObj.getItem(), ref);
        }
        // my change!

        return new AiAction(action, ref);
    }

    public static AiAction newAction(String string, UnitAI ai) {
        return newAction(ai.getUnit().getActionOrSpell(string), ai.getUnit().getRef());
    }
}
