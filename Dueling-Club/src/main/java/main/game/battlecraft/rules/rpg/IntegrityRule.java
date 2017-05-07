package main.game.battlecraft.rules.rpg;

import main.content.ContentManager;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.content.ValuePages;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.HeroEnums.PRINCIPLES;
import main.content.values.parameters.PARAMETER;
import main.entity.Entity;
import main.entity.EntityCheckMaster;
import main.entity.obj.unit.Unit;
import main.system.DC_Formulas;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

public class IntegrityRule {

    private static final String MASTERY_SCORES_BONUS_FORMULA = "({amount}-100)*0.5";

    public static PARAMETER[] INTEGRITY_MODIFIED_VALUES;

    private static PARAMETER[] getIntegrityModifiedParameters() {
        if (INTEGRITY_MODIFIED_VALUES == null) {
            INTEGRITY_MODIFIED_VALUES = new PARAMETER[]{
                    ContentManager.getMasteryScore(PARAMS.LEADERSHIP_MASTERY),
                    ContentManager.getMasteryScore(PARAMS.DIVINATION_MASTERY), PARAMS.XP_LEVEL_MOD,
                    PARAMS.GOLD_COST_REDUCTION, PARAMS.SPIRIT, PARAMS.FOCUS_RESTORATION,
                    PARAMS.FOCUS_RETAINMENT, PARAMS.STARTING_FOCUS,
                    // + Organization
                    // + Personality modifiers
            };
        }
        return INTEGRITY_MODIFIED_VALUES;
    }

    public static Integer getMaxIdentityValue() {
        return 5;
    }

    public static Integer getMinIdentityValue() {
        return -3;
    }

    public static Integer getMaxIntegrityValue() {
        return 300;
    }

    public static Integer getMaxAlignmentValue() {
        return 30;
    }

    public static Integer getMinAlignmentValue() {
        return -20;
    }

    public static List<String> getIntegrityBonusInfo(Unit hero) {
        List<String> list = new LinkedList<>();
        int integrity = hero.getIntParam(PARAMS.INTEGRITY);
        int amount = getMasteryScoresBonus(integrity, hero);
        String string = " " + amount;
        if (amount > 0) {
            string = "+" + string;
        }
        if (integrity == 0) {
            list.add("No effect on Mastery Scores");
        } else {
            list.add(string + "% to all Mastery Scores");
        }
        String focusString = "";
        for (PARAMETER param : getIntegrityModifiedParameters()) {
            amount = getBonus(param, integrity, hero);
            string = amount + "% to " + param.getName();
            if (amount > 0) {
                string = "+" + string;
            }

            if (amount == 0) {
                list.add("No effect on " + param.getName());
            } else {
                if (param.getName().contains("Focus")) {
                    if (focusString.isEmpty()) {
                        focusString += string; // w/o last
                    } else {
                        focusString += ", " + param.getName();
                    }
                } else {
                    list.add(string);
                }
            }
        }
        list.add(focusString);
        return list;
    }

    private static int getMasteryScoresBonus(int amount, Unit hero) {
        return getBonus(null, amount, hero);
    }

    private static int getBonus(PARAMETER param, int amount, Unit hero) {
        String formula = MASTERY_SCORES_BONUS_FORMULA;

        if (param == null) {
            formula = MASTERY_SCORES_BONUS_FORMULA;
        } else {
            switch (param.getName()) {
                case "Leadership Mastery Score":
                    formula = MASTERY_SCORES_BONUS_FORMULA;
                    break;
            }
        }
        int bonus = DC_Formulas.calculateFormula(formula, amount);
        return bonus;
    }

    public static void resetIntegrity(Unit hero) {
        resetAlignment(hero);
        resetIdentification(hero);
        for (PRINCIPLES principle : HeroEnums.PRINCIPLES.values()) {
            int product = getIntegrityMod(hero, principle);
            hero.modifyParameter(PARAMS.INTEGRITY, product);

        }
    }

    public static int getIntegrityMod(Entity hero, PRINCIPLES principle) {
        PARAMETER alignment_param = DC_ContentManager.getAlignmentForPrinciple(principle);
        PARAMETER identity_param = DC_ContentManager.getIdentityParamForPrinciple(principle);
        Integer degree = hero.getIntParam(identity_param);
        Integer amount = hero.getIntParam(alignment_param);
        int product = getIntegrityProduct(degree, amount);
        return product;
    }

    private static int getIntegrityProduct(Integer degree, Integer amount) {
        return amount * degree;
    }

    public static List<PRINCIPLES> getAffectingPrinciples(Entity item, Entity hero) {
        List<PRINCIPLES> list = new LinkedList<>();
        for (PRINCIPLES p : HeroEnums.PRINCIPLES.values()) {
            for (Integer val : getValues(p, item, hero)) {
                if (val != 0) {
                    if (!list.contains(p)) {
                        list.add(p);
                    }
                }
            }
        }
        return list;
    }

    public static boolean isAffectingPrinciple(Entity item, Entity hero, PRINCIPLES principle) {
        if (principle == null) {
            for (PRINCIPLES p : HeroEnums.PRINCIPLES.values()) {
                for (Integer val : getValues(p, item, hero)) {
                    if (val != 0) // cache affected principles?
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        for (Integer val : getValues(principle, item, hero)) {
            if (val != 0) {
                return true;
            }
        }
        return false;
    }

    public static Integer[] getValues(PRINCIPLES principle, Entity item, Entity hero) {
        PARAMETER alignment_param = DC_ContentManager.getAlignmentForPrinciple(principle);
        PARAMETER identity_param = DC_ContentManager.getIdentityParamForPrinciple(principle);
        Integer alignment = hero.getIntParam(alignment_param);
        Integer identity = hero.getIntParam(identity_param);
        if (item != null) {
            alignment = item.getIntParam(alignment_param);
            identity = item.getIntParam(identity_param);
        }
        return new Integer[]{identity, alignment,
                getIntegrityProduct(hero.getIntParam(identity_param), alignment)};
    }

    private static void resetAlignment(Unit hero) {

        // for (DC_FeatObj c : hero.getClasses()) { TODO automatic now
        // applyAlignmentMods(hero, c);
        // }
    }

    private static void resetIdentification(Unit hero) {
        // TODO background, deity, choice, from skills/classes t ...

        applyIdentityMods(hero, hero.getDeity());
    }

    public static void applyIntegrity(Unit hero) {
        // TODO revamp and fix when ready...

        // Integer mod = hero.getIntParam(PARAMS.INTEGRITY);
        // if (mod != 100) {
        // hero.modifyParameter(PARAMS.GOLD_COST_REDUCTION, mod - 100);
        // hero.modifyParameter(PARAMS.XP_GAIN_MOD, mod - 100);
        // hero.modifyParameter(ContentManager.getMasteryScore(PARAMS.LEADERSHIP_MASTERY),
        // mod - 100);
        // // + battlespirit
        // }
        // if (Math.abs(mod - 100) > 50) {
        // if (mod > 50)
        // mod = mod - 150;
        // else
        // mod = mod - 50;
        // for (PARAMETER p : ContentManager.getMasteries()) {
        // hero.multiplyParamByPercent(p, mod, false);
        // }
        // }
    }

    public static void applyAlignmentMods(Entity hero, Entity from) {
        applyMods(hero, from, true);
    }

    public static void applyIdentityMods(Entity hero, Entity from) {
        applyMods(hero, from, false);
    }

    public static void applyMods(Entity hero, Entity from, boolean alignment_identity) {
        if (from == null) {
            return;
        }
        PARAMETER[] array = alignment_identity ? ValuePages.PRINCIPLE_ALIGNMENTS
                : ValuePages.PRINCIPLE_IDENTITIES;
        for (PARAMETER param : array) {
            Integer amount = from.getIntParam(param);
            // min/max?
            hero.modifyParameter(param, amount);
        }

    }

    public static String getDescription(Entity hero) {
        Object[] levels = getHighestLowestLevels(true, hero);
        PRINCIPLES highestPrinciple = (PRINCIPLES) levels[0];
        ALIGNMENT_LEVEL highestLevel = (ALIGNMENT_LEVEL) levels[1];
        PRINCIPLES lowestPrinciple = (PRINCIPLES) levels[2];
        ALIGNMENT_LEVEL lowestLevel = (ALIGNMENT_LEVEL) levels[3];

        String adjective = "";
        if (highestPrinciple != null && highestLevel != null) {
            adjective = getAdjective(highestPrinciple, highestLevel);
        } else {
            if (lowestPrinciple != null && lowestLevel != null) {
                adjective = getAdjective(lowestPrinciple, lowestLevel);
            }
        }
        levels = getHighestLowestLevels(false, hero);
        highestPrinciple = (PRINCIPLES) levels[0];
        IDENTITY_LEVEL highestLevel2 = (IDENTITY_LEVEL) levels[1];
        String noun = "";
        if (highestLevel2 != null) {
            switch (highestLevel2) {
                case FONDNESS:
                    noun = getNounFond(highestPrinciple, hero);
                    break;
                case PREDILECTION:
                    noun = getNounPredilection(highestPrinciple, hero);
                    break;
                case LOVE:
                    noun = getNounLove(highestPrinciple, hero);
                    break;
            }
        }

		/*
         * if no positive alignment,
		 * 
		 * adjective per alignment 
		 * 
		 * TODO this is great but what if the signs don't match? "Guilty" -TR will increase INT ? why not: ) 
		 * short description should be ADJ+NOUN still...
		 * but each Principle should have a 'status' - Alignment and Id. if present! 
		 * 
		 */
        String string = noun;
        if (!StringMaster.isEmpty(adjective)) {
            string = adjective + " " + noun;
        }
        return string;
    }

    public static IDENTITY_LEVEL getIdentityLevel(Integer amount) {
        return (IDENTITY_LEVEL) getLevel(amount, IDENTITY_LEVEL.values());
    }

    public static ALIGNMENT_LEVEL getAlignmentLevel(Integer amount) {
        return (ALIGNMENT_LEVEL) getLevel(amount, ALIGNMENT_LEVEL.values());
    }

    private static INTEGRITY_LEVEL getIntegrityLevel(Integer amount) {
        return (INTEGRITY_LEVEL) getLevel(amount, INTEGRITY_LEVEL.values());
    }

    public static Object[] getHighestLowestLevels(boolean alignment, Entity hero) {
        Integer highestAmount = 0;
        PRINCIPLES highestPrinciple = null;
        VALUE_LEVEL highestLevel = null;
        Integer lowestAmount = 0;
        PRINCIPLES lowestPrinciple = null;
        VALUE_LEVEL lowestLevel = null;
        for (PRINCIPLES principle : HeroEnums.PRINCIPLES.values()) {
            Integer amount = hero
                    .getIntParam(DC_ContentManager.getAlignmentForPrinciple(principle));
            VALUE_LEVEL level = alignment ? getAlignmentLevel(amount) : getIdentityLevel(amount);
            if (highestAmount < amount) {
                highestPrinciple = principle;
                highestLevel = level;
                highestAmount = amount;
            }
            if (lowestAmount > amount) {
                highestPrinciple = principle;
                highestLevel = level;
                highestAmount = amount;
            }
        }
        return new Object[]{highestPrinciple, highestLevel, lowestPrinciple, lowestLevel};
    }

    public static ALIGNMENT_LEVEL getAlignmentLevel(PRINCIPLES principle, Entity hero) {
        Integer amount = hero.getIntParam(DC_ContentManager.getAlignmentForPrinciple(principle));
        ALIGNMENT_LEVEL alignmentLevel = getAlignmentLevel(amount);
        return alignmentLevel;
    }

    public static IDENTITY_LEVEL getIdentityLevel(PRINCIPLES principle, Entity hero) {
        Integer amount = hero.getIntParam(DC_ContentManager.getAlignmentForPrinciple(principle));
        IDENTITY_LEVEL identityLevel = getIdentityLevel(amount);
        return identityLevel;
    }

    public static VALUE_LEVEL getLevel(Integer amount, VALUE_LEVEL[] levels) {
        VALUE_LEVEL level = null;
        for (VALUE_LEVEL level1 : levels) {
            if (level1.getBarrier() < 0) {
                if (amount <= level1.getBarrier()) {
                    level = level1;
                }
            } else if (amount >= level1.getBarrier()) {
                level = level1;
            } else {
                break;
            }
        }
        return level;
    }

    // FONDNESS
    // PREDILECTION ,
    // LOVE
    public static String getNounLove(PRINCIPLES principle, Entity hero) {
        return getNounPredilection(principle, hero);

    }

    public static String getNounPredilection(PRINCIPLES principle, Entity hero) {
        switch (principle) {
            case FREEDOM:
                return "Rebel"; // Troublemaker renegade, daredevil,
            // troublemaker, adventurer
            case HONOR:
                if (EntityCheckMaster.getGender(hero) == HeroEnums.GENDER.FEMALE) {
                    return "Lady";
                }
                return "Gentleman";
            case TREACHERY:
                // if (EntityMaster.getGender(hero) == GENDER.FEMALE)
                // return "Femme Fatale";
                return "Scoundrel"; // Rascal, .... female?..
            case AMBITION:
                return "Schemer";
            case WAR:
                return "Bully"; // Warmonger,
            case CHARITY:
                return "Giver"; // Teacher
            case LAW:
                return "Citizen";
            case PEACE:
                return "Teacher";
            case PROGRESS:
                return "Idealist"; // Achiever Student
            case TRADITION:
                return "Follower";
        }
        return getEmptyDescription(hero);
    }

    public static String getNoun(PRINCIPLES principle, Entity hero) {
        IDENTITY_LEVEL identityLevel = IntegrityRule.getIdentityLevel(principle, hero);
        if (identityLevel == null) {
            return null;
        }
        String s = "";
        switch (identityLevel) {
            case CONTEMPT:
                return IntegrityRule.getNounPredilection(principle.getOpposite(), hero);
            case HATRED:
                return IntegrityRule.getNounLove(principle.getOpposite(), hero);
            case DISREGARD:
                return IntegrityRule.getNounDisregard(principle, hero);
            case FONDNESS:
                return IntegrityRule.getNounFond(principle, hero);
            case PREDILECTION:
                return IntegrityRule.getNounPredilection(principle, hero);
            case LOVE:
                return IntegrityRule.getNounLove(principle, hero);
        }
        return s;
    }

    public static String getNounDisregard(PRINCIPLES principle, Entity hero) {
        switch (principle) {
            case FREEDOM:
                return "Ascetic";
            case HONOR:
                return "Sneak";
            case TREACHERY:
                if (EntityCheckMaster.getGender(hero) == HeroEnums.GENDER.FEMALE) {
                    return "Lady";
                }
                return "Gentleman";
            case AMBITION:
                return "Servant";
            case WAR:
                return "Bully"; // Warmonger,
            case CHARITY:
                return "Schemer"; // Teacher
            case LAW:
                return "Troublemaker";
            case PEACE:
                return "Bully";
            case PROGRESS:
                return "Beaurocrat"; // Achiever Student
            case TRADITION:
                return "Unbeliever";
        }
        return getEmptyDescription(hero);
    }

    public static String getNounFond(PRINCIPLES principle, Entity hero) {
        switch (principle) {
            case FREEDOM:
                return "Free Spirit"; // renegade, daredevil, troublemaker,
            // adventurer
            case HONOR:
                if (EntityCheckMaster.getGender(hero) == HeroEnums.GENDER.FEMALE) {
                    return "Lady";
                }
                return "Gentleman";
            case TREACHERY:
                // if (EntityMaster.getGender(hero) == GENDER.FEMALE)
                // return "Femme Fatale";
                return "Scoundrel"; // Rascal, .... female?..
            case AMBITION:
                return "Schemer";
            case WAR:
                return "Bully"; // Warmonger,
            case CHARITY:
                return "Giver"; // Teacher
            case LAW:
                return "Citizen";
            case PEACE:
                return "Teacher";
            case PROGRESS:
                return "Idealist"; // Achiever Student
            case TRADITION:
                return "Follower";
        }
        return getEmptyDescription(hero);
    }

    private static String getEmptyDescription(Entity hero) {
        if (EntityCheckMaster.getGender(hero) == HeroEnums.GENDER.FEMALE) {
            return "Woman";
        }
        return "Man";
    }

    public static String getAdjective(PRINCIPLES principle, ALIGNMENT_LEVEL level) {
        // shouldn't LEVEL be different for each principle?
        switch (principle) {
            case AMBITION:
                switch (level) {
                    case VERY_LOW:
                        return "Desperate";
                    case LOW:
                        return "Anxious";
                    case NORMAL:
                        return "Ambitious";
                    case HIGH:
                        return "Vane"; // accomplished
                    case VERY_HIGH:
                        return "Self-Agrandized";
                }
            case CHARITY:
                switch (level) {
                    case VERY_LOW:
                        return "Gluttonous";
                    case LOW:
                        return "Conflicted";
                    case NORMAL:
                        return "Charitable";
                    case HIGH:
                        return "Selfless";
                    case VERY_HIGH:
                        return "Enlightened";
                }
            case FREEDOM:
                switch (level) {
                    case VERY_LOW:
                        return "Repentent";
                    case LOW:
                        return "Guilty";
                    case NORMAL:
                        return "Easygoing";
                    case HIGH:
                        return "Happy";
                    case VERY_HIGH:
                        return "Liberated";
                }
            case PEACE:
                switch (level) {
                    case VERY_LOW:
                        return "Apathetic";
                    case LOW:
                        return "Sorrowful";
                    case NORMAL:
                        return "Amiable";
                    case HIGH:
                        return "Peaceful";
                    case VERY_HIGH:
                        return "Tranquil";
                }
            case HONOR:
                switch (level) {
                    case VERY_LOW:
                        return "Dishonored";
                    case LOW:
                        return "Shamed"; // TODO
                    case NORMAL:
                        return "Honorable";
                    case HIGH:
                        return "Valiant";
                    case VERY_HIGH:
                        return "Heroic";
                }
            case LAW:
                switch (level) {
                    case VERY_LOW:
                        return "Renegade";
                    case LOW:
                        return "Conflicted";
                    case NORMAL:
                        return "Lawful";
                    case HIGH:
                        return "Loyal";
                    case VERY_HIGH:
                        return "Fanatical";
                }
            case PROGRESS:
                switch (level) {
                    case VERY_LOW:
                        return "Disillusioned";
                    case LOW:
                        return "Sceptical"; // Apathetic
                    case NORMAL:
                        return "Aspiring";
                    case HIGH:
                        return "Enthusiastic";
                    case VERY_HIGH:
                        return "Accomplished";
                }
            case TRADITION:
                switch (level) {
                    case VERY_LOW:
                        return "Faithless";
                    case LOW:
                        return "Prodigal";
                    case NORMAL:
                        return "Diligent"; // Abiding, dutiful, upright, decent,
                    // upstanding, obedient
                    case HIGH:
                        return "Pious";
                    case VERY_HIGH:
                        return "Righteous";
                }
            case WAR:
                switch (level) {
                    case VERY_LOW:
                        return "Pacifist";
                    case LOW:
                        return "Merciful";
                    case NORMAL:
                        return "Cruel";
                    case HIGH:
                        return "Belligerent";
                    case VERY_HIGH:
                        return "Bloodthirsty";
                }

            case TREACHERY:
                switch (level) {
                    case VERY_LOW:
                        return "Honest";
                    case LOW:
                        return "Self-Conscious";
                    case NORMAL:
                        return "Treacherous";
                    case HIGH:
                        return "Scheming";
                    case VERY_HIGH:
                        return "Devious";
                }

        }
        return "";
    }

    public enum ALIGNMENT_LEVEL implements VALUE_LEVEL {
        VERY_LOW(-20), LOW(-10), NORMAL(10), HIGH(20), VERY_HIGH(30),;
        int barrier;

        ALIGNMENT_LEVEL(int barrier) {
            this.barrier = barrier;
        }

        public int getBarrier() {
            return barrier;
        }
    }

    public enum INTEGRITY_LEVEL implements VALUE_LEVEL {
        VERY_LOW(0), LOW(50), NORMAL(100), HIGH(150), VERY_HIGH(250),;
        int barrier;

        INTEGRITY_LEVEL(int barrier) {
            this.barrier = barrier;
        }

        public int getBarrier() {
            return barrier;
        }
    }

    public enum IDENTITY_LEVEL implements VALUE_LEVEL {
        HATRED(-5),
        CONTEMPT(-3),
        DISREGARD(-1),
        INDIFFERENCE(0),
        FONDNESS(1),
        PREDILECTION(3),
        LOVE(5),;
        int barrier;

        IDENTITY_LEVEL(int barrier) {
            this.barrier = barrier;
        }

        public int getBarrier() {
            return barrier;
        }
    }

    public interface VALUE_LEVEL {
        public int getBarrier();
    }

}
