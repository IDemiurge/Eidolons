package eidolons.game.battlecraft.rules.counter.anew;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.unit.Unit;
import main.content.enums.entity.EffectEnums;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;

public class ExertionRule {

    private static final PROPERTY PROP = G_PROPS.STATUS;
    private static final String EXERTION = "Exertion";
    EffectEnums.COUNTER[] exertCounters = {
            EffectEnums.COUNTER.Adrenaline,
            EffectEnums.COUNTER.Energy,
            EffectEnums.COUNTER.Rage,
            EffectEnums.COUNTER.Channeling,
    };

    public static boolean checkExertionType(EffectEnums.COUNTER counter, Unit source) {
        return source.checkProperty(PROP, getMark(counter));
    }

    public static boolean checkExertionTypeAllowed(EffectEnums.COUNTER counter, Unit source) {
        if (checkExertionType(counter, source))
            return true;

        return !source.checkProperty(G_PROPS.MODE, EXERTION);
    }

    public static void activated(EffectEnums.COUNTER counter, Unit source) {
        source.addProperty(G_PROPS.MODE, EXERTION);
        source.addProperty(PROP, getMark(counter));
        //TODO add Fatigue immediately?
    }

    public static boolean isActionBlocked(ActiveObj action) {
        Unit source = (Unit) action.getOwnerObj();

        if (source.checkProperty(G_PROPS.MODE, EXERTION)) {
            return false;
        }
        switch (action.getActionGroup()) {
            case MOVE:
            case TURN:
                return !(checkExertionType(EffectEnums.COUNTER.Energy, source)
                        || checkExertionType(EffectEnums.COUNTER.Adrenaline, source));
            case ATTACK:
                return !(checkExertionType(EffectEnums.COUNTER.Rage, source)
                        || checkExertionType(EffectEnums.COUNTER.Adrenaline, source));
            case SPELL:
                return !checkExertionType(EffectEnums.COUNTER.Channeling, source);
        }

        return true;
    }


    private static String getMark(EffectEnums.COUNTER counter) {
        return "Exertion: " + counter.getName();
    }
}
