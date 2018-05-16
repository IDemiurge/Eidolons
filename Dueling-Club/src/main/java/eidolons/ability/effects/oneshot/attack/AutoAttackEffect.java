package eidolons.ability.effects.oneshot.attack;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import main.ability.effects.OneshotEffect;

import java.util.ArrayList;
import java.util.List;

public class AutoAttackEffect extends DC_Effect implements OneshotEffect {
    private boolean manual;

    // boolean offhand; auto!

    @Override
    public boolean applyThis() {
        // auto-select action
        // getUnit().getAction(action)
        DC_ActiveObj attack = pickAttack();
        if (attack == null) {
            return false;
        }
        boolean result = attack.activatedOn(ref);
        if (result) {
            if (getActiveObj().getParentAction() != null) {
                getActiveObj().getParentAction().setLastSubaction(attack);
                getActiveObj().getParentAction().getRef().setTarget(ref.getTarget());
            }
        } else {
            return false;
        }
        // getRef().getActive()
        return true;
    }

    @Override
    public boolean isAnimationDisabled() {
        return true;
    }

    private int calculatePriority(DC_ActiveObj attack, BattleFieldObject target) {
        return DC_PriorityManager.getAttackPriority(attack, target);
    }

    private DC_ActiveObj pickAttack() {
        List<DC_ActiveObj> subActions = new ArrayList<>();
        for (DC_ActiveObj attack : getActiveObj().getSubActions()) {
            if (attack.canBeActivated(ref, true)) {
                if (attack.canBeTargeted(target)) {
                    subActions.add(attack);
                }
            }
        }
        if (subActions.size() == 1) {
            return subActions.get(0);
        }
        if (!getGame().isOffline()) {
            if (!getUnit().isMine()) {
//                String name = WaitingThread.waitOrGetInput(HOST_CLIENT_CODES.CUSTOM_PICK);
//                return new ListMaster<DC_ActiveObj>().findType(name, subActions);
            }
        }
        if (getUnit().isAiControlled() || isPickAutomaticallyOn()) {
            return pickAutomatically(subActions);
        }
        DC_ActiveObj pick = null;
        if (manual)
            try {
                pick = pickManually(subActions);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        if (pick == null)
            pick = pickAutomatically(subActions);
        if (pick != null) {
            return pick;
        }
        return null;
    }

    private boolean isPickAutomaticallyOn() {
        // return getActiveObj().isAutoSelectionOn();
        return getActiveObj().isAutoSelectionOn();
    }

    private DC_ActiveObj pickManually(List<DC_ActiveObj> subActions) {
        if (!manual) {
            throw new RuntimeException();
        }
//        AttackChoicePanel dialog = new AttackChoicePanel(subActions, getTarget());
//        DC_ActiveObj action =
//         dialog.chooseEntity();
//        if (action != null) {
//            if (!getGame().isOffline()) {
//                if (getGame().getConnection() != null) {
//                    getGame().getConnection().send(HOST_CLIENT_CODES.CUSTOM_PICK, action.getName());
//                }
//            }
//        }
//        return action;
        // Obj obj = DialogMaster.objChoice("Pick an Attack Type", subActions
        // .toArray(new DC_ActiveObj[subActions.size()]));
        // if (obj == null)
        // return null;
        // if (!getGame().isOffline())
        // if (getGame().getConnection() != null)
        // getGame().getConnection().send(HOST_CLIENT_CODES.CUSTOM_PICK,
        // obj.getName());
        // return (DC_ActiveObj) obj;
        return null;
    }

    private DC_ActiveObj pickAutomatically(List<DC_ActiveObj> subActions) {
        DC_ActiveObj pick = null;
        int max = 0;

        for (DC_ActiveObj attack : subActions) {
            int priority = calculatePriority(attack, getTarget());
            if (priority > max) {
                pick = attack;
                max = priority;
            }
        }
        return pick;
    }

}
