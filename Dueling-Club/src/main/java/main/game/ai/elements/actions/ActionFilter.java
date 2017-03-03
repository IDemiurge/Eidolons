package main.game.ai.elements.actions;

import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.ai.elements.goal.Goal.GOAL_TYPE;
import main.game.ai.tools.target.AI_SpellMaster;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 3/3/2017.
 */
public class ActionFilter {
    static List<DC_ActiveObj> filterByCanActivate(Unit unit,
                                                  List<DC_ActiveObj> actionsList) {
        List<DC_ActiveObj> list = new LinkedList<>();
        for (DC_ActiveObj a : actionsList) {
            if (a.canBeActivated(unit.getRef(), true) || checkException(a)) {
                list.add(a);
            }
        }
        return list;
    }

    private static boolean checkException(DC_ActiveObj a) {
        if (a.isRanged()) {
            if (!a.isThrow()) {
                return true;
            }
        }
        return false;
    }

    static Collection<? extends DC_ActiveObj> filterActives(GOAL_TYPE type,
                                                            List<? extends DC_ActiveObj> spells) {
        List<DC_ActiveObj> list = new LinkedList<>();
        for (DC_ActiveObj spell : spells) {
            GOAL_TYPE goal = null;
            try {
                goal = AI_SpellMaster.getGoal(spell);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (goal != null) {
                if (goal.equals(type)) {
                    list.add(spell);
                }
            }
        }
        return list;
    }
}
