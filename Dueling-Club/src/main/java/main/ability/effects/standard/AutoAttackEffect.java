package main.ability.effects.standard;

import main.ability.effects.DC_Effect;
import main.client.net.GameConnector.HOST_CLIENT_CODES;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.DC_HeroObj;
import main.game.ai.tools.priority.PriorityManager;
import main.swing.generic.services.dialog.AttackChoicePanel;
import main.system.auxiliary.data.ListMaster;
import main.system.net.WaitingThread;

import java.util.LinkedList;
import java.util.List;

public class AutoAttackEffect extends DC_Effect {

    // boolean offhand; auto!

    @Override
    public boolean applyThis() {
        // auto-select action
        // getUnit().getAction(action)
        DC_ActiveObj attack = pickAttack();
        if (attack == null) {
            return false;
        }
        boolean result = attack.activate(ref);
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

    private int calculatePriority(DC_ActiveObj attack, DC_HeroObj target) {
        return PriorityManager.getAttackPriority(attack, target);
    }

    private DC_ActiveObj pickAttack() {
        List<DC_ActiveObj> subActions = new LinkedList<>();
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
                String name = WaitingThread.waitOrGetInput(HOST_CLIENT_CODES.CUSTOM_PICK);
                return new ListMaster<DC_ActiveObj>().findType(name, subActions);
            }
        }
        if (getUnit().isAiControlled() || isPickAutomaticallyOn()) {
            return pickAutomatically(subActions);
        }
        DC_ActiveObj pick;
        try {
            pick = pickManually(subActions);
        } catch (Exception e) {
            e.printStackTrace();
            pick = pickAutomatically(subActions);
        }
        if (pick != null) {
            return pick;
        }
        return null;
    }

    private boolean isPickAutomaticallyOn() {
        // return getActiveObj().isSwitchOn();
        return getActiveObj().isSwitchOn();
    }

    private DC_ActiveObj pickManually(List<DC_ActiveObj> subActions) {
        AttackChoicePanel dialog = new AttackChoicePanel(subActions, getTarget());
        DC_ActiveObj action = dialog.chooseEntity();
        if (action != null) {
            if (!getGame().isOffline()) {
                if (getGame().getConnection() != null) {
                    getGame().getConnection().send(HOST_CLIENT_CODES.CUSTOM_PICK, action.getName());
                }
            }
        }
        return action;
        // Obj obj = DialogMaster.objChoice("Pick an Attack Type", subActions
        // .toArray(new DC_ActiveObj[subActions.size()]));
        // if (obj == null)
        // return null;
        // if (!getGame().isOffline())
        // if (getGame().getConnection() != null)
        // getGame().getConnection().send(HOST_CLIENT_CODES.CUSTOM_PICK,
        // obj.getName());
        // return (DC_ActiveObj) obj;
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
