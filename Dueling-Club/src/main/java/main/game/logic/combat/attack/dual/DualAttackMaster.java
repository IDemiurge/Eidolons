package main.game.logic.combat.attack.dual;

import main.ability.Abilities;
import main.ability.Ability;
import main.ability.ActiveAbility;
import main.ability.DC_CostsFactory;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.common.ModifyValueEffect;
import main.ability.effects.oneshot.activation.ActivateEffect;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.elements.conditions.PropCondition;
import main.elements.costs.Cost;
import main.elements.costs.CostImpl;
import main.elements.costs.Costs;
import main.elements.costs.Payment;
import main.elements.targeting.AutoTargeting;
import main.entity.Ref;
import main.entity.active.DC_UnitAction;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.system.math.MathMaster;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 4/16/2017.
 */
public class DualAttackMaster {

    public static List<DC_UnitAction> getDualAttacks(Unit unit) {
        List<DC_UnitAction> list = new LinkedList<>();

        unit.getMainWeapon().getAttackActions().forEach(main -> {
            unit.getSecondWeapon().getAttackActions().forEach(offhand -> {
                if (checkRangeCanMerge(main, offhand))
                    if (checkCostsCanMerge(main, offhand))
                        if (checkTypeCanMerge(main, offhand))
                            list.add(createDual(main, offhand));
            });
        });

        return list;
    }

    private static boolean checkTypeCanMerge(DC_UnitAction main, DC_UnitAction offhand) {

        return true;
    }

    private static boolean checkRangeCanMerge(DC_UnitAction main, DC_UnitAction offhand) {
        if (main.isRanged())
            return false;
        return main.getIntParam(PARAMS.RANGE)==offhand.getIntParam(PARAMS.RANGE);
    }

    private static DC_UnitAction createDual(DC_UnitAction main, DC_UnitAction offhand) {
        Costs costs = getDualCosts(main, offhand);
//cooldown!
        ActiveAbility activateAttacks = new ActiveAbility(main.getTargeting(), new Effects(new ActivateEffect(main.getName(), true)
         , new ActivateEffect(offhand.getName(), true)));

        Ability setCooldown = new ActiveAbility(new AutoTargeting(new PropCondition(G_PROPS.ACTION_TAGS, "Dual", false)),
         new ModifyValueEffect(PARAMS.C_COOLDOWN,
         MOD.SET, getCooldown(main.getOwnerObj())));
        Abilities abilities = new Abilities();
        abilities.add(activateAttacks);
        abilities.add(setCooldown);
        ObjType newType = new ObjType(
         "Dual: " + main.getName() + " and " + offhand.getName(),
         DataManager.getType("Dual Attack", DC_TYPE.ACTIONS));
        for (Cost cost : costs.getCosts()) {
            PARAMETER p = cost.getCostParam();
            newType.setParam(p, cost.getPayment().getAmountFormula().toString());
        }
        DC_UnitAction dual = new DC_UnitAction(newType, main.getOwner(), main.getGame(), new Ref(main.getOwnerObj()));
        dual.setAbilities(abilities);
        dual.setCosts(costs);
        dual.setTargeting(main.getTargeting());
        dual.getTargeter().setTargetingInitialized(true);
        dual.setConstructed(true);
        return dual;
    }

    private static String getCooldown(Unit ownerObj) {
        return "1";
    }

    private static Costs getDualCosts(DC_UnitAction main, DC_UnitAction offhand) {
        List<Cost> list = new LinkedList<>();

        Costs costsMain = main.getCosts();
//        if (costsMain == null)
            costsMain = DC_CostsFactory.getCostsForAction(main);

        Costs costsOffhand = offhand.getCosts();
//        if (costsOffhand == null)
            costsOffhand = DC_CostsFactory.getCostsForAction(offhand);
        for (Cost cost : costsMain.getCosts()) {
            PARAMETER p = cost.getPayment().getParamToPay();
            Cost cost2 = costsOffhand.getCost(p);
            Integer value1 = cost.getPayment().getAmountFormula().getInt();
            Integer value2 =(cost2==null )? 0: cost2.getPayment().getAmountFormula().getInt();
            Boolean mode = getMinMaxOrAverage((PARAMS) p);
            Integer value =
             MathMaster.getTotalOrMinMax(mode, value1, value2);
            list.add(new CostImpl(new Payment(p, value)));
        }
        Costs costsDual = new Costs(list);
        Integer focReq = 25;
        list.add(new CostImpl(new Payment(PARAMS.FOC_REQ, focReq)));
        return costsDual;
    }

    private static boolean checkCostsCanMerge(DC_UnitAction main, DC_UnitAction offhand) {
        Costs costsMain = main.getCosts();
            costsMain = DC_CostsFactory.getCostsForAction(main);
        Costs costsOffhand = offhand.getCosts();
            costsOffhand = DC_CostsFactory.getCostsForAction(offhand);
        for (Cost cost : costsMain.getCosts()) {
            PARAMETER p = cost.getPayment().getParamToPay();
            Cost cost2 = costsOffhand.getCost(p);
            Integer value1 = cost.getPayment().getAmountFormula().getInt();
            Integer value2 =(cost2==null )? 0: cost2.getPayment().getAmountFormula().getInt();
            Boolean mode = getMinMaxOrAverage((PARAMS) p);
            if (!checkCost(value1, value2, mode))
                return false;
        }
        return true;
    }

    private static boolean checkCost(Integer value1, Integer value2, Boolean mode) {
//if (Math.abs(value1-value2)/)
        if (mode == null)
            return true;
        return MathMaster.getCentimalPercentage(
         Math.max(value1, value2), Math.min(value1, value2)) < 135;
    }

    private static Boolean getMinMaxOrAverage(PARAMS p) {
        switch (p) {
            case C_N_OF_ACTIONS:
                return false;
        }
        return null;
    }

}