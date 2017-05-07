package main.entity.obj.hero;

import main.content.ContentManager;
import main.content.DC_ContentManager;
import main.content.DC_ContentManager.ATTRIBUTE;
import main.content.PARAMS;
import main.entity.obj.unit.Unit;
import main.system.DC_ConditionMaster;
import main.system.DC_Formulas;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;

public class DC_Attributes {

    private Unit hero;

    public DC_Attributes(Unit hero) {
        this.hero = hero;
    }

    private int getAttrValue(ATTRIBUTE attr) {
        return hero.getIntParam(attr.getParameter());
    }

    private void applyAttr(ATTRIBUTE attr) {
        int amount = getAttrValue(attr);
        String modifierKey = StringMaster.getWellFormattedString(attr.toString());
        switch (attr) {
            case STRENGTH:
                hero.modifyParameter(PARAMS.TOUGHNESS,
                        DC_Formulas.getToughnessFromStrength(amount), "Strength");
                // damage modifier
                // reduces heavy item penalties
                //

                hero.modifyParameter(PARAMS.CARRYING_CAPACITY, DC_Formulas
                        .getCarryingCapacityFromStrength(amount), "Strength");

                break;
            case VITALITY:
                hero.modifyParameter(PARAMS.FORTITUDE,
                        DC_Formulas.getFortitudeFromVitality(amount), modifierKey);
                hero.modifyParameter(PARAMS.TOUGHNESS,
                        DC_Formulas.getToughnessFromVitality(amount), modifierKey);

                hero.modifyParameter(PARAMS.ENDURANCE,
                        DC_Formulas.getEnduranceFromVitality(amount), modifierKey);
                hero.modifyParameter(PARAMS.STAMINA, DC_Formulas.getStaminaFromVitality(amount),
                        modifierKey);

                hero.modifyParameter(PARAMS.REST_BONUS, DC_Formulas
                        .getRestBonusFromVitality(amount), modifierKey);

                if (DC_ConditionMaster.checkLiving(hero)) {
                    hero.modifyParameter(PARAMS.ENDURANCE_REGEN, DC_Formulas
                            .getEnduranceRegenFromVitality(amount), modifierKey);
                }
                break;
            case AGILITY:
                hero.modifyParameter(PARAMS.NOISE, -amount / 2, modifierKey);
                hero.modifyParameter(PARAMS.N_OF_COUNTERS, DC_Formulas.getCountersFromAgi(amount),
                        modifierKey); // with
                // strength?
                // TODO
                hero.modifyParameter(PARAMS.ATTACK, DC_Formulas.getAttackFromAgi(amount),
                        modifierKey);
                // hero.modifyParameter(PARAMS.INITIATIVE_MODIFIER, DC_Formulas
                // .getInitModFromAgi(amount * 2 / 3),
                // StringMaster.getWellFormattedString(attr.toString())); //in
                // DEXTERITY
                hero.modifyParameter(PARAMS.ATTACK_AP_PENALTY, -amount / 2, modifierKey);
                hero.modifyParameter(PARAMS.OFFHAND_ATTACK_AP_PENALTY, -amount / 2, modifierKey);
                // hero.modifyParameter(PARAMS.N_OF_ACTIONS, DC_Formulas
                // .getActsFromAgi(amount),
                // StringMaster.getWellFormattedString(attr.toString())); TODO
                // ARMOR_MOD ? //
                // ATTACK_AP_PENALTY

                break;

            case DEXTERITY:
                hero.modifyParameter(PARAMS.NOISE, -amount / 2, modifierKey);

                Integer agi = hero.getIntParam(PARAMS.AGILITY);

                hero.modifyParameter(PARAMS.INITIATIVE_MODIFIER, DC_Formulas
                                .getInitModFromAgi(MathMaster.round(new Float((amount + 2 * agi) / 3))),
                        modifierKey);

                Float apBoost = new Float(amount + new Float(agi / 2));
                hero.modifyParameter(PARAMS.N_OF_ACTIONS, DC_Formulas.getActsFromDex(MathMaster
                        .round(apBoost)), modifierKey);
                // NEW

                // hero.modifyParameter(PARAMS.N_OF_ACTIONS, DC_Formulas
                // .getActsFromDex(amount),
                // StringMaster.getWellFormattedString(attr.toString()));
                hero.modifyParameter(PARAMS.DEFENSE, DC_Formulas.getDefFromDex(amount), modifierKey);
                hero.modifyParameter(PARAMS.STEALTH, amount / 2, modifierKey);
                hero.modifyParameter(PARAMS.MOVE_AP_PENALTY, -amount / 2, modifierKey);
                // hero.modifyParameter(PARAMS.INITIATIVE_MODIFIER, DC_Formulas
                // .getInitModFromDex(amount),
                // StringMaster.getWellFormattedString(attr.toString())); //
                // MOVE_AP_PENALTY
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
                hero.modifyParameter(PARAMS.MORALE_RESTORATION, amount, modifierKey);
                hero.modifyParameter(PARAMS.MORALE_RETAINMENT, amount, modifierKey);

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
            case SPELLPOWER:
                break;
            case WISDOM:
                hero.modifyParameter(PARAMS.ESSENCE, DC_Formulas.getEssenceFromWisdom(amount),
                        modifierKey);
                hero.modifyParameter(PARAMS.DETECTION, amount / 2, modifierKey);
                hero.modifyParameter(PARAMS.PERCEPTION, amount / 2, modifierKey);
                hero.modifyParameter(PARAMS.MEDITATION_BONUS, (amount), modifierKey);
                break;
            case CHARISMA:
                hero.modifyParameter(PARAMS.DIVINATION_CAP, (amount), modifierKey);
                hero.modifyParameter(ContentManager.getMasteryScore(PARAMS.LEADERSHIP_MASTERY),
                        (amount), modifierKey);

                hero.modifyParameter(PARAMS.GOLD_COST_REDUCTION, 2 * (amount), modifierKey);

                break;
            default:
                break;

        }

    }

    public void apply() {
        // main.system.auxiliary.LogMaster.log(1, "before: " +
        // hero.getModifierMaps());
        for (ATTRIBUTE attr : DC_ContentManager.getAttributeEnums()) {
            applyAttr(attr);
        }
        // main.system.auxiliary.LogMaster.log(1, "after: " +
        // hero.getModifierMaps());
    }

}
