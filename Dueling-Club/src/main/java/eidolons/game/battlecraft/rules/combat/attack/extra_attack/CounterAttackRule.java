package eidolons.game.battlecraft.rules.combat.attack.extra_attack;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.RuleKeeper.RULE;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.game.logic.event.Event;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.threading.WaitMaster;

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
        if (attacked.isAlliedTo(active.getOwnerUnit().getOwner()))
            return false;
        if (active.checkPassive(UnitEnums.STANDARD_PASSIVES.NO_RETALIATION)) {
            return false;
        }
        // if (!attacked.checkPassive(STANDARD_PASSIVES.VIGILANCE))
        return !active.getOwnerUnit().checkPassive(UnitEnums.STANDARD_PASSIVES.NO_RETALIATION);
    }

    public ActiveObj tryCounter(Attack attack) {
        return tryCounter(attack, true);
    }

    public ActiveObj tryCounter(Attack attack, boolean checkAnimationFinished) {
        //        if (checkAnimationFinished) {
        //        }

        ActiveObj counter = null;
        if (!attack.isRanged())
        if (attack.getAttackedUnit() != null)
            if (!attack.isCounter() &&
             (RuleKeeper.isRuleTestOn(RULE.COUNTER_ATTACK) ||
              (attack.isCanCounter() &&
               //           attack.getAttackedUnit().
               canCounter((Unit) attack.getAttackedUnit(), attack.getAction())))
             ) {
                counter = counter(attack.getAction(), (Unit) attack.getAttackedUnit(), attack.getAttacker());
            }

        return counter;

    }

    private ActiveObj counter(DC_ActiveObj action, Unit attacked, Unit attacker) {
        //        game.getLog().combatLog();
//        game.getLogManager().log(LogMaster.LOG.GAME_INFO, attacked + " tries to counter-attack against "
//         + action.getOwnerUnit());
        ActiveObj activeObj = game.getActionManager().activateCounterAttack(action,
         attacked);
        if (activeObj == null) {
            game.getLogManager().log(LogMaster.LOG.GAME_INFO, attacked + " fails to counter-attack against " +
             action.getOwnerUnit());
            return null;
        }
        game.getLogManager().log(LogMaster.LOG.GAME_INFO, attacked + " makes a counter-attack against " +
         action.getOwnerUnit() +
         " " + StringMaster.wrapInParenthesis(activeObj.getName()));

        WaitMaster.WAIT(3000);
//        WaitMaster.(1000);

        Ref ref =  (attacked.getRef()).getCopy();
        ref.setTarget(attacker.getId());
        game.fireEvent(new Event(Event.STANDARD_EVENT_TYPE.ATTACK_COUNTER, ref));

        FloatingTextMaster.getInstance().createFloatingText(FloatingTextMaster.TEXT_CASES.COUNTER_ATTACK,
                "Counter Attack!", attacked);

        return activeObj;
    }
}
