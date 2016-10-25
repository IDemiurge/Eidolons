package main.rules.generic;

import main.content.CONTENT_CONSTS.AI_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.ValuePages;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.entity.Entity;
import main.entity.obj.ActiveObj;
import main.entity.obj.DC_HeroObj;
import main.system.auxiliary.EnumMaster;

public class UnitAnalyzer {

    public static boolean checkIsCaster(DC_HeroObj unit) {
        for (PARAMETER mastery : ValuePages.MASTERIES_MAGIC_SCHOOLS) {
            if (unit.getIntParam(mastery) > 0)
                return true;
        }
        return false;
    }

    public static boolean checkOffhand(DC_HeroObj unit) {
        if (unit.getActiveWeapon(true) == null)
            return false;
        if (unit.getMainWeapon() == null)
            return true;
        if (unit.getMainWeapon().isTwoHanded())
            return false;
        return true;

        // if (checkDualNaturalWeapons(unit))
        // return true;
        // if (unit.getSecondWeapon() == null)
        // return false;
        // if (unit.getSecondWeapon().isRanged() ||
        // unit.getSecondWeapon().isMagical())
        // return false;
        // return (unit.getSecondWeapon().isWeapon());
    }

    public static boolean isFlying(Entity u) {
        if (u.checkProperty(G_PROPS.STANDARD_PASSIVES, "Flying"))
            return true;
        if (u.checkProperty(G_PROPS.CLASSIFICATIONS, "Flying"))
            return true;
        return false;
    }

    public static boolean isDoor(Entity u) {
        if (u.checkProperty(G_PROPS.BF_OBJECT_TYPE, "Door"))
            return true;
        if (u.checkProperty(G_PROPS.BF_OBJECT_GROUP, "Door"))
            return true;
        return false;
    }

    public static boolean isWall(Entity u) {
        if (u.checkProperty(G_PROPS.BF_OBJECT_TYPE, "Wall"))
            return true;
        if (u.checkProperty(G_PROPS.BF_OBJECT_TAGS, "Wall"))
            return true;
        if (u.checkProperty(G_PROPS.BF_OBJECT_GROUP, "Wall"))
            return true;
        return false;
    }

    public static boolean checkDualWielding(DC_HeroObj unit) {
        if (unit.getSecondWeapon() == null || unit.getMainWeapon() == null)
            return false;
        if (unit.getMainWeapon().isRanged() || unit.getMainWeapon().isMagical())
            return false;
        if (unit.getSecondWeapon().isRanged() || unit.getSecondWeapon().isMagical())
            return false;
        return (unit.getSecondWeapon().isWeapon());

    }

    public static boolean checkDualNaturalWeapons(DC_HeroObj unit) {
        if (unit.getMainWeapon() == null && unit.getSecondWeapon() == null)
            if (unit.getNaturalWeapon(false) != null && unit.getNaturalWeapon(true) != null)
                return true;
        return false;
    }

    public static boolean hasFocusBlockedActions(DC_HeroObj unit) {
        int focus = unit.getIntParam(PARAMS.C_FOCUS);

        for (ActiveObj a : unit.getActives()) {
            if (a.getIntParam(PARAMS.FOC_REQ, false) > focus)
                return true;
        }

        return false;
    }

    public static boolean isMeleePreferred(Entity unit) {
        AI_TYPE ai_type = new EnumMaster<AI_TYPE>().retrieveEnumConst(AI_TYPE.class, unit
                .getProperty(PROPS.AI_TYPE));
        if (ai_type == AI_TYPE.CASTER)
            return false;
        if (ai_type == AI_TYPE.ARCHER)
            return false;
        return true;
    }

    public static boolean isOffensePreferred(Entity unit) {
        AI_TYPE ai_type = new EnumMaster<AI_TYPE>().retrieveEnumConst(AI_TYPE.class, unit
                .getProperty(PROPS.AI_TYPE));
        if (ai_type == AI_TYPE.TANK)
            return false;
        return true;
    }

}
