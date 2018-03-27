package main.entity.obj.hero;

import main.ability.effects.Effect.MOD;
import main.ability.effects.common.ModifyValueEffect;
import main.content.ContentManager;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.content.values.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.system.DC_Formulas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DC_Masteries {

    Map<MASTERY, Integer> map;
    private Unit hero;

    public DC_Masteries(Unit hero) {
        this.hero = hero;
    }

    public List<PARAMETER> getHighest(int i) {
        List<PARAMETER> list = new ArrayList<>(
         DC_ContentManager.getMasteryParams());
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
        int amount = hero.getIntParam(ContentManager.getMasteryScore(mastery));
        if (amount <= 0) {
            return;
        }
        switch (mastery) {
            case TACTICS_MASTERY: {
                // boostParameter((amount), PARAMS.ORGANIZATION,
                // MODVAL_TYPE.MODIFY_BY_CONST);
                break;

            }
            case LEADERSHIP_MASTERY: {
                // boostParameter((amount), PARAMS.BATTLE_SPIRIT,
                // MODVAL_TYPE.MODIFY_BY_CONST);
                break;
            }
            case ARMORER_MASTERY: {

                boostParameter(-(amount), PARAMS.DURABILITY_SELF_DAMAGE_MOD,
                 MOD.MODIFY_BY_CONST);
                break;
            }
            case DEFENSE_MASTERY:
                boostParameter(
                 DC_Formulas.getDefenseFromDefenseMastery(amount),
                 PARAMS.DEFENSE, MOD.MODIFY_BY_CONST);
                // boostParameter(DC_Formulas.getDefenseModFromDefenseMastery(amount),
                // PARAMS.DEFENSE, MODVAL_TYPE.MODIFY_BY_CONST);
                break;
            case DUAL_WIELDING_MASTERY:
                boostParameter(amount, PARAMS.OFFHAND_ATTACK_MOD,
                 MOD.MODIFY_BY_CONST);
                boostParameter(amount, PARAMS.OFFHAND_DAMAGE_MOD,
                 MOD.MODIFY_BY_CONST);
                if (hero.checkDualWielding()) {
                    boostParameter(-amount / 2, PARAMS.ATTACK_AP_PENALTY,
                     MOD.MODIFY_BY_CONST);
                    boostParameter(-amount / 2,
                     PARAMS.OFFHAND_ATTACK_AP_PENALTY,
                     MOD.MODIFY_BY_CONST);
                }
                break;
            case STEALTH_MASTERY:
                boostParameter(amount, PARAMS.STEALTH,
                 MOD.MODIFY_BY_CONST);
                break;

            case ATHLETICS_MASTERY:
                hero.modifyParamByPercent(PARAMS.VITALITY, amount);
                hero.modifyParamByPercent(PARAMS.STRENGTH, amount);
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
                hero.modifyParamByPercent(PARAMS.KNOWLEDGE, amount);
                hero.modifyParamByPercent(PARAMS.INTELLIGENCE, amount);
                // hero.modifyParamByPercent(amount, PARAMS.KNOWLEDGE);
                // hero.modifyParamByPercent(amount, PARAMS.INTELLIGENCE);
                break;
            default:
                break;
        }
    }

    private void boostParameter(int i, PARAMS p, MOD modval) {
        Ref ref = new Ref(hero.getGame(), hero.getId());
        ref.setTarget(hero.getId());
        ref.setBase(true);
        ref.setQuiet(true);
        new ModifyValueEffect(p, modval, i + "").apply(ref);
    }

    private void boostParameterByPercent(int i, PARAMS p) {
        boostParameter(i, p, MOD.MODIFY_BY_PERCENT);
    }

    public void apply() {
        for (PARAMS mastery : DC_ContentManager.getMasteryParams()) {
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