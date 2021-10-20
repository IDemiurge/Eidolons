package eidolons.entity.handlers.active;

import eidolons.ability.costs.DC_CostsFactory;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActionManager;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.atb.AtbMaster;
import eidolons.game.core.master.EffectMaster;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
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
public class ActiveObjInitializer extends EntityInitializer<DC_ActiveObj> {


    public ActiveObjInitializer(DC_ActiveObj entity, EntityMaster<DC_ActiveObj> entityMaster) {
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
        if (EffectMaster.getEffectsFromAbilities(getEntity().getAbilities()).size() == 0) {
            if (getEntity().isAttackAny()) {
                main.system.auxiliary.log.LogMaster.important(">>> ATTACK CONSTRUCT FAILeD: " + getEntity());
            } else if (!getEntity().getName().equalsIgnoreCase("wait"))
                if (!getEntity().getName().equalsIgnoreCase("idle"))
                    if (!getEntity().getName().equalsIgnoreCase("Dummy Action")) {
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

            costs.addCost(new CostImpl(new Payment(null, (int) AtbMaster.getReadinessCost(getEntity()))
                    , PARAMS.ATB));
        }

        costs.setActive(getEntity());
        costs.setActiveId(getId());

        //TODO Review
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
            ap = ownerObj.getIntParam(PARAMS.COUNTER_PTS_COST_MOD);
            sta = ownerObj
                    .getIntParam(PARAMS.COUNTER_TOUGHNESS_COST_MOD);
        }
        if (getHandler().isInstantMode()) {
            ap = ownerObj.getIntParam(PARAMS.INSTANT_PTS_COST_MOD);
            sta = ownerObj
                    .getIntParam(PARAMS.INSTANT_TOUGHNESS_COST_MOD);
        }
        if (getHandler().isAttackOfOpportunityMode()) {
            ap = ownerObj.getIntParam(PARAMS.AOO_PTS_COST_MOD);
            sta = ownerObj
                    .getIntParam(PARAMS.AOO_TOUGHNESS_COST_MOD);
        }
        if (ap != 0)
            if (costs.getCost(PARAMS.AP_COST) != null)
                costs.getCost(PARAMS.AP_COST).getPayment().getAmountFormula().applyModifier(ap);
        if (sta != 0)
            if (costs.getCost(PARAMS.TOU_COST) != null) {
                costs.getCost(PARAMS.TOU_COST).getPayment().getAmountFormula().applyModifier(sta);
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
