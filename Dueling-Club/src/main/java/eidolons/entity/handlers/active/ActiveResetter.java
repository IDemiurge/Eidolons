package eidolons.entity.handlers.active;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.DC_ValueManager;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.mechanics.TerrainRule;
import eidolons.game.battlecraft.rules.perk.FlyingRule;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TAGS;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.entity.ItemEnums.WEAPON_SIZE;
import main.content.enums.system.MetaEnums.CUSTOM_VALUE_TEMPLATE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.ability.construct.AbilityConstructor;
import main.entity.Ref.KEYS;
import main.entity.handlers.EntityMaster;
import main.entity.handlers.EntityResetter;
import main.entity.obj.Obj;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;

import static main.content.enums.system.MetaEnums.CUSTOM_VALUE_TEMPLATE.COST_MOD_ACTIVE_NAME;
import static main.content.enums.system.MetaEnums.CUSTOM_VALUE_TEMPLATE.COST_REDUCTION_ACTIVE_NAME;

/**
 * Created by JustMe on 2/23/2017.
 */
public class ActiveResetter extends EntityResetter<DC_ActiveObj> {


    public ActiveResetter(DC_ActiveObj entity, EntityMaster<DC_ActiveObj> entityMaster) {
        super(entity, entityMaster);
    }

    @Override
    public Executor getHandler() {
        return (Executor) super.getHandler();
    }

    @Override
    public void toBase() {
        if (ExplorationMaster.isExplorationOn()){
            if (!getEntity().getOwnerUnit().isMine())
                return;
        }
        super.toBase();
        if (getOwnerObj() == null) {
            return;
        }
        getHandler().getTargeter().resetTargetingCache();
        addCostMods();
        getHandler().getActivator().setCanActivate(null);

        initRange();

        if (!StringMaster.isEmpty(getProperty(G_PROPS.PASSIVES))) {
            try {
                AbilityConstructor.constructPassives(getEntity());
            } catch (Exception e) {
            }
        }
        // super.activatePassives(); now upon resolve()!!!


    }

    private Unit getOwnerObj() {
        return getEntity().getOwnerUnit();
    }


    protected void addCostMods() {
        if (getEntity().getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.MODE) {
            return;
        }
        if (getEntity().checkContainerProp(G_PROPS.ACTION_TAGS, ACTION_TAGS.FIXED_COST.toString())) {
            return;
        }

        // addCustomMods(); deprecated
        if (getEntity().getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.ITEM) {
            return;
        }
        applyPenalties();

    }


    protected void addCustomMods() {
        if (getOwnerObj().getCustomParamMap() == null) {
            return;
        }
        for (PARAMETER param : DC_ContentValsManager.getCostParams()) {
            addCustomMod(
             COST_REDUCTION_ACTIVE_NAME,
             getName(), param, false);
            addCustomMod(COST_MOD_ACTIVE_NAME,
             getName(), param, true);
        }
    }

    protected void addCustomMod(CUSTOM_VALUE_TEMPLATE template, String string, PARAMETER param,
                                boolean percent) {

        String value_ref = DC_ValueManager.getCustomValueName(template, param.getName(), string);
        int amount = getOwnerObj().getCounter(value_ref);
        if (amount == 0) {
            return;
        }
        if (percent) {
            getEntity().modifyParamByPercent(param, amount, true);
        } else {
            if (amount > 0) {
                getEntity().modifyParameter(param, amount);
            } else {
                getEntity().modifyParameter(param, amount, 1);
            }
        }
    }


    protected void applyPenalties() {
        if (getEntity().getActionGroup() == ACTION_TYPE_GROUPS.MODE) {
            return;
        }

        Unit ownerObj = getOwnerObj();
        //param revamp
        Integer sta = ownerObj.getIntParam(PARAMS.TOUGHNESS_PENALTY);
        Integer ap = ownerObj.getIntParam(PARAMS.AP_PENALTY);
        Integer ess = ownerObj.getIntParam(PARAMS.ESSENCE_PENALTY);
        Integer foc = ownerObj.getIntParam(PARAMS.FOCUS_PENALTY);
        Integer cp = ownerObj.getIntParam(PARAMS.CP_PENALTY);
        if (getHandler().isCounterMode()) {
            ap = MathMaster.applyModIfNotZero(ap, ownerObj.getIntParam(PARAMS.COUNTER_CP_PENALTY));
            sta = MathMaster.applyModIfNotZero(sta, ownerObj
             .getIntParam(PARAMS.COUNTER_TOUGH_PENALTY));
        }
        if (getHandler().isInstantMode()) {
            ap = MathMaster.applyModIfNotZero(ap, ownerObj.getIntParam(PARAMS.INSTANT_CP_PENALTY));
            sta = MathMaster.applyModIfNotZero(sta, ownerObj
             .getIntParam(PARAMS.INSTANT_TOUGH_PENALTY));
        }
        if (getHandler().isAttackOfOpportunityMode()) {
            ap = MathMaster.applyModIfNotZero(ap, ownerObj.getIntParam(PARAMS.AOO_CP_PENALTY));
            sta = MathMaster.applyModIfNotZero(sta, ownerObj
             .getIntParam(PARAMS.AOO_TOUGH_PENALTY));
        }
        switch (getEntity().getActionGroup()) {
            case ATTACK:
                // boolean offhand = checkSingleProp(G_PROPS.ACTION_TAGS,
                // StringMaster
                // .getWellFormattedString(ACTION_TAGS.OFF_HAND + ""));
                if (getEntity().isOffhand()) {
                    ap += ownerObj.getIntParam(PARAMS.OFFHAND_ATTACK_AP_PENALTY, false);
                    sta += ownerObj.getIntParam(PARAMS.OFFHAND_ATTACK_STA_PENALTY, false);
                } else {
                    ap += ownerObj.getIntParam(PARAMS.ATTACK_AP_PENALTY, false);
                    sta += ownerObj.getIntParam(PARAMS.ATTACK_STA_PENALTY, false);
                }
                if (getEntity().isThrow()) {
                    sta += 25 * (EnumMaster.getEnumConstIndex(WEAPON_SIZE.class, getOwnerObj()
                     .getWeapon(getEntity().isOffhand()).getWeaponSize()) - 1);
                    // TODO
                }
                break;
            case MOVE:
                FlyingRule.checkAddMoveCostReductions(ownerObj);
                TerrainRule.addMoveCost(getEntity());
                sta += ownerObj.getIntParam(PARAMS.MOVE_TOU_PENALTY, false);
                ap += ownerObj.getIntParam(PARAMS.MOVE_AP_PENALTY, false);
                break;
            case SPELL:
                ap += ownerObj.getIntParam(PARAMS.SPELL_AP_PENALTY, false);
                sta += ownerObj.getIntParam(PARAMS.SPELL_STA_PENALTY, false);
                ess += ownerObj.getIntParam(PARAMS.SPELL_ESS_PENALTY, false);
                foc += ownerObj.getIntParam(PARAMS.SPELL_FOC_PENALTY, false);

                break;
            case TURN:
                ap += ownerObj.getIntParam(PARAMS.MOVE_AP_PENALTY, false);
                break;
            case HIDDEN:
                ap += ownerObj.getIntParam(PARAMS.ATTACK_AP_PENALTY, false);
                sta += ownerObj.getIntParam(PARAMS.ATTACK_STA_PENALTY, false);
                break;

        }
        getEntity().modifyParamByPercent(PARAMS.CP_COST, cp, true);
        getEntity().modifyParamByPercent(PARAMS.TOU_COST, sta, true);
        getEntity().modifyParamByPercent(PARAMS.AP_COST, ap, true);
        getEntity().modifyParamByPercent(PARAMS.ESS_COST, ess, true);
        if (foc!=0)
        getEntity().modifyParamByPercent(PARAMS.FOC_COST, foc, true);
        if (foc!=0)
        getEntity().modifyParamByPercent(PARAMS.FOC_REQ, foc, false);
    }

    private void initRange() {
        if (!getEntity().isStandardAttack() && getEntity().getActionType() != ActionEnums.ACTION_TYPE.SPECIAL_ATTACK) {
            return;
        }
        Obj weapon = getRef().getObj(KEYS.RANGED);
        if (!getEntity().isRanged()) {
            weapon = null;
            // if (isOffhand()) TODO
            // weapon = ref.getObj(KEYS.OFFHAND);
            // else
            // weapon = ref.getObj(KEYS.WEAPON);
            // or getOrCreate from hero directly?
        } else {
            // Ref Not Empty(ranged,AMMO,);
        }
        if (weapon != null) {
            getEntity().modifyParameter(PARAMS.RANGE, weapon.getIntParam(PARAMS.RANGE));
        }
    }


}
