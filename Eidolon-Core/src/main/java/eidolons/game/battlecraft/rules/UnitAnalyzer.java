package eidolons.game.battlecraft.rules;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.content.values.ValuePages;
import eidolons.entity.unit.Unit;
import main.content.enums.entity.HeroEnums;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import main.entity.obj.IActiveObj;
import main.system.auxiliary.EnumMaster;

public class UnitAnalyzer {

    public static boolean checkIsCaster(Unit unit) {
        for (PARAMETER mastery : ValuePages.MASTERIES_MAGIC_SCHOOLS) {
            if (unit.getIntParam(mastery) > 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkOffhand(Unit unit) {
        return unit.getActiveWeapon(true) != null;
    }

    public static boolean isFlying(Entity u) {
        if (u.checkProperty(G_PROPS.STANDARD_PASSIVES, "Flying")) {
            return true;
        }
        return u.checkProperty(G_PROPS.CLASSIFICATIONS, "Flying");
    }

    public static boolean checkDualWielding(Unit unit) {
        if (unit.getOffhandWeapon() == null || unit.getMainWeapon() == null) {
            return false;
        }
        if (unit.getMainWeapon().isRanged() || unit.getMainWeapon().isMagical()) {
            return false;
        }
        if (unit.getOffhandWeapon().isRanged() || unit.getOffhandWeapon().isMagical()) {
            return false;
        }
        return (unit.getOffhandWeapon().isWeapon());

    }

    public static boolean checkDualNaturalWeapons(Unit unit) {
        if (unit.getMainWeapon() == null && unit.getOffhandWeapon() == null) {
            return unit.getNaturalWeapon(false) != null && unit.getNaturalWeapon(true) != null;
        }
        return false;
    }

    public static boolean hasFocusBlockedActions(Unit unit) {
        int focus = unit.getIntParam(PARAMS.C_FOCUS);

        for (IActiveObj a : unit.getActives()) {
            if (a.getIntParam(PARAMS.FOC_REQ, false) > focus) {
                return true;
            }
        }

        return false;
    }

    public static boolean isMeleePreferred(Entity unit) {
        AI_TYPE ai_type = new EnumMaster<AI_TYPE>().retrieveEnumConst(AI_TYPE.class, unit
         .getProperty(PROPS.AI_TYPE));
        if (ai_type == AiEnums.AI_TYPE.CASTER) {
            return false;
        }
        return ai_type != AI_TYPE.ARCHER;
    }

    public static boolean isOffensePreferred(Entity unit) {
        AI_TYPE ai_type = new EnumMaster<AI_TYPE>().retrieveEnumConst(AI_TYPE.class, unit
         .getProperty(PROPS.AI_TYPE));
        return ai_type != AI_TYPE.TANK;
    }

    public static boolean isFemale(Entity hero) {
        return hero.checkProperty(G_PROPS.GENDER, HeroEnums.GENDER.FEMALE + "");
    }
}
