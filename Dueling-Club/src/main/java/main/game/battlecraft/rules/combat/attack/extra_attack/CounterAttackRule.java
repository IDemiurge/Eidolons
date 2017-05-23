package main.game.battlecraft.rules.combat.attack.extra_attack;

import main.content.enums.entity.UnitEnums;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.rules.RuleMaster;
import main.game.battlecraft.rules.RuleMaster.RULE;
import main.game.battlecraft.rules.combat.attack.Attack;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 3/16/2017.
 */
public class CounterAttackRule {

    DC_Game game;

    public CounterAttackRule(DC_Game game) {
        this.game = game;
    }

    public static boolean canCounter(Unit attacked, DC_ActiveObj active) {
        if (!attacked.canCounter()) {
            return false;
        }
        if (active.checkPassive(UnitEnums.STANDARD_PASSIVES.NO_RETALIATION)) {
            return false;
        }
        // if (!attacked.checkPassive(STANDARD_PASSIVES.VIGILANCE))
        if (active.getOwnerObj().checkPassive(UnitEnums.STANDARD_PASSIVES.NO_RETALIATION)) {
            return false;
        }
        return true;
    }

    public ActiveObj tryCounter(Attack attack) {
        return tryCounter(attack, true);
    }

    public ActiveObj tryCounter(Attack attack, boolean checkAnimationFinished) {
        if (checkAnimationFinished) {
            if (attack.getAnimation() != null) {
//                waitForAttackAnimation(attack);
            }
        }

        ActiveObj counter = null;
        if (attack.getAttackedUnit()!=null )
        if (!attack.isCounter() &&
         (RuleMaster.isRuleTestOn(RULE.COUNTER_ATTACK) ||
          (attack.isCanCounter() &&
//           attack.getAttackedUnit().
            canCounter((Unit) attack.getAttackedUnit(), attack.getAction())))
         ) {
            counter = counter(attack.getAction(), (Unit) attack.getAttackedUnit());
        }

        return counter;

    }

    private ActiveObj counter(DC_ActiveObj action, Unit attacked) {
        return game.getActionManager().activateCounterAttack(action,
         attacked);
    }
}
