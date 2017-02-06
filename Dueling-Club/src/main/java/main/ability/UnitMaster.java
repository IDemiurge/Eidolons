package main.ability;

import main.client.cc.logic.UnitLevelManager;
import main.content.CONTENT_CONSTS.CLASSIFICATIONS;
import main.content.PARAMS;
import main.content.ValuePages;
import main.content.parameters.PARAMETER;
import main.entity.obj.DC_HeroObj;
import main.rules.generic.UnitAnalyzer;
import main.system.launch.CoreEngine;
import main.system.math.MathMaster;
import main.test.frontend.FAST_DC;

public class UnitMaster {

    private static final int DEFAULT_XP_MOD = -33;
    private static final int HUMANOID_XP_MOD = -33;
    private static final int DEFAULT_CASTER_XP_MOD = 60;
    private static boolean random;

    static {
        // if (UnitGroupMaster.isFactionMode())
        // setRandom(false);
    }

    private static boolean shopOn=true;
    private static boolean spellsOn;
    private static boolean skillsOn;

    public static void train(DC_HeroObj unit) {
        if (!FAST_DC.getGameLauncher().getFAST_MODE())
            if (CoreEngine.isGraphicTestMode())
                return;

        int perc = DEFAULT_XP_MOD;

        if (unit.checkClassification(CLASSIFICATIONS.HUMANOID)) {
            perc += HUMANOID_XP_MOD;
        }
        unit.modifyParamByPercent(PARAMS.XP, perc);

        Integer spell_xp = unit.getIntParam(PARAMS.SPELL_XP_MOD);

        if (spell_xp == 0 && UnitAnalyzer.checkIsCaster(unit)) {
            spell_xp = getSpellXpPercentage(unit);
            spell_xp = MathMaster.applyMod(DEFAULT_CASTER_XP_MOD, spell_xp);
        }

        // DEFAULT_CASTER_XP_MOD;
        int spellXp = unit.getIntParam(PARAMS.XP) * (spell_xp) / 100;
        int skillXp = unit.getIntParam(PARAMS.XP) - spellXp;

        unit.setParam(PARAMS.XP, skillXp);
        if (isSkillsOn())
            try {
                //// TODO: 17.11.2016 improve train func execution speed
                UnitTrainer.train(unit);
            } catch (Exception e) {
                e.printStackTrace();
            }
        if (isShopOn())
            try {
                UnitShop.buyItemsForUnit(unit);
            } catch (Exception e) {
                e.printStackTrace();
            }

        unit.modifyParameter(PARAMS.XP, spellXp);

        if (isSpellsOn())
            try {
                UnitLibrary.learnSpellsForUnit(unit);
            } catch (Exception e) {
                e.printStackTrace();
            }

        try {
            // adjust XP/Gold as per mods and default types...
            // monsters should probably getOrCreate more...
            // and then there is the Unit Level...

            new UnitLevelManager().buyPoints(true, unit.getType());
            new UnitLevelManager().buyPoints(false, unit.getType());
            new UnitLevelManager().spendPoints(unit.getType(), true);
            // will xp/gold be "spent"? WriteToType, nota bene...
            unit.toBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Integer getSpellXpPercentage(DC_HeroObj unit) {
        Integer mastery = 0;
        for (PARAMETER m : ValuePages.MASTERIES) {
            mastery += (unit.getIntParam(m));
        }
        Integer spellMastery = 0;
        for (PARAMETER m : ValuePages.MASTERIES_MAGIC_SCHOOLS) {
            spellMastery += (unit.getIntParam(m));
        }
        return Math.round(new Float(spellMastery) / mastery * 100);

    }

    public static boolean isRandom() {
        return random;
    }

    public static void setRandom(boolean random) {
        UnitMaster.random = random;
    }

    public static boolean isShopOn() {
        return shopOn;
    }

    public static void setShopOn(boolean shopOn) {
        UnitMaster.shopOn = shopOn;
    }

    public static boolean isSpellsOn() {
        return spellsOn;
    }

    public static void setSpellsOn(boolean spellsOn) {
        UnitMaster.spellsOn = spellsOn;
    }

    public static boolean isSkillsOn() {
        return skillsOn;
    }

    public static void setSkillsOn(boolean skillsOn) {
        UnitMaster.skillsOn = skillsOn;
    }
}
