package eidolons.game.battlecraft.rules.counter.anew;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.unit.Unit;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;

public class ExertionRule {

    private static final PROPERTY PROP = G_PROPS.STATUS;
    private static final String EXERTION = "Exertion";
    UnitEnums.COUNTER[] exertCounters = {
            UnitEnums.COUNTER.Adrenaline,
            UnitEnums.COUNTER.Energy,
            UnitEnums.COUNTER.Rage,
            UnitEnums.COUNTER.Channeling,
    };

    public static boolean checkExertionType(UnitEnums.COUNTER counter, Unit source) {
        return source.checkProperty(PROP, getMark(counter));
    }

    public static boolean checkExertionTypeAllowed(UnitEnums.COUNTER counter, Unit source) {
        if (checkExertionType(counter, source))
            return true;

        return !source.checkProperty(G_PROPS.MODE, EXERTION);
    }

    public static void activated(UnitEnums.COUNTER counter, Unit source) {
        source.addProperty(G_PROPS.MODE, EXERTION);
        source.addProperty(PROP, getMark(counter));
        //TODO add Fatigue immediately?
    }

    public static boolean isActionBlocked(DC_ActiveObj action) {
        Unit source = (Unit) action.getOwnerObj();

        if (source.checkProperty(G_PROPS.MODE, EXERTION)) {
            return false;
        }
        switch (action.getActionGroup()) {
            case MOVE:
            case TURN:
                return !(checkExertionType(UnitEnums.COUNTER.Energy, source)
                        || checkExertionType(UnitEnums.COUNTER.Adrenaline, source));
            case ATTACK:
                return !(checkExertionType(UnitEnums.COUNTER.Rage, source)
                        || checkExertionType(UnitEnums.COUNTER.Adrenaline, source));
            case SPELL:
                return !checkExertionType(UnitEnums.COUNTER.Channeling, source);
        }

        return true;
    }


    private static String getMark(UnitEnums.COUNTER counter) {
        return "Exertion: " + counter.getName();
    }
}
