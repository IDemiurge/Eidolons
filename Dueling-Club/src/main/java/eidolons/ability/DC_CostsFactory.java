package eidolons.ability;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.rules.round.FocusRule;
import eidolons.game.core.Eidolons;
import eidolons.system.DC_ConditionMaster;
import main.elements.conditions.NumericCondition;
import main.elements.conditions.Requirement;
import main.elements.costs.*;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.launch.CoreEngine;
import main.system.math.Formula;

import java.util.ArrayList;
import java.util.List;

public class DC_CostsFactory {

    public static Costs getCostsForAction(DC_ActiveObj type) {
        return getCostsForSpell(type, false);
    }

    public static Costs getCostsForSpell(DC_ActiveObj spell, boolean isSpell) {
        List<Cost> costs = new ArrayList<>();
        Cost cost = null;

        if (!DC_Engine.isAtbMode()) {
            cost = getCost(spell, PARAMS.AP_COST, PARAMS.C_N_OF_ACTIONS);
            if (cost != null) {
                costs.add(cost);
            }
        }

        cost = getCost(spell, PARAMS.ENERGY_COST, PARAMS.C_ENERGY);
        if (cost != null) {
            costs.add(cost);
        }

        // cost = getCost(spell, PARAMS.ATP_COST, PARAMS.C_N_OF_ATTACKS);
        // if (cost != null)
        // costs.add(cost);

        cost = getCost(spell, PARAMS.CP_COST, PARAMS.C_N_OF_COUNTERS);
        if (cost != null) {
            costs.add(cost);
        }

        // cost = getCost(spell, PARAMS.MP_COST, PARAMS.C_N_OF_MOVES);
        // if (cost != null)
        // costs.add(cost);

        cost = getCost(spell, PARAMS.ESS_COST, PARAMS.C_ESSENCE);
        if (cost != null) {
            costs.add(cost);
        }
        cost = getCost(spell, PARAMS.STA_COST, PARAMS.C_STAMINA);
        if (cost != null) {
            costs.add(cost);
        }
        if (FocusRule.isFatigueOn()) {
            cost = getCost(spell, PARAMS.FOC_COST, PARAMS.FOCUS_FATIGUE, true);
            if (cost != null) {
                costs.add(cost);
            }
        } else {
            cost = getCost(spell, PARAMS.FOC_COST, PARAMS.C_FOCUS);
            if (cost != null) {
                costs.add(cost);
            }
        }
        cost = getCost(spell, PARAMS.ENDURANCE_COST, PARAMS.C_ENDURANCE);
        if (cost != null) {
            costs.add(cost);
        }

        // if (spell.getIntParam(PARAMS.FOC_REQ) == 0)
        // return new Costs(costs);

        String s = ""
                + Math.max(spell.getIntParam(PARAMS.FOC_REQ),
                spell.getIntParam(PARAMS.FOC_COST));
        CostRequirements requirements = new CostRequirements();
        if (!StringMaster.isEmptyOrZero(s))
            requirements = new CostRequirements(new Payment(
                    PARAMS.C_FOCUS, new Formula(s)));

//        if (!DC_Engine.isAtbMode())
        if (!CoreEngine.isSafeMode()) // TODO igg hack
            requirements.add(new Requirement(
                    new NumericCondition("1", StringMaster.getValueRef(KEYS.ACTIVE,
                            PARAMS.C_COOLDOWN)),
                    InfoMaster.COOLDOWN_REASON));

        addSpecialRequirements(requirements, spell);

        return (isSpell) ? new DC_SpellCosts(requirements, costs)
                : new DC_ActionCosts(requirements, costs);
    }

    private static void addSpecialRequirements(CostRequirements requirements,
                                               DC_ActiveObj spell) {
        if (StringMaster.isEmpty(spell.getSpecialRequirements())) {
            return;
        }

        for (String subString : ContainerUtils.open(spell
                .getSpecialRequirements())) {
            Requirement req = DC_ConditionMaster
                    .getSpecialReq(subString, spell);
            if (req != null) {
                requirements.add(req);
            }

        }

    }

    public static Cost getCost(Entity obj, PARAMS cost_param, PARAMS pay_param) {
        return getCost(obj, cost_param, pay_param, false);
    }

    public static Cost getCost(Entity obj, PARAMS cost_param, PARAMS pay_param, boolean add) {

        String paramValue = obj.getParam(cost_param);
        Formula formula;

        boolean var;
        int amount = NumberUtils.getInteger(paramValue);
        if (amount == 0) {
            return null;
        }
        if (add) {
            amount = -amount;
        }
        formula = new Formula(amount + "");
        var = false;

        Cost cost = new CostImpl(new Payment(pay_param, formula) {
            @Override
            public boolean pay(Obj payee, Ref ref) {
                if (add) {
                    if (payee == Eidolons.getMainHero()) {
                        payee.getGame().getLogManager().log(payee + "'s " +
                                pay_param.getName() + " is now " +
                                payee.getIntParam(PARAMS.FOCUS_FATIGUE));
                    }
                }
                return super.pay(payee, ref);
            }
        }, cost_param);
        cost.setVariable(var);
        return cost;
    }

    // public final PARAMETER[] STANDARD_SPELL_COSTS_TEMPLATE = {
    // DC_PARAM.C_ESSENCE,
    //
    // };
}
