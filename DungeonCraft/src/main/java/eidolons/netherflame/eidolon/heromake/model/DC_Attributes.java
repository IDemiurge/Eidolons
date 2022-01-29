package eidolons.netherflame.eidolon.heromake.model;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.DC_ContentValsManager.ATTRIBUTE;
import eidolons.content.PARAMS;
import eidolons.entity.unit.Unit;
import main.content.values.parameters.PARAMETER;
import main.system.auxiliary.StringMaster;

import static eidolons.content.values.DC_ValueManager.VALUE_GROUP.*;

public class DC_Attributes  {
    private final Unit hero;

    public DC_Attributes(Unit hero) {
        this.hero = hero;
    }

    private int getAttrValue(ATTRIBUTE attr) {
        return hero.getIntParam(attr.getParameter());
    }

    public void apply() {
        for (ATTRIBUTE attr : DC_ContentValsManager.getAttributeEnums()) {
            applyAttr(attr);
        }
    }

    private void mod(int amount, float modifier, ATTRIBUTE attr, PARAMETER... params) {
        float result = (modifier * amount);
        mod(result, attr, params);
    }
    private void mod(float amount,  ATTRIBUTE attr, PARAMETER... params) {
        String modifierKey = StringMaster.format(attr.toString());
        for (PARAMETER param : params) {
            hero.modifyParameter(param, amount, modifierKey);
        }
    }

    private void applyAttr(ATTRIBUTE attr) {
        int amount = getAttrValue(attr);
        int before25 = Math.min(25, amount);
        switch (attr) {
            case STRENGTH:
                // + damage modifier
                mod(amount, AttributeConsts.STR_TOU_BONUS, attr, PARAMS.TOUGHNESS);
                mod(amount, AttributeConsts.STR_END_BONUS, attr, PARAMS.ENDURANCE);
                mod(amount, AttributeConsts.STR_MIGHT_BONUS, attr, PARAMS.MIGHT);
                mod(before25, AttributeConsts.B25_STR_CARRY_CAPACITY, attr, PARAMS.CARRYING_CAPACITY);
                mod(amount - 25, AttributeConsts.A25_STR_PENALTY_REDUCTION, attr, PARAMS.WEIGHT_PENALTY_REDUCTION);

                break;
            case VITALITY:
                mod(amount, AttributeConsts.VIT_TOU_BONUS, attr, PARAMS.TOUGHNESS);
                mod(amount, AttributeConsts.VIT_END_BONUS, attr, PARAMS.ENDURANCE);
                mod(amount, AttributeConsts.VIT_MIGHT_BONUS, attr, PARAMS.MIGHT);
                mod(before25, AttributeConsts.B25_VIT_ELEMENTAL_RESIST, attr, ELEMENTAL_RESISTANCES.getParams());
                mod(amount - 25, AttributeConsts.A25_VIT_END_REGEN, attr, PARAMS.ENDURANCE_REGEN);

            case AGILITY:
                // + damage modifier
                mod(amount, AttributeConsts.AGI_ATK_BONUS, attr, PARAMS.ATTACK);
                //TODO NF Rules revamp
                // mod(DC_Formulas.getCountersFromAgi(amount), attr, PARAMS.EXTRA_ATTACKS); adrenaline gain?
                mod(amount, AttributeConsts.AGI_REFLEX_BONUS, attr, PARAMS.REFLEX);
                mod(before25, AttributeConsts.B25_AGI_ARMOR_PENETRATION, attr, PARAMS.ARMOR_PENETRATION);
                mod(amount - 25, AttributeConsts.A25_AGI_INITIATIVE , attr, PARAMS.INITIATIVE);
                break;

            case DEXTERITY:
                //TODO NF Rules revamp
                // mod(DC_Formulas.getExtraMovesFromDex(amount), attr, PARAMS.EXTRA_MOVES); energy?
                mod(amount, AttributeConsts.DEX_DEF_BONUS, attr, PARAMS.DEFENSE);
                mod(amount, AttributeConsts.DEX_REFLEX_BONUS, attr, PARAMS.REFLEX);
                mod(before25, AttributeConsts.B25_DEX_INITIATIVE , attr, PARAMS.INITIATIVE);
                mod(amount - 25, AttributeConsts.A25_DEX_STEALTH, attr, PARAMS.STEALTH);
                break;
            case WILLPOWER:
                mod(amount, AttributeConsts.WIL_RES_BONUS, attr, ASTRAL_AND_ELEMENTAL_RESISTANCES.getParams());
                mod(amount, AttributeConsts.WIL_GRIT_BONUS, attr, PARAMS.GRIT);
                mod(amount, AttributeConsts.WIL_SPIRIT_BONUS, attr, PARAMS.SPIRIT);
                mod(before25, AttributeConsts.B25_WIL_FOC_BONUS , attr,PARAMS.STARTING_FOCUS);
                //TODO spirit?
                // mod(amount - 25, AttributeConsts.A25_WIL_FOC_BONUS , attr,PARAMS.STARTING_FOCUS);
                break;
            case SPELLPOWER:
                // power of spells! damage modifier for <?>
                mod(amount, AttributeConsts.SP_GRIT_BONUS, attr, PARAMS.GRIT);
                mod(before25, AttributeConsts.B25_SP_PENETRATION , attr,PARAMS.RESISTANCE_PENETRATION);
                //TODO
                // mod(amount - 25, AttributeConsts.A25_INT_MASTERY_MOD  , attr, PARAMS.MASTERY_SCORE_MOD);
                break;
            case INTELLIGENCE:
                //memorize cap
                // + damage modifier
                mod(amount, AttributeConsts.INT_WIT_BONUS, attr, PARAMS.WIT);
                mod(before25, AttributeConsts.B25_INT_PENETRATION  , attr, PARAMS.RESISTANCE_PENETRATION );
                mod(amount - 25, AttributeConsts.A25_INT_MASTERY_MOD  , attr, PARAMS.MASTERY_SCORE_MOD);
                break;
            case KNOWLEDGE:
                //verbatim transcribe cap
                mod(amount, AttributeConsts.KN_WIT_BONUS, attr, PARAMS.WIT);
                mod(before25, AttributeConsts.B25_KN_ASTRAL_RES  , attr, ASTRAL_RESISTANCES.getParams() );
                mod(amount - 25, AttributeConsts.A25_KN_PENETRATION  , attr, PARAMS.RESISTANCE_PENETRATION );
                break;
            case WISDOM:
                mod(amount, AttributeConsts.WIS_ESSENCE_BONUS, attr, PARAMS.ESSENCE);
                mod(amount, AttributeConsts.WIS_SPIRIT_BONUS, attr, PARAMS.SPIRIT );
                mod(amount, AttributeConsts.WIS_LUCK_BONUS, attr, PARAMS.LUCK );
                mod(amount - 25, AttributeConsts.B25_WIS_ELEMENTAL_RES , attr, ELEMENTAL_RESISTANCES.getParams());
                mod(before25, AttributeConsts.A25_WIS_DETECTION, attr, PARAMS.DETECTION);

            case CHARISMA:
                //divination cap
                // luck?
                mod(amount, AttributeConsts.CHA_SPIRIT_BONUS, attr, PARAMS.SPIRIT );
                mod(amount, AttributeConsts.CHA_LUCK_BONUS, attr, PARAMS.LUCK );
                mod(amount, AttributeConsts.CHA_DEITY_BONUS, attr, PARAMS.DEITY_EFFECTS_MOD);
                mod(amount - 25, AttributeConsts.B25_CHA_DISCOUNT , attr, PARAMS.GOLD_COST_REDUCTION);
                mod(before25, AttributeConsts.A25_CHA_ASTRAL_RES , attr, ASTRAL_RESISTANCES.getParams());
                break;

        }

    }


}
