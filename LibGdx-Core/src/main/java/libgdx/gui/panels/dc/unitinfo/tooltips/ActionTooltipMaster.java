package libgdx.gui.panels.dc.unitinfo.tooltips;

import eidolons.content.values.DC_ValueManager;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.game.battlecraft.rules.combat.attack.AttackCalculator.MOD_IDENTIFIER;
import eidolons.game.battlecraft.rules.combat.mechanics.ForceRule;
import eidolons.system.math.ModMaster;
import main.content.VALUE;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.values.properties.G_PROPS;
import main.system.images.ImageManager;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 3/12/2017.
 */
public class ActionTooltipMaster {

    private static String getDiceText(DC_ActiveObj action) {
        DC_WeaponObj weapon = action.getActiveWeapon();
        int dieSize =
         MathMaster.applyPercent(
          weapon.getIntParam(PARAMS.DIE_SIZE),
          action.getFinalModParam(PARAMS.DAMAGE_MOD));
        Integer dice =
         MathMaster.applyPercent(
          weapon.getIntParam(PARAMS.DICE),
          action.getFinalModParam(PARAMS.DAMAGE_MOD));

        return dice + "d" + dieSize;
    }

    private static String getDamageText(DC_ActiveObj action) {
//    new AttackCalculator()
        int damage = MathMaster.applyPercent(
         action.getOwnerUnit().getIntParam(action.isOffhand() ? PARAMS.OFF_HAND_DAMAGE : PARAMS.DAMAGE),
         action.getIntParam(PARAMS.DAMAGE_MOD));
        return damage + " + " + getDiceText(action);
    }

    public static String getIconPathForTableRow(VALUE value) {

        if (value instanceof PARAMS) {
            PARAMS p = (PARAMS) value;

            switch (p) {
                case COUNTER_MOD:
                    return MOD_IDENTIFIER.COUNTER_ATTACK.getImagePath();
                case AOO_ATTACK_MOD:
                case AOO_DAMAGE_MOD:
                    return MOD_IDENTIFIER.AOO.getImagePath();
                case INSTANT_ATTACK_MOD:
                case INSTANT_DAMAGE_MOD:
                    return MOD_IDENTIFIER.INSTANT_ATTACK.getImagePath();
                case SIDE_ATTACK_MOD:
                case SIDE_DAMAGE_MOD:
                    return MOD_IDENTIFIER.SIDE_ATTACK.getImagePath();
                case DIAGONAL_ATTACK_MOD:
                case DIAGONAL_DAMAGE_MOD:
                    return MOD_IDENTIFIER.DIAGONAL_ATTACK.getImagePath();
                case CLOSE_QUARTERS_ATTACK_MOD:
                case CLOSE_QUARTERS_DAMAGE_MOD:
                    return MOD_IDENTIFIER.CLOSE_QUARTERS.getImagePath();
                case LONG_REACH_ATTACK_MOD:
                case LONG_REACH_DAMAGE_MOD:
                    return MOD_IDENTIFIER.LONG_REACH.getImagePath();
            }
        }
        return ImageManager.getValueIconPath(value);
    }

//    public static String getStringForHeader(VALUE value, DC_ActiveObj action) {
//
//    }
//    public static String getStringForRow(VALUE value, DC_ActiveObj action) {
//
//    }

    public static boolean isParamDisplayedAsCustomString(PARAMS p) {
        return p == PARAMS.DAMAGE || p == PARAMS.ATTACK;
    }

    public static String getValueForTableParam(PARAMS value, DC_ActiveObj action) {
        if (isParamDisplayedAsCustomString(value)) {
            try {
                return getStringForTableValue(value, action);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (DC_ValueManager.isCentimalModParam(value)) {
            if (action.getIntParam(value) == 0) {
                return "100";
            }
        }
        return action.getParam(value);
    }

    public static String getTextForTableValue(VALUE value,
                                              DC_ActiveObj action) {
        if (value instanceof PARAMS) {
            PARAMS p = (PARAMS) value;
//            if (isIgnoreIfZero(portrait) ) {
//
//            }
            if (DC_ValueManager.isCentimalModParam(p)) {
                if (action.getIntParam(p) == 100) {
                    return null;
                }
            }
            if (action.getIntParam(p) == 0) {
                return null; //don't show any text!
            }

            if (p.getDefaultValue().equals(action.getParam(p))) {
                return null;
            }
        }
        return getStringForTableValue(value, action);
    }

    public static String getStringForTableValue(VALUE value, DC_ActiveObj action) {
        try {
            return tryGetStringForTableValue(value, action);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return "ERROR";
    }

    public static String tryGetStringForTableValue(VALUE value, DC_ActiveObj action) {
        if (value == G_PROPS.NAME) {
            return action.getName();
        }
        if (value instanceof PARAMS) {

            PARAMS p = (PARAMS) value;

            switch (p) {
                case CLOSE_QUARTERS_DAMAGE_MOD:
                    return (getRange0(action));
                case DAMAGE_MOD:
                    return (getRange1(action));
                case LONG_REACH_DAMAGE_MOD:
                    return (getRange2(action));

                case DAMAGE_BONUS: //TODO ????
                    return ImageManager.getValueIconPath(p);
                case ATTACK:
                    //TODO getAttack
                    return
                     String.valueOf(MathMaster.applyPercent(action.getOwnerUnit().getIntParam(p),
                      action.getIntParam(PARAMS.ATTACK_MOD)));
                case BASE_DAMAGE:
                    return "Base";
                case DAMAGE:
                    return getDamageText(action);
                case COUNTER_MOD:
                case COUNTER_ATTACK_MOD:
                    return "Counter";
                case INSTANT_ATTACK_MOD:
                case INSTANT_DAMAGE_MOD:
                    return "Instant";
                case AOO_ATTACK_MOD:
                case AOO_DAMAGE_MOD:
                    return "Opportunity";
                case SIDE_DAMAGE_MOD:
                case SIDE_ATTACK_MOD:
                    return "To a Side";
                case DIAGONAL_DAMAGE_MOD:
                case DIAGONAL_ATTACK_MOD:
                    return "Diagonal";
                case FORCE_KNOCK_MOD:
                    return getForcePushDescription(action);
                case FORCE_PUSH_MOD:
                    return getForceKnockDescription(action);
                case FORCE_DAMAGE_MOD:
                    return getForceDamageDescription(action);
                case FORCE_MAX_STRENGTH_MOD:
                    return getForceMaxStrengthDescription(action);
                case BLEEDING_MOD:
                    return getBleedDescription(action);
                case ARMOR_PENETRATION:
                    return getArmorPenetrationDescription(action);
            }
            return action.getParam(p);
        }
        return null;

    }

    private static boolean isIgnoreIfZero(PARAMS p) {
        return p != PARAMS.FORCE && p != PARAMS.BASE_DAMAGE;
    }

    private static String getForceMaxStrengthDescription(DC_ActiveObj action) {
        return "Strength limit for Force: " +
         action.getIntParam(PARAMS.FORCE_MAX_STRENGTH_MOD);
    } //TODO rework

    private static String getForceDamageDescription(DC_ActiveObj action) {
        DAMAGE_TYPE type = DAMAGE_TYPE.BLUDGEONING;
        return "+ Inflicts " +
         action.getIntParam(PARAMS.FORCE_DAMAGE_MOD) + "% of Force as " +
         type.getName() +
         " damage";

    }


    private static String getArmorPenetrationDescription(DC_ActiveObj action) {
        return "";

    }

    public static void test(DC_ActiveObj action, PARAMS[] params) {
        for (PARAMS p : params) {
            String s = getStringForTableValue(p, action);
            if (s != null) {
                main.system.auxiliary.log.LogMaster.log(1, " " + s);
            }
        }
    }

    private static String getBleedDescription(DC_ActiveObj action) {

        return "Bleeding: Inflicts " + action.getIntParam(PARAMS.BLEEDING_MOD) + "% Bleed Counters";

    }

    private static String getForcePushDescription(DC_ActiveObj action) {
        int weight_max = ForceRule.getMaxWeightPush(action);
        String roll_info = "";
        return "Push: targets with less than " + weight_max +
         "lb weight" + roll_info;
    }

    private static String getForceKnockDescription(DC_ActiveObj action) {
        int weight_max = ForceRule.getMaxWeightKnock(action);
        int weight_min = ForceRule.getMinWeightKnock(action);
        String roll_info = "";
        return "Knockdown: never rolled vs > " + weight_max +
         "lb, always win vs < " + weight_min + "lb (or Interrupt)" + roll_info;
    }


    public static String getRange0(DC_ActiveObj action) {
        return "0";
    }

    public static String getRange1(DC_ActiveObj action) {
        return String.valueOf(action.getIntParam(PARAMS.RANGE)); //AUTO_ATTACK_?
    }

    public static String getRange2(DC_ActiveObj action) {
        return String.valueOf(action.getIntParam(PARAMS.RANGE) + 1) + "+";
    }

}
