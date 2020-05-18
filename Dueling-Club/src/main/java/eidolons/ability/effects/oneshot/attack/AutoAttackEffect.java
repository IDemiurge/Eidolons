package eidolons.ability.effects.oneshot.attack;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import eidolons.game.core.EUtils;
import eidolons.game.netherflame.main.death.ShadowMaster;
import main.ability.effects.OneshotEffect;
import main.content.enums.entity.ActionEnums;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.system.auxiliary.RandomWizard;

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
            if (getUnit() == ShadowMaster.getShadowUnit()) {
                attack = getActiveObj().getSubActions().get(0);
            } else {
                ref.getActive().setCancelled(true);
                EUtils.showInfoText("Could not find an attack!");
                return false;
            }
        }

        //TODO igg demo hack
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
        DC_ActiveObj attack = getActiveObj().getOwnerUnit().getAttackOfType(ActionEnums.ATTACK_TYPE.STANDARD_ATTACK);
        List<DC_ActiveObj> subActions = getActiveObj().getValidSubactions(ref, target);
        if (subActions.isEmpty()) {
            main.system.auxiliary.log.LogMaster.log(1, "Failing on our autoattack ... ");

            for (DC_ActiveObj subAction : getActiveObj().getSubActions()) {
                if (subAction.isThrow()) {
                    continue;
                }
                if (!subAction.canBeTargeted(target)) {
                    Ref REF = ref.getCopy();
                    REF.setMatch(target);
                    for (Condition condition : subAction.getTargeting().getFilter().getConditions()) {
                        if (!condition.check(REF)) {
                            main.system.auxiliary.log.LogMaster.log(1, "Breaking our autoattack: " + condition);
                        }
                    }
                } else {
                    main.system.auxiliary.log.LogMaster.log(1, "It worked?: " + subAction);
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
        return pick;
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
        int max = Integer.MIN_VALUE;

        for (DC_ActiveObj attack : subActions) {
            int priority = calculatePriority(attack, getTarget());
            priority += RandomWizard.getRandomIntBetween(-2, 2);
            if (priority > max) {
                pick = attack;
                max = priority;
            }
        }
        return pick;
    }

}
