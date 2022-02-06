package eidolons.entity.mngr.action;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.item.QuickItem;
import eidolons.game.core.game.DC_Game;
import main.content.enums.entity.ActionEnums;
import main.system.threading.Weaver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 2/2/2022
 */
public class ActionHelper {
    public static List<ActiveObj> filterActionsByCanBePaid(List<ActiveObj> actions) {
        List<ActiveObj> list = new ArrayList<>();
        for (ActiveObj action : actions) {
            if (action.canBeActivated()) {
                list.add(action);
            }
        }
        // ++ can be targeted
        return list;
    }

    public static ActionEnums.ACTION_TYPE_GROUPS getStdObjType(ActiveObj activeObj) {
        for (ActionEnums.STD_ACTIONS action : ActionEnums.STD_ACTIONS.values()) {
            if (action.toString().equals(activeObj.getType().getName())) {
                switch (action) {
                    case Attack:
                        return ActionEnums.ACTION_TYPE_GROUPS.ATTACK;
                    case Move:
                        return ActionEnums.ACTION_TYPE_GROUPS.MOVE;
                    case Turn_Anticlockwise:
                    case Turn_Clockwise:
                        return ActionEnums.ACTION_TYPE_GROUPS.TURN;

                    default:
                        break;

                }
            }
        }
        for (ActionEnums.STD_ACTIONS action : ActionEnums.STD_ACTIONS.values()) {
            if (activeObj.getType().getName().contains(action.toString())) {
                switch (action) {
                    case Attack:
                        return ActionEnums.ACTION_TYPE_GROUPS.ATTACK;
                    case Move:
                        return ActionEnums.ACTION_TYPE_GROUPS.MOVE;
                    case Turn_Anticlockwise:
                    case Turn_Clockwise:
                        return ActionEnums.ACTION_TYPE_GROUPS.TURN;
                    default:
                        break;

                }
            }
        }

        return null;
    }

    public static void resetCostsInNewThread(DC_Game game) {
        Weaver.inNewThread(() -> {
            if (game.getManager().getActiveObj() == null) {
                return;
            }
            for (ActionEnums.ACTION_TYPE key : game.getManager().getActiveObj().getActionMap()
                    .keySet()) {
                for (ActiveObj active : game.getManager().getActiveObj()
                        .getActionMap().get(key)) {
                    active.initCosts();
                }
            }

            for (ActiveObj active : game.getManager().getActiveObj().getSpells()) {
                active.initCosts();
            }

            for (QuickItem item : game.getManager().getActiveObj().getQuickItems()) {
                if (item.getActive() != null) {
                    item.getActive().initCosts();
                }
            }
        });
    }
}
