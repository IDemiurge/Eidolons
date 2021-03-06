package eidolons.ability;

import eidolons.content.PARAMS;
import eidolons.content.values.ValuePages;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.rules.UnitAnalyzer;
import eidolons.game.module.herocreator.logic.UnitLevelManager;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.math.MathMaster;

public class UnitTrainingMaster {

    private static final int DEFAULT_XP_MOD = -33;
    private static final int HUMANOID_XP_MOD = -33;
    private static final int DEFAULT_CASTER_XP_MOD = 60;
    private static boolean random;
    private static boolean shopOn = true;
    private static boolean spellsOn = true;
    private static boolean skillsOn;

    static {
        // if (UnitGroupMaster.isFactionMode())
        // setRandom(false);
    }

    public static void train(Unit unit) {
        if (CoreEngine.isLevelEditor())
            return;
        if (CoreEngine.TEST_LAUNCH)
            return;
        if (Flags.isFullFastMode())
            return;
        if (Flags.isFastMode()) {
            if (isSpellsOn()) {
                try {
                    UnitLibrary.learnSpellsForUnit(unit);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            return;
        }
        if (DC_Engine.isTrainingOff())
            return;
        //TODO [30-03-21] progression revamp

        if (isSkillsOn()) {
            try {
                //// TODO: 17.11.2016 improve train func execution speed
                UnitTrainer.train(unit);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (isShopOn()) {
            try {
                UnitShop.buyItemsForUnit(unit);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            UnitShop.ensureMinimumItems(unit);
        }


        if (isSpellsOn()) {
            try {
                UnitLibrary.learnSpellsForUnit(unit);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }

    }

    public static Integer getSpellXpPercentage(Unit unit) {
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
        UnitTrainingMaster.random = random;
    }

    public static boolean isShopOn() {
        return shopOn;
    }

    public static void setShopOn(boolean shopOn) {
        UnitTrainingMaster.shopOn = shopOn;
    }

    public static boolean isSpellsOn() {
        return spellsOn;
    }

    public static void setSpellsOn(boolean spellsOn) {
        UnitTrainingMaster.spellsOn = spellsOn;
    }

    public static boolean isSkillsOn() {
        return skillsOn;
    }

    public static void setSkillsOn(boolean skillsOn) {
        UnitTrainingMaster.skillsOn = skillsOn;
    }
}
