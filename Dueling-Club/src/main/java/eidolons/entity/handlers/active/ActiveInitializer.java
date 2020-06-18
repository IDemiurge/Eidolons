package eidolons.entity.handlers.active;

import eidolons.ability.DC_CostsFactory;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActionManager;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.target.EffectFinder;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.core.atb.AtbMaster;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.values.properties.G_PROPS;
import main.elements.costs.Cost;
import main.elements.costs.CostImpl;
import main.elements.costs.Costs;
import main.elements.costs.Payment;
import main.entity.handlers.EntityInitializer;
import main.entity.handlers.EntityMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.math.Formula;

import java.util.ArrayList;

/**
 * Created by JustMe on 2/23/2017.
 */
public class ActiveInitializer extends EntityInitializer<DC_ActiveObj> {


    public ActiveInitializer(DC_ActiveObj entity, EntityMaster<DC_ActiveObj> entityMaster) {
        super(entity, entityMaster);
    }

    @Override
    public ActiveMaster getMaster() {
        return (ActiveMaster) super.getMaster();
    }

    public void construct() {
        if (!getEntity().isConstructed()) {
            try {
                super.construct();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        getMaster().getHandler().getTargeter().initTargetingMode();
        if (!getEntity().getActives().isEmpty()) {
            return;
        }
        if (EffectFinder.getEffectsFromAbilities(getEntity().getAbilities()).size() == 0) {
            if (getEntity() instanceof DC_UnitAction) {
                if (((DC_UnitAction) getEntity()).isDummy()) {
                    return;
                }
            }
            if (getEntity().isAttackAny()) {
                main.system.auxiliary.log.LogMaster.important(">>> ATTACK CONSTRUCT FAILeD: " + getEntity());
            } else if (!getEntity().getName().equalsIgnoreCase("wait"))
                if (!getEntity().getName().equalsIgnoreCase("idle"))
                if (!getEntity().getName().equalsIgnoreCase("Dummy Action"))
                {
                    main.system.auxiliary.log.LogMaster.important(">>> NO EFFECTS AFTER CONSTRUCT: " + getEntity());
                }
        }
    }

    public void initCosts() {
        initCosts(false);
    }

    public void initCosts(boolean anim) {
        Costs costs = null;
        if (getEntity().isFree()) {
            costs = new Costs(new ArrayList<>());
        } else {
            try {
                costs = DC_CostsFactory.getCostsForSpell(getEntity(),
                        getChecker().isSpell());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

            Cost cp_cost = costs.getCost(PARAMS.C_N_OF_COUNTERS);
            Formula ap_cost =   new Formula(getParam(PARAMS.AP_COST)) ;
            boolean noCounterCost = cp_cost == null;
            if (!noCounterCost) {
                noCounterCost = cp_cost.getPayment().getAmountFormula().toString().isEmpty()
                        || cp_cost.getPayment().getAmountFormula().toString().equals("0");
            }
            if (noCounterCost) { // if not specifically set...
                if (getHandler().isExtraAttackMode()) {
                    cp_cost = new CostImpl(new Payment(PARAMS.C_N_OF_COUNTERS,
                            (ap_cost)));
                    cp_cost.getPayment().getAmountFormula().applyModifier(
                            getEntity().getOwnerUnit().getIntParam(PARAMS.EXTRA_ATTACKS_POINT_COST_MOD));
                    cp_cost.setCostParam(PARAMS.CP_COST);

                }
            } else {
                if (!(getHandler().isExtraAttackMode())) {
                    costs.getCosts().remove(cp_cost);
                }
            }

            costs.addCost(
                    new CostImpl(new Payment(null, (int) AtbMaster.getReadinessCost(getEntity()))
                            , PARAMS.ATB
                    ));
            if (!getHandler().isExtraAttackMode())
                costs.removeCost(PARAMS.C_N_OF_COUNTERS);
            //TODO EA check
        }
        if (anim) {
//          getAnimator().  addCostAnim();

        }
        costs.setActive(getEntity());
//   TODO what for?
//     if (!getHandler().isInstantMode() || getHandler().isCounterMode()) {
//            getHandler().getActivator().setCanActivate(costs.canBePaid(getRef()));
//        }

        costs.setActiveId(getId());

        if (getEntity().isAttackAny())
            if (getEntity().isExtraAttackMode())
                applyDynamicCostMods(costs);
        getEntity().setCosts(costs);
    }


    public void applyDynamicCostMods(Costs costs) {
        Unit ownerObj = getEntity().getOwnerUnit();
        Integer sta = 0;
        Integer ap = 0;
        if (getHandler().isCounterMode()) {
            ap = ownerObj.getIntParam(PARAMS.COUNTER_CP_PENALTY);
            sta = ownerObj
                    .getIntParam(PARAMS.COUNTER_STAMINA_PENALTY);
        }
        if (getHandler().isInstantMode()) {
            ap = ownerObj.getIntParam(PARAMS.INSTANT_CP_PENALTY);
            sta = ownerObj
                    .getIntParam(PARAMS.INSTANT_STAMINA_PENALTY);
        }
        if (getHandler().isAttackOfOpportunityMode()) {
            ap = ownerObj.getIntParam(PARAMS.AOO_CP_PENALTY);
            sta = ownerObj
                    .getIntParam(PARAMS.AOO_STAMINA_PENALTY);
        }
        if (ap != 0)
            if (costs.getCost(PARAMS.AP_COST) != null)
                costs.getCost(PARAMS.AP_COST).getPayment().getAmountFormula().applyModifier(ap);
        if (sta != 0)
            if (costs.getCost(PARAMS.STA_COST) != null) {
                costs.getCost(PARAMS.STA_COST).getPayment().getAmountFormula().applyModifier(sta);
            }
    }


    public ACTION_TYPE_GROUPS initActionTypeGroup() {
        if (checkProperty(G_PROPS.ACTION_TYPE_GROUP)) {
            return new EnumMaster<ACTION_TYPE_GROUPS>()
                    .retrieveEnumConst(ACTION_TYPE_GROUPS.class, getProperty(G_PROPS.ACTION_TYPE_GROUP));
        }
        if (getChecker().isStandardAttack()) {
            return ActionEnums.ACTION_TYPE_GROUPS.ATTACK;
        }
        if (StringMaster.isEmpty(getProperty(G_PROPS.ACTION_TYPE))) {
            return ActionEnums.ACTION_TYPE_GROUPS.SPELL;
        }
        ACTION_TYPE type =
                getEntity().getActionType();
//         new EnumMaster<ACTION_TYPE>().retrieveEnumConst(ACTION_TYPE.class,
//         getProperty(G_PROPS.ACTION_TYPE));
        if (type == null) {
            return ActionEnums.ACTION_TYPE_GROUPS.SPELL;
        }
        switch (type) {
            case HIDDEN:
                return ActionEnums.ACTION_TYPE_GROUPS.HIDDEN;
            case MODE:
                return ActionEnums.ACTION_TYPE_GROUPS.MODE;
            case SPECIAL_ACTION:
                return ActionEnums.ACTION_TYPE_GROUPS.SPECIAL;
            case SPECIAL_ATTACK:
                return ActionEnums.ACTION_TYPE_GROUPS.ATTACK;
            case ADDITIONAL_MOVE:
            case SPECIAL_MOVE:
                return ActionEnums.ACTION_TYPE_GROUPS.MOVE;
            case STANDARD:
                return DC_ActionManager.getStdObjType(getEntity());

        }
        return ActionEnums.ACTION_TYPE_GROUPS.SPELL;
    }

    @Override
    protected void initDefaults() {

    }

    @Override
    public void init() {
        super.init();
        addDynamicValues();
    }

    public void addDynamicValues() {
        if (RuleKeeper.isCooldownOn())
            return;
        Integer cooldown = getIntParam(PARAMS.COOLDOWN);
        if (cooldown < 0) {
            getEntity().setParam(PARAMS.C_COOLDOWN, cooldown);
        } else {
            getEntity().setParam(PARAMS.C_COOLDOWN, 0);
        }
        // TODO adjust costs based on hero's skills

    }

    @Override
    public Executor getHandler() {
        return (Executor) super.getHandler();
    }

    @Override
    public ActiveChecker getChecker() {
        return (ActiveChecker) super.getChecker();
    }

}
