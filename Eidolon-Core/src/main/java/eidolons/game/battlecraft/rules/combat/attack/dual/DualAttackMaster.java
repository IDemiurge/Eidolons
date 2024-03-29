package eidolons.game.battlecraft.rules.combat.attack.dual;

import eidolons.ability.costs.DC_CostsFactory;
import eidolons.ability.effects.oneshot.activation.ActivateEffect;
import eidolons.content.PARAMS;
import eidolons.entity.feat.active.UnitAction;
import eidolons.entity.unit.Unit;
import main.ability.Abilities;
import main.ability.ActiveAbility;
import main.ability.effects.Effects;
import main.content.DC_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.elements.costs.Cost;
import main.elements.costs.CostImpl;
import main.elements.costs.Costs;
import main.elements.costs.Payment;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.math.MathMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 4/16/2017.
 */
@Deprecated
public class DualAttackMaster {
    public static List<UnitAction> getDualAttacks(Unit unit) {
        List<UnitAction> list = new ArrayList<>();
        // DC Review
        // unit.getMainWeapon().getAttackActions().forEach(main -> {
        //     unit.getOffhandWeapon().getAttackActions().forEach(offhand -> {
        //         if (checkRangeCanMerge(main, offhand))
        //             if (checkCostsCanMerge(main, offhand))
        //                 if (checkTypeCanMerge(main, offhand))
        //                     list.add(createDual(main, offhand));
        //     });
        // });
        return list;
    }

    private static boolean checkTypeCanMerge(UnitAction main, UnitAction offhand) {
        // good as counters?
        if (offhand.checkProperty(G_PROPS.ACTION_TAGS, ActionEnums.ACTION_TAGS.TWO_HANDS.toString())) {
            return false;
        }
        return !main.checkProperty(G_PROPS.ACTION_TAGS, ActionEnums.ACTION_TAGS.TWO_HANDS.toString());
    }

    private static boolean checkRangeCanMerge(UnitAction main, UnitAction offhand) {
        if (main.isRanged())
            return false;
        return main.getIntParam(PARAMS.RANGE) == offhand.getIntParam(PARAMS.RANGE);
    }

    //TODO core revamp - VIA EXTRA ATK only!
    private static UnitAction createDual(UnitAction main, UnitAction offhand) {
        Costs costs = getDualCosts(main, offhand);
        ActiveAbility activateAttacks = new ActiveAbility(main.getTargeting(), new Effects(new ActivateEffect(main.getName(), true)
                , new ActivateEffect(offhand.getName(), true)));

        Abilities abilities = new Abilities();
        abilities.add(activateAttacks);
        ObjType newType = new ObjType(
                "Dual " + main.getName() + "&" + offhand.getName().replace("Off hand", ""),
                DataManager.getType("Dual Attack", DC_TYPE.ACTIONS));
        for (Cost cost : costs.getCosts()) {
            PARAMETER p = cost.getCostParam();
            if (p == null)
                continue;
            newType.setParam(p, cost.getPayment().getAmountFormula().toString());
        }
        UnitAction dual = new UnitAction(newType, main.getOwner(), main.getGame(), new Ref(main.getOwnerUnit()));
        dual.setAbilities(abilities);
        dual.setCosts(costs);
        dual.setTargeting(main.getTargeting());
        dual.getTargeter().setTargetingInitialized(true);
        dual.setConstructed(true);
        return dual;
    }

    private static Costs getDualCosts(UnitAction main, UnitAction offhand) {
        List<Cost> list = new ArrayList<>();

        Costs costsMain;
        //        if (costsMain == null)
        costsMain = DC_CostsFactory.getCostsForAction(main);

        Costs costsOffhand;
        //        if (costsOffhand == null)
        costsOffhand = DC_CostsFactory.getCostsForAction(offhand);
        for (Cost cost : costsMain.getCosts()) {
            PARAMETER p = cost.getPayment().getParamToPay();
            Cost cost2 = costsOffhand.getCost(p);
            Integer value1 = cost.getPayment().getAmountFormula().getInt();
            Integer value2 = (cost2 == null) ? 0 : cost2.getPayment().getAmountFormula().getInt();
            Boolean mode = getMinMaxOrAverage((PARAMS) p);
            Integer value =
                    MathMaster.getTotalOrMinMax(mode, value1, value2);
            list.add(new CostImpl(new Payment(p, value), cost.getCostParam()));
        }
        Costs costsDual = new Costs(list);
        Integer focReq = 25;
        list.add(new CostImpl(new Payment(PARAMS.FOC_REQ, focReq)));
        return costsDual;
    }

    private static boolean checkCostsCanMerge(UnitAction main, UnitAction offhand) {
        Costs costsMain;
        costsMain = DC_CostsFactory.getCostsForAction(main);
        Costs costsOffhand;
        costsOffhand = DC_CostsFactory.getCostsForAction(offhand);
        for (Cost cost : costsMain.getCosts()) {
            PARAMETER p = cost.getPayment().getParamToPay();
            Cost cost2 = costsOffhand.getCost(p);
            Integer value1 = cost.getPayment().getAmountFormula().getInt();
            Integer value2 = (cost2 == null) ? 0 : cost2.getPayment().getAmountFormula().getInt();
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
        return null;
    }

}
