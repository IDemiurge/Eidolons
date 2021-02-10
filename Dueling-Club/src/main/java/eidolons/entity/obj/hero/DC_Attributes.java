package eidolons.entity.obj.hero;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.DC_ContentValsManager.ATTRIBUTE;
import eidolons.content.DC_ValueManager;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.system.DC_ConditionMaster;
import eidolons.system.DC_Formulas;
import main.content.ContentValsManager;
import main.content.values.parameters.PARAMETER;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;

public class DC_Attributes {

    private final Unit hero;

    public DC_Attributes(Unit hero) {
        this.hero = hero;
    }

    private int getAttrValue(ATTRIBUTE attr) {
        return hero.getIntParam(attr.getParameter());
    }

    private void applyAttr(ATTRIBUTE attr) {
        int amount = getAttrValue(attr);
        String modifierKey = StringMaster.format(attr.toString());
        switch (attr) {
            case STRENGTH:
                if (hero.isHero()) {
                    hero.modifyParameter(PARAMS.TOUGHNESS,
                            DC_Formulas.getToughnessFromStrengthHero(amount), "Strength");
                } else {
                    hero.modifyParameter(PARAMS.TOUGHNESS,
                            DC_Formulas.getToughnessFromStrength(amount), "Strength");
                }
                // damage modifier
                // reduces heavy item penalties
                //

                hero.modifyParameter(PARAMS.CARRYING_CAPACITY, DC_Formulas
                 .getCarryingCapacityFromStrength(amount), "Strength");

                break;
            case VITALITY:
                hero.modifyParameter(PARAMS.FORTITUDE,
                 DC_Formulas.getFortitudeFromVitality(amount), modifierKey);

                if (!hero.isHero()) {
                hero.modifyParameter(PARAMS.TOUGHNESS,
                 DC_Formulas.getToughnessFromVitality(amount), modifierKey);
                }

                hero.modifyParameter(PARAMS.ENDURANCE,
                 DC_Formulas.getEnduranceFromVitality(amount), modifierKey);

                hero.modifyParameter(PARAMS.REST_BONUS, DC_Formulas
                 .getRestBonusFromVitality(amount), modifierKey);

                if (RuleKeeper.isHeroEnduranceRegenOn() || !hero.isHero())
                if (DC_ConditionMaster.checkLiving(hero)) {
                    hero.modifyParameter(PARAMS.ENDURANCE_REGEN, DC_Formulas
                     .getEnduranceRegenFromVitality(amount), modifierKey);
                }
                hero.modifyParameter(PARAMS.TOUGHNESS_RECOVERY, amount/5, false);
                hero.modifyParameter(PARAMS.TOUGHNESS_RETAINMENT, amount/5, false);
                break;
            case AGILITY:
                hero.modifyParameter(PARAMS.ATTACK, DC_Formulas.getAttackFromAgi(amount),
                        modifierKey);

                hero.modifyParameter(PARAMS.EXTRA_ATTACKS, DC_Formulas.getCountersFromAgi(amount),
                 modifierKey);
                hero.modifyParameter(PARAMS.ATTACK_ATB_COST_MOD, -amount / 2, modifierKey);
                hero.modifyParameter(PARAMS.OFFHAND_ATTACK_ATB_COST_MOD, -amount / 2, modifierKey);

                break;

            case DEXTERITY:

                hero.modifyParameter(PARAMS.DEFENSE, DC_Formulas.getDefFromDex(amount), modifierKey);

                Float apBoost = (float) (amount + hero.getParamDouble(PARAMS.AGILITY));
                int bonus = DC_Formulas.getActsFromDexAndAgility(MathMaster
                        .round(apBoost));
                hero.modifyParameter(PARAMS.INITIATIVE, bonus, modifierKey);

                hero.modifyParameter(PARAMS.STEALTH, amount / 2, modifierKey);
                hero.modifyParameter(PARAMS.NOISE, -amount / 2, modifierKey);

                hero.modifyParameter(PARAMS.MOVE_ATB_COST_MOD, -amount / 2, modifierKey);
                hero.modifyParameter(PARAMS.EXTRA_MOVES, DC_Formulas.getExtraMovesFromDex(amount));
                break;

            case WILLPOWER:
                hero.modifyParameter(PARAMS.RESISTANCE, DC_Formulas.getResistanceFromWill(amount),
                 modifierKey);


                hero.modifyParameter(PARAMS.SPIRIT, DC_Formulas.getSpiritFromWill(amount),
                 modifierKey);
                hero.modifyParameter(PARAMS.STARTING_FOCUS, DC_Formulas
                 .getStartingFocusFromWill(amount), modifierKey);
                hero.modifyParameter(PARAMS.INTERRUPT_DAMAGE, DC_Formulas
                 .getStartingFocusFromWill(amount), modifierKey);

                hero.modifyParameter(PARAMS.FOCUS_RESTORATION, amount, modifierKey);
                hero.modifyParameter(PARAMS.FOCUS_RETAINMENT, amount, modifierKey);
                hero.modifyParameter(PARAMS.ESSENCE_RESTORATION, amount, modifierKey);
                hero.modifyParameter(PARAMS.ESSENCE_RETAINMENT, amount, modifierKey);

                hero.modifyParameter(PARAMS.CONCENTRATION_BONUS, (amount / 2), modifierKey);

                break;

            case INTELLIGENCE:
                hero.modifyParameter(PARAMS.MEMORIZATION_CAP, DC_Formulas.getMemoryFromInt(amount),
                 modifierKey);
                hero.modifyParameter(PARAMS.XP_GAIN_MOD, amount, modifierKey);
                hero.modifyParameter(PARAMS.XP_LEVEL_MOD, amount, modifierKey);
                hero.modifyParameter(PARAMS.DURATION_MOD, amount, modifierKey);
                break;
            case KNOWLEDGE:

                hero.modifyParameter(PARAMS.XP_COST_REDUCTION, amount, modifierKey);
                // hero.modifyParameter(PARAMS.XP_GAIN_MOD, amount,
                // StringMaster.getWellFormattedString(attr.toString()));
                // hero.modifyParameter(PARAMS.XP_LEVEL_MOD, amount,
                // StringMaster.getWellFormattedString(attr.toString()));
                break;
            case WISDOM:
                hero.modifyParameter(PARAMS.ESSENCE,amount*5);
                        hero.modifyParameter(PARAMS.ESSENCE, DC_Formulas.getEssenceFromWisdom(amount),
                 modifierKey);
                hero.modifyParameter(PARAMS.DETECTION, amount / 2, modifierKey);
                hero.modifyParameter(PARAMS.PERCEPTION, amount / 2, modifierKey);
                hero.modifyParameter(PARAMS.MEDITATION_BONUS, (amount), modifierKey);
                break;
            case CHARISMA:
                hero.modifyParameter(PARAMS.DIVINATION_CAP, (amount), modifierKey);
                hero.modifyParameter(ContentValsManager.getMasteryScore(PARAMS.LEADERSHIP_MASTERY),
                 (amount), modifierKey);

                hero.modifyParameter(PARAMS.GOLD_COST_REDUCTION, 2 * (amount), modifierKey);

                break;
            default:
                break;

        }

    }
    private void applyPostAttribute() {
        int n = hero.getIntParam(PARAMS.SPIRIT);
        for (PARAMETER param : DC_ValueManager.VALUE_GROUP.ASTRAL_RESISTANCES.getParams()) {
            hero.modifyParameter(param, n );
        }
        n = hero.getIntParam(PARAMS.FORTITUDE);
        for (PARAMETER param : DC_ValueManager.VALUE_GROUP.ASTRAL_RESISTANCES.getParams()) {
            hero.modifyParameter(param, n );
        }
//        hero.modifyParameter(param, n);
    }

    public void apply() {
        // main.system.auxiliary.LogMaster.log(1, "before: " +
        // hero.getModifierMaps());
        for (ATTRIBUTE attr : DC_ContentValsManager.getAttributeEnums()) {
            applyAttr(attr);
        }

        applyPostAttribute();

        // main.system.auxiliary.LogMaster.log(1, "after: " +
        // hero.getModifierMaps());
    }


}
