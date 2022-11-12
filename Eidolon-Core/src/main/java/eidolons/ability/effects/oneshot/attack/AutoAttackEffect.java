package eidolons.ability.effects.oneshot.attack;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import eidolons.game.core.EUtils;
import main.ability.effects.OneshotEffect;
import main.system.auxiliary.RandomWizard;
import main.system.launch.Flags;

import java.util.List;

import static main.system.auxiliary.log.LogMaster.log;

public class AutoAttackEffect extends DC_Effect implements OneshotEffect {

    @Override
    public boolean applyThis() {
        ActiveObj attack = pickAttack();
        if (attack == null) {
                ref.getActive().setCancelled(true);
                EUtils.showInfoText("Could not find an attack!");
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
        return true;
    }

    @Override
    public boolean isAnimationDisabled() {
        return true;
    }

    private int calculatePriority(ActiveObj attack, BattleFieldObject target) {
        return DC_PriorityManager.getAttackPriority(attack, target);
    }

    private ActiveObj pickAttack() {
        if (Flags.isSafeMode())
            return getSourceUnitOrNull().getStdAttack();
        List<ActiveObj> subActions = getActiveObj().getValidSubactions(ref, target);

        //TODO NF Rules revamp
        if (subActions.size() == 1) {
            return subActions.get(0);
        }
        if (getSourceUnitOrNull().isAiControlled() || isPickAutomaticallyOn()) {
            return pickAutomatically(subActions);
        }
        return pickAutomatically(subActions);
    }

    private boolean isPickAutomaticallyOn() {
        return getActiveObj().isAutoSelectionOn();
    }

    private ActiveObj pickAutomatically(List<ActiveObj> subActions) {
        ActiveObj pick = null;
        int max = Integer.MIN_VALUE;

        for (ActiveObj attack : subActions) {
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
