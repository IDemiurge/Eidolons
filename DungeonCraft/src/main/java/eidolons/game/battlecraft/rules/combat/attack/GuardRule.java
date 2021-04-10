package eidolons.game.battlecraft.rules.combat.attack;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.master.EffectMaster;
import eidolons.system.math.roll.RollMaster;
import main.content.enums.GenericEnums.RollType;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.mode.STD_MODES;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by JustMe on 8/10/2017.
 */
public class GuardRule {
    public static final boolean on = false;

    public static BattleFieldObject checkTargetChanged(DC_ActiveObj action) {

        BattleFieldObject target = (BattleFieldObject) action.getTargetObj();
//         attack.getAttackedUnit();
        List<Unit> guards = new ArrayList<>();
        Collection<Unit> units = target.getGame().getUnitsForCoordinates(target.getCoordinates());
        for (Unit unit : units) {
            if (unit.isAlliedTo(target.getOwner())) {
                if (unit.getMode() == STD_MODES.GUARDING
                 || unit.checkStatus(STATUS.GUARDING)) {
                    Ref ref = Ref.getCopy(action.getRef());
                    ref.setTarget(target.getId());
                    if (!SneakRule.checkSneak(ref))
                        guards.add(unit);
                }
            }
        }
        //add special defenders - adjacent or in line
        //TODO sort
        for (Unit guard : guards) {
            if (guard == target) continue;
            if (action.isAttackAny()) {
                if (action.isRanged()) {
                    if (checkDefenderTakesMissile(action, guard))
                        return guard;
                    continue;
                }
                Attack attack = EffectMaster.getAttackFromAction(action);
                if (checkDefenderTakesAttack(attack, guard))
                    return guard;
            } else {
                if (checkDefenderTakesMissile(action, guard))
                    return guard;
            }
        }
        //what kind of animation would there be?
        return null;
    }

    private static boolean checkDefenderTakesAttack(Attack attack, Unit guard) {
        Ref ref = Ref.getCopy(attack.getRef());
        ref.setTarget(guard.getId());
        String success = StringMaster.getValueRef(KEYS.TARGET, PARAMS.INITIATIVE )
         + "*(100+" + RollMaster.getVigilanceModifier(guard, attack.getAction()) +
         ")/100/3 ";
        String fail = StringMaster.getValueRef(KEYS.SOURCE, PARAMS.INITIATIVE) +
         "*(100+" + RollMaster.getDexterousModifier(guard, attack.getAction()) +
         ")/100/" + attack.getAction().getIntParam(PARAMS.AP_COST);
        String log = " to defend " + attack.getAttackedUnit().getName() +
         " against " + attack.getAction().getName();
        //        boolean result = RollMaster.roll(ROLL_TYPES.REACTION, ref);

        //TODO RPG Review
        // return !RollMaster.roll(RollType.REACTION, success, fail, ref, log);
        return false;
    }

    private static boolean checkDefenderTakesMissile(DC_ActiveObj action, Unit unit) {
        return false;
    }
}
