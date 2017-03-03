package main.game.ai.tools;

import main.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.entity.Entity;
import main.entity.obj.unit.Unit;
import main.game.ai.elements.generic.AiHandler;

import java.util.List;

public class ParamAnalyzer extends AiHandler {
    public ParamAnalyzer(AiHandler master) {
        super(master);
    }

    public static boolean isFatigued(Unit unit) {
        int buffLevel = unit.getGame().getRules().getStaminaRule()
                .getBuffLevel(unit);
        if (buffLevel < 0) {
            return false;
        }
        if (buffLevel == unit.getGame().getRules().getStaminaRule()
                .getMaxLevel()) {
            return false;
        }
        return true;
    }

    public static boolean isHazed(Unit unit) {
        int buffLevel = unit.getGame().getRules().getFocusRule()
                .getBuffLevel(unit);
        if (buffLevel < 0) {
            return false;
        }
        if (buffLevel == unit.getGame().getRules().getFocusRule().getMaxLevel()) {
            return false;
        }
        return true;
    }

    public static boolean isParamIgnored(Unit unit, PARAMETER p) {
        if (p == PARAMS.C_STAMINA) {
            return isStaminaIgnore(unit);
        }
        if (p == PARAMS.C_FOCUS) {
            return isStaminaIgnore(unit);
        }
        if (p == PARAMS.C_MORALE) {
            return isMoraleIgnore(unit);
        }
        return false;

    }

    public static boolean isMoraleIgnore(Unit unit) {
        return !unit.getGame().getRules().getMoraleRule().check(unit);
    }

    public static boolean isStaminaIgnore(Unit unit) {
        return !unit.getGame().getRules().getStaminaRule().check(unit);
    }

    public static boolean isFocusIgnore(Unit unit) {
        return !unit.getGame().getRules().getFocusRule().check(unit);
    }

    public static int getParamMinValue(PARAMETER param) {
        if (param instanceof PARAMS) {
            PARAMS p = (PARAMS) param;
            switch (p) {
                case C_FOCUS:
                    return 0;
                case C_STAMINA:
                    return 0;
                case C_ESSENCE:
                    return 0;

            }
        }
        return Integer.MIN_VALUE;
    }

    public static int getMaxParam(PARAMS p, List<Entity> linkedList) {
        int max = Integer.MIN_VALUE;
        for (Entity e : linkedList) {
            if (e.getIntParam(p) > max) {
                max = e.getIntParam(p);
            }
        }

        return max;

    }

    public static Entity getMaxParamUnit(PARAMS p, List<Entity> linkedList) {
        Entity unit = null;
        int max = Integer.MIN_VALUE;
        for (Entity e : linkedList) {
            if (e.getIntParam(p) > max) {
                max = e.getIntParam(p);
                unit = e;
            }
        }

        return unit;

    }

}
