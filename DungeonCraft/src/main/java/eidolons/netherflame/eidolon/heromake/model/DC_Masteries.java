package eidolons.netherflame.eidolon.heromake.model;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.entity.unit.Unit;
import eidolons.content.DC_Formulas;
import main.ability.effects.Effect.MOD;
import main.content.ContentValsManager;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.content.values.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DC_Masteries {

    Map<MASTERY, Integer> map;
    private final Unit hero;

    public DC_Masteries(Unit hero) {
        this.hero = hero;
    }

    public List<PARAMETER> getHighest(int i) {
        List<PARAMETER> list = new ArrayList<>(
                DC_ContentValsManager.getMasteryParams());
        //        SortMaster.sortByParameter(hero, list, true);
        return list.subList(0, i);
    }

    public Map<MASTERY, Integer> getMap() {
        if (map == null)
            initMap();
        return map;
    }

    private void initMap() {
        map = new XLinkedMap<>();

    }

    private void initMastery(PARAMS mastery) {
        int amount = hero.getIntParam(ContentValsManager.getMasteryScore(mastery));
        if (amount <= 0) {
            return;
        }
        switch (mastery) {
            case MARKSMANSHIP_MASTERY:
                mod(mastery, amount, PARAMS.THROW_ATTACK_MOD, MOD.MODIFY_BY_CONST);
                break;// mod(mastery,(amount), PARAMS.BATTLE_SPIRIT,
            case ARMORER_MASTERY: {
                mod(mastery, -(amount), PARAMS.DURABILITY_SELF_DAMAGE_MOD, MOD.MODIFY_BY_CONST);
                break;
            }
            case DEFENSE_MASTERY:
                mod(mastery,
                        DC_Formulas.getDefenseFromDefenseMastery(amount), PARAMS.DEFENSE, MOD.MODIFY_BY_CONST);
                break;
            case DUAL_WIELDING_MASTERY:
                mod(mastery, amount, PARAMS.OFFHAND_ATTACK_MOD,
                        MOD.MODIFY_BY_CONST);
                mod(mastery, amount, PARAMS.OFFHAND_DAMAGE_MOD,
                        MOD.MODIFY_BY_CONST);

                if (hero.checkDualWielding()) {
                    mod(mastery, -amount / 2, PARAMS.ATTACK_ATB_COST_MOD,
                            MOD.MODIFY_BY_CONST);
                    mod(mastery, -amount / 2,
                            PARAMS.OFFHAND_ATTACK_ATB_COST_MOD,
                            MOD.MODIFY_BY_CONST);
                }
                break;
            case DETECTION_MASTERY:
                mod(mastery, amount, PARAMS.DETECTION,
                        MOD.MODIFY_BY_CONST);
                mod(mastery, amount / 2, PARAMS.PERCEPTION,
                        MOD.MODIFY_BY_CONST);
                break;
            case STEALTH_MASTERY:
                mod(mastery, amount, PARAMS.STEALTH,
                        MOD.MODIFY_BY_CONST);
                mod(mastery, -amount / 2, PARAMS.NOISE,
                        MOD.MODIFY_BY_CONST);
                break;

            case ATHLETICS_MASTERY:
                hero.modifyParamByPercent(PARAMS.VITALITY, amount);
                hero.modifyParamByPercent(PARAMS.STRENGTH, amount);
                break;
            case MEDITATION_BONUS:
                mod(mastery, amount / 5, PARAMS.MEDITATION_BONUS,
                        MOD.MODIFY_BY_CONST);
                break;
            case DISCIPLINE_MASTERY:
                hero.modifyParamByPercent(PARAMS.WILLPOWER, 2 * amount);
                break;
            case MEDITATION_MASTERY:
                hero.modifyParamByPercent(PARAMS.WISDOM, 2 * amount);
                break;
            case MOBILITY_MASTERY:
                hero.modifyParamByPercent(PARAMS.AGILITY, amount);
                hero.modifyParamByPercent(PARAMS.DEXTERITY, amount);
                break;
            case SPELLCRAFT_MASTERY:
                hero.modifyParamByPercent(PARAMS.SPELLPOWER, 2 * amount);

                break;
            case WIZARDRY_MASTERY:
                mod(mastery, amount, //MasteryConsts.WIZARDRY_KN_PERC
                        1, PARAMS.KNOWLEDGE, MOD.MODIFY_BY_PERCENT);
                mod(mastery, amount, //MasteryConsts.WIZARDRY_INT_PERC
                       1 , PARAMS.INTELLIGENCE, MOD.MODIFY_BY_PERCENT);
                break;
            default:
                break;
        }
    }

    private void mod(PARAMETER mastery, int amount, float modifier, PARAMETER params, MOD modtype) {
        int result = Math.round(modifier * amount);
        mod(mastery, result, params, modtype);
    }

    private void mod(PARAMETER mastery, int amount, PARAMETER param, MOD modtype) {
        String modifierKey = StringMaster.format(mastery.getName());
        switch (modtype) {
            case MODIFY_BY_PERCENT:
                hero.modifyParamByPercent(param, amount, true, modifierKey);
                break;
            case MODIFY_BY_CONST:
                hero.modifyParameter(param, amount, modifierKey);
                break;
        }
    }

    public void apply() {
        for (PARAMS mastery : DC_ContentValsManager.getMasteryParams()) {
            initMastery(mastery);
        }

    }

} // for (DC_SpellObj spell : hero.getSpells()) {
// String mod = DC_Formulas
// .getEssCostReductionFromSpellcraft(amount);
// spell.modifyCost(DC_PARAMS.ESS_COST, mod);}
// case ENCHANTER_MASTERY:
// for (DC_SpellObj spell : hero.getSpells()) {
// if (!spell.isEnchantment())
// continue;
// spell.setFocusRequirementMod(DC_Formulas
// .getFocReqReductionFromEnchantment(amount));
//
// }
// break;
// case SORCERER_MASTERY:
// for (DC_SpellObj spell : hero.getSpells()) {
// if (!spell.isSorcery())
// continue;
// spell.setFocusRequirementMod(DC_Formulas
// .getFocReqReductionFromSorcery(amount));
// // +% to damage formulas? or use in final damage calc? or change
// // to give resistance penetr
// }
// break;
// case SUMMONER_MASTERY:
// // for (DC_SpellObj spell : hero.getSpells()) {
// // if (!spell.isSummoning())
// // continue;
// // spell.setFocusRequirementMod(DC_Formulas
// // .getFocReqReductionFromSummoning(amount));
// //
// // }
// break;
// case DIVINATION_MASTERY:
// // reduce divination cost
// break;