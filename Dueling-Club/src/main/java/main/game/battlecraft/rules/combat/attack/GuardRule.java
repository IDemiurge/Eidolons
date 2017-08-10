package main.game.battlecraft.rules.combat.attack;

import main.content.enums.GenericEnums.ROLL_TYPES;
import main.content.mode.STD_MODES;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.tools.target.EffectFinder;
import main.system.math.roll.RollMaster;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 8/10/2017.
 */
public class GuardRule {

    public static BattleFieldObject checkTargetChanged(DC_ActiveObj action) {

        BattleFieldObject target = (BattleFieldObject) action.getTargetObj();
//         attack.getAttackedUnit();
        List<Unit> defenders = new LinkedList<>();
        Collection<Unit> units = target.getGame().getUnitsForCoordinates(target.getCoordinates());
        for (Unit unit : units) {
            if (unit.isAlliedTo(target.getOwner())) {
                if (unit.getMode() == STD_MODES.GUARDING) {
                    Ref ref= Ref.getCopy(action.getRef() );
                    ref.setTarget(target.getId());
                    if (!SneakRule.checkSneak(ref))
                        defenders.add(unit);
                }
            }
        }
        //add special defenders - adjacent or in line
        //TODO sort
        for (Unit unit : defenders) {
            if (action.isAttackAny()) {
                if (action.isRanged()) {
                    if (checkDefenderTakesMissile(action, unit))
                        return unit;
                    continue;
                }
                Attack attack = EffectFinder.getAttackFromAction(action);
                if (checkDefenderTakesAttack(attack, unit))
                    return unit;
            } else {
                if (checkDefenderTakesMissile(action, unit))
                    return unit;
            }
        }
        //what kind of animation would there be?
        return target;
    }
    private static boolean checkDefenderTakesAttack(Attack attack, Unit unit) {
        Ref ref=   Ref.getCopy(attack.getRef());
        ref.setTarget(unit.getId());
        String success="";
        String fail="";
        String log="";
//        boolean result = RollMaster.roll(ROLL_TYPES.REACTION, success, fail, ref, log);
        boolean result = RollMaster.roll(ROLL_TYPES.REACTION, ref);
        return result;
    }

    private static boolean checkDefenderTakesMissile(DC_ActiveObj action, Unit unit) {
        return false;
    }
}
