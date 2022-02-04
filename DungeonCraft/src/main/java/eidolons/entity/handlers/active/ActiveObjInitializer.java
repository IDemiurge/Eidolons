package eidolons.entity.handlers.active;

import eidolons.ability.costs.DC_CostsFactory;
import eidolons.content.PARAMS;
import eidolons.entity.mngr.action.ActionHelper;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.game.core.atb.AtbMaster;
import eidolons.game.core.master.EffectMaster;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.values.properties.G_PROPS;
import main.elements.costs.CostImpl;
import main.elements.costs.Costs;
import main.elements.costs.Payment;
import main.entity.handlers.EntityInitializer;
import main.entity.handlers.EntityMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;

/**
 * Created by JustMe on 2/23/2017.
 */
public class ActiveObjInitializer extends EntityInitializer<ActiveObj> {


    public ActiveObjInitializer(ActiveObj entity, EntityMaster<ActiveObj> entityMaster) {
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

        getEntity().setCosts(costs);
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
                return ActionHelper.getStdObjType(getEntity());

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
