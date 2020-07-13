package eidolons.ability.effects.oneshot.attack;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import eidolons.game.core.EUtils;
import eidolons.game.netherflame.main.death.ShadowMaster;
import main.ability.effects.OneshotEffect;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.system.auxiliary.RandomWizard;

import java.util.List;

import static main.system.auxiliary.log.LogMaster.log;

public class AutoAttackEffect extends DC_Effect implements OneshotEffect {

    @Override
    public boolean applyThis() {
        DC_ActiveObj attack = pickAttack();
        if (attack == null) {
            if (getSourceUnitOrNull() == ShadowMaster.getShadowUnit()) {
                attack = getActiveObj().getSubActions().get(0);
            } else {
                ref.getActive().setCancelled(true);
                EUtils.showInfoText("Could not find an attack!");
                return false;
            }
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
        List<DC_ActiveObj> subActions = getActiveObj().getValidSubactions(ref, target);
        if (subActions.isEmpty()) {
            log(1, "Failing on our autoattack ... ");

            for (DC_ActiveObj subAction : getActiveObj().getSubActions()) {
                if (subAction.isThrow()) {
                    continue;
                }
                if (!subAction.canBeTargeted(target)) {
                    Ref REF = ref.getCopy();
                    REF.setMatch(target);
                    for (Condition condition : subAction.getTargeting().getFilter().getConditions()) {
                        if (!condition.check(REF)) {
                            log(1, "Breaking our autoattack: " + condition);
                        }
                    }
                } else {
                    log(1, "It worked?: " + subAction);
                }
            }
        }
        if (subActions.size() == 1) {
            return subActions.get(0);
        }
        if (getSourceUnitOrNull().isAiControlled() || isPickAutomaticallyOn()) {
            return pickAutomatically(subActions);
        }
        DC_ActiveObj pick = pickAutomatically(subActions);
        return pick;
    }

    private boolean isPickAutomaticallyOn() {
        return getActiveObj().isAutoSelectionOn();
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
