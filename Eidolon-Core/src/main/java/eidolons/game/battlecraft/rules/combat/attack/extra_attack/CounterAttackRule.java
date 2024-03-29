package eidolons.game.battlecraft.rules.combat.attack.extra_attack;

import eidolons.content.consts.VisualEnums;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import eidolons.game.core.game.DC_Game;
import eidolons.system.libgdx.GdxStatic;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref;
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

    public static boolean canCounter(Unit attacked, ActiveObj active) {
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

    public ActiveObj tryFindCounter(Attack attack) {
        return tryFindCounter(attack, true);
    }

    public ActiveObj tryFindCounter(Attack attack, boolean checkAnimationFinished) {
        //        if (checkAnimationFinished) {
        //        }

        ActiveObj counter = null;
        if (!attack.isRanged())
            if (attack.getAttackedUnit() != null)
                if (!attack.isCounter() &&
                        (RuleKeeper.isRuleTestOn(RuleEnums.RULE.COUNTER_ATTACK) ||
                                (attack.isCanCounter() &&
                                        //           attack.getAttackedUnit().
                                        canCounter((Unit) attack.getAttackedUnit(), attack.getAction())))
                ) {
                    counter = counter(attack.getAction(), (Unit) attack.getAttackedUnit(), attack.getAttacker());
                }

        return counter;

    }

    public void counterWith(ActiveObj action, ActiveObj counter) {
        Unit attacker = action.getOwnerUnit();
        Unit attacked = counter.getOwnerUnit();
        game.getLogManager().log(LogMaster.LOG.GAME_INFO, attacked + " makes a counter-attack against " +
                action.getOwnerUnit() +
                " " + StringMaster.wrapInParenthesis(action.getName()));
        Ref ref = (attacked.getRef()).getCopy();
        ref.setTarget(attacker.getId());
        game.fireEvent(new Event(Event.STANDARD_EVENT_TYPE.ATTACK_COUNTER, ref));


        GdxStatic.floatingText( VisualEnums.TEXT_CASES.COUNTER_ATTACK,
                "Counter Attack!", attacked);

        Unit target = action.getOwnerUnit();
        Unit source = counter.getOwnerUnit();
        counter.setCounterMode(true);
        boolean result = false;
        try {
            WaitMaster.WAIT(1000);
            game.getActionManager().activateAction(target, source, counter);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            counter.setCounterMode(false);
        }
    }

    private ActiveObj counter(ActiveObj action, Unit attacked, Unit attacker) {
        //        game.getLog().combatLog();
//        game.getLogManager().log(LogMaster.LOG.GAME_INFO, attacked + " tries to counter-attack against "
//         + action.getOwnerUnit());



        return  game.getActionManager().findCounterAttack(action,
                attacked);
    }
}
