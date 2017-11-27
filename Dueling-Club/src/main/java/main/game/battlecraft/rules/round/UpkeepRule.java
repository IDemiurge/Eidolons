package main.game.battlecraft.rules.round;

import main.ability.PassiveAbilityObj;
import main.ability.effects.Effect;
import main.ability.effects.Effect.UPKEEP_FAIL_ACTION;
import main.ability.effects.attachment.AddBuffEffect;
import main.ability.effects.common.OwnershipChangeEffect;
import main.ability.effects.continuous.BehaviorModeEffect;
import main.ability.effects.oneshot.InstantDeathEffect;
import main.ability.effects.oneshot.buff.RemoveBuffEffect;
import main.ability.effects.oneshot.status.ImmobilizeEffect;
import main.content.*;
import main.content.enums.system.AiEnums;
import main.content.values.parameters.PARAMETER;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.BuffObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.math.Formula;

import java.util.ArrayList;
import java.util.List;

public class UpkeepRule extends RoundRule {

    private static final String BUFF_DISPELLED_STRING = " is dispelled due to upkeep fail!";

    public UpkeepRule(DC_Game game) {
        super(game);
    }

    public static void addUpkeep(Obj payObj) {
        Obj spell = payObj.getRef().getObj(KEYS.ACTIVE);
        if (spell == null) {
            return;
        }
        Obj abil = payObj.getRef().getObj(KEYS.ABILITY);
        if (abil != null) {
            if (abil instanceof PassiveAbilityObj) {
                return;
            }
        }
        String property = spell.getProperty(PROPS.UPKEEP_FAIL_ACTION);
        if (new EnumMaster<UPKEEP_FAIL_ACTION>().retrieveEnumConst(
         UPKEEP_FAIL_ACTION.class, property) == null) {
            property = UPKEEP_FAIL_ACTION.DEATH + "";
        }
        payObj.setProperty(PROPS.UPKEEP_FAIL_ACTION, property);
        for (PARAMETER p : ValuePages.UPKEEP_PARAMETERS) {
            Integer param = spell.getIntParam(p);
            if (param > 0) {
                payObj.getType().setParam(p, param);
            }
        }

    }

    @Override
    public boolean check(Unit unit) {
        return true;
    }

    @Override
    public void apply(Unit unit) {
        // TODO getOrCreate all buffs/units with this SOURCE /summoner
        List<Obj> payObjects = new ArrayList<>();
        List<Obj> destroyObjects = new ArrayList<>();
        boolean destroy = unit.isDead() || unit.isUnconscious();
        for (Unit u : game.getUnits()) {
            if (destroy || u.isDead() || u.isUnconscious()) {
                destroyObjects.add(u);
            } else if (u.getRef().getObj(KEYS.SUMMONER) == unit) {
                if (checkHasUpkeep(u)) {
                    payObjects.add(u);
                }
            }
        }

        for (Obj buff : game.getObjects(DC_TYPE.BUFFS)) {
            try {
                Obj spell = (Obj) buff.getRef().getActive();
                if (spell == null) {
                    continue;
                }
                if (spell.getRef().getSourceObj() == unit) {// TODO summoner?
                    if (checkHasUpkeep(buff)) {
                        payObjects.add(buff);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        for (Obj payObj : destroyObjects) {
            enactUpkeepFail(getFailAction(payObj),
             Ref.getSelfTargetingRefCopy(payObj));
        }
            for (Obj payObj : payObjects) {
            if (!checkCanUpkeep(unit, payObj)) { // positive upkeep?
                enactUpkeepFail(getFailAction(payObj),
                 Ref.getSelfTargetingRefCopy(payObj));
            } else {
                subtractUpkeep(unit, payObj);
            }
        }
    }

    private boolean checkHasUpkeep(Obj obj) {
        for (PARAMETER p : ValuePages.UPKEEP_PARAMETERS) {
            if (obj.getIntParam(p) > 0) {
                return true;
            }
        }
        return false;
    }

    private UPKEEP_FAIL_ACTION getFailAction(Obj payObj) {
        return new EnumMaster<UPKEEP_FAIL_ACTION>().retrieveEnumConst(
         UPKEEP_FAIL_ACTION.class,
         payObj.getProperty(PROPS.UPKEEP_FAIL_ACTION));
    }

    private boolean checkCanUpkeep(Unit unit, Obj payObj) {
        if (unit.isDead())
        // if (payObj.checkBool(DISPEL_ON_DEATH)) //only those with upkeep,
        // never fear!
        {
            return false;
        }
        for (PARAMETER p : ValuePages.UPKEEP_PARAMETERS) {
            PARAMETER payParamFromUpkeep = DC_ContentManager
             .getPayParamFromUpkeep(p);
            Integer amount = new Formula(payObj.getParam(p))
             .getAppendedByModifier(
              StringMaster.getValueRef(KEYS.SUMMONER,
               PARAMS.UPKEEP_MOD)).getInt(unit.getRef());
            if (amount <= 0) {
                continue;
            }
            String param = amount + "";
            if (!unit.checkParam(payParamFromUpkeep, param)) {
                return false;
            }
        }
        return true;
    }

    public void subtractUpkeep(Unit unit, Obj payObj) {
        for (PARAMETER p : ValuePages.UPKEEP_PARAMETERS) {
            PARAMETER payParam = DC_ContentManager.getPayParamFromUpkeep(p);
            int amount = new Formula(payObj.getParam(p)).getAppendedByModifier(
             StringMaster.getValueRef(KEYS.SOURCE, PARAMS.UPKEEP_MOD))
             .getInt(unit.getRef());
            if (amount <= 0) {
                return;
            }
            unit.modifyParameter(payParam, -amount);

            unit.getGame()
             .getLogManager()
             .log(payObj.getName()
              + "'s "
              + ContentManager.getBaseParameterFromCurrent(p)
              .getName() + " upkeep paid: " + amount);

        }
    }

    private Effect getFailEffects(UPKEEP_FAIL_ACTION ufa) {
        switch (ufa) {
            case BERSERK:
                return new AddBuffEffect(new BehaviorModeEffect(
                 AiEnums.BEHAVIOR_MODE.BERSERK));
            case CONFUSION:
                return new AddBuffEffect(new BehaviorModeEffect(
                 AiEnums.BEHAVIOR_MODE.CONFUSED));
            case DEATH:
                return new InstantDeathEffect(false, false);
            case STASIS:
                return new ImmobilizeEffect();
            case TREASON:
                return new OwnershipChangeEffect(true);
            default:
                break;

        }
        return null;
    }

    public void enactUpkeepFail(UPKEEP_FAIL_ACTION ufa, Ref ref) {

        Obj targetObj = ref.getTargetObj();
        if (targetObj instanceof BuffObj) {
            BuffObj buffObj = (BuffObj) targetObj;
            ref.setTarget(buffObj.getBasis().getId());
            new RemoveBuffEffect(targetObj.getName()).apply(ref);
            ref.getGame().getLogManager()
             .log(targetObj.getName() + " " + BUFF_DISPELLED_STRING);
            return;
        }
        ref.getGame().getLogManager()
         .log(targetObj.getName() + " " + ufa.getLogString());
        // TODO enemy string should be different!
        Effect e = getFailEffects(ufa);
        e.apply(ref);
    }

}
