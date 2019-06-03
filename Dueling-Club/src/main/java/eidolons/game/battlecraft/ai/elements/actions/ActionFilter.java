package eidolons.game.battlecraft.ai.elements.actions;

import eidolons.entity.active.DC_ActionManager;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.handlers.bf.unit.UnitChecker;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.ai.tools.target.AI_SpellMaster;
import main.content.enums.system.AiEnums.GOAL_TYPE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by JustMe on 3/3/2017.
 */
public class ActionFilter {
    static List<DC_ActiveObj> filterByCanActivate(Unit unit,
                                                  List<DC_ActiveObj> actionsList) {
        List<DC_ActiveObj> list = new ArrayList<>();
        for (DC_ActiveObj a : actionsList) {
            if (a==null || unit == null ){
                continue;
            } //a.getCosts().canBePaid(unit.getRef())
            if (a.canBeActivated(unit.getRef(), true) || checkException(a)) {
                list.add(a);
            }
        }
        return list;
    }

    private static boolean checkException(DC_ActiveObj a) {
        if (AI_Manager.getBrokenActions().contains(a))
            return true;
        if (a.isRanged()) {
            if (!a.isThrow()) {
                return true;
            }
        }
        return false;
    }

    static Collection<? extends DC_ActiveObj> filterActives(GOAL_TYPE type,
                                                            List<? extends DC_ActiveObj> spells) {
        List<DC_ActiveObj> list = new ArrayList<>();
        for (DC_ActiveObj spell : spells) {
            GOAL_TYPE goal = null;
            try {
                goal = AI_SpellMaster.getGoal(spell);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            if (goal != null) {
                if (goal.equals(type)) {
                    list.add(spell);
                }
            }
        }
        return list;
    }

    public static void filterAttacks(List<DC_ActiveObj> actions, Unit unit) {
        actions.remove(AiActionFactory.getUnitAction(unit, DC_ActionManager.OFFHAND_ATTACK));
        DC_UnitAction
                action = unit.getAction(
                "Throw", false);
        actions.remove(action);
        action = unit.getAction(
                "Throw", false);
        actions.remove(action);
if (!UnitChecker.isUnarmedFighter(unit))
        if (unit.getWeapon(false)!=null  || unit.getWeapon(true)!=null )
            actions.removeIf(a -> a.getChecker().isUnarmed());
    }
}
