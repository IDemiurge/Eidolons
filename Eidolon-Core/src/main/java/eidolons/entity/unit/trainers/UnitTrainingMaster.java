package eidolons.entity.unit.trainers;

import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

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
