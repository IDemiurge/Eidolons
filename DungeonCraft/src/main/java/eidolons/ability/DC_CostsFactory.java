package eidolons.ability;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.system.DC_ConditionMaster;
import main.elements.conditions.Requirement;
import main.elements.costs.*;
import main.entity.Entity;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.math.Formula;

import java.util.ArrayList;
import java.util.List;

public class DC_CostsFactory {

    public static Costs getCostsForAction(DC_ActiveObj type) {
        return getCostsForSpell(type, false);
    }

    public static Costs getCostsForSpell(DC_ActiveObj spell, boolean isSpell) {
        List<Cost> costs = new ArrayList<>();
        Cost cost;

        cost = getCost(spell, PARAMS.ENERGY_COST, PARAMS.C_ENERGY);
        if (cost != null) {
            costs.add(cost);
        }

        // cost = getCost(spell, PARAMS.ATP_COST, PARAMS.C_N_OF_ATTACKS);
        // if (cost != null)
        // costs.add(cost);

        cost = getCost(spell, PARAMS.ATK_PTS_COST, PARAMS.C_EXTRA_ATTACKS);
        if (cost != null) {
            costs.add(cost);
        }
        cost = getCost(spell, PARAMS.MOVE_PTS_COST, PARAMS.C_EXTRA_MOVES);
        if (cost != null) {
            costs.add(cost);
        }

        cost = getCost(spell, PARAMS.SF_COST, PARAMS.C_SOULFORCE);
        if (cost != null) {
            costs.add(cost);
        }
        cost = getCost(spell, PARAMS.ESS_COST, PARAMS.C_ESSENCE);
        if (cost != null) {
            costs.add(cost);
        }
        cost = getCost(spell, PARAMS.TOU_COST, PARAMS.C_TOUGHNESS);
        if (cost != null) {
            costs.add(cost);
        }
        cost = getCost(spell, PARAMS.FOC_COST, PARAMS.C_FOCUS);
        if (cost != null) {
            costs.add(cost);
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

    public static Cost getCost(Entity active, PARAMS cost_param, PARAMS pay_param) {
        return getCost(active, cost_param, pay_param, false);
    }

    public static Cost getCost(int amount, PARAMS cost_param, PARAMS pay_param, boolean add) {
        Formula formula;
        boolean var;
        if (add) {
            amount = -amount;
        }
        formula = new Formula(amount + "");
        var = false;

        Cost cost = new CostImpl(new Payment(pay_param, formula), cost_param);
        cost.setVariable(var);
        return cost;
    }

    public static Cost getCost(Entity active, PARAMS cost_param, PARAMS pay_param, boolean add) {
        int amount = active.getIntParam(cost_param);
        if (amount == 0) {
            return null;
        }
        return getCost(amount, cost_param, pay_param, add);
    }

    // public final PARAMETER[] STANDARD_SPELL_COSTS_TEMPLATE = {
    // DC_PARAM.C_ESSENCE,
    //
    // };
}
