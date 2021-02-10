package eidolons.game.battlecraft.rules.mechanics;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.ability.ignored.special.media.InfoTextEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.triggered.InterruptEffect;
import main.elements.conditions.*;
import main.elements.conditions.standard.OwnershipCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;

public class InterruptRule {

    private static final String FOC_FORMULA = "-35";
    private static final String AP_FORMULA = "-35";

    public static Effect getEffect() {
        return new Effects(
                new InfoTextEffect(KEYS.SOURCE, " has been interrupted!", true),
                new ModifyValueEffect(PARAMS.C_ATB, MOD.MODIFY_BY_PERCENT,
         AP_FORMULA), new ModifyValueEffect(PARAMS.C_FOCUS, MOD.MODIFY_BY_PERCENT,
         FOC_FORMULA));
    }

    public static Condition getConditions() {
        return new Conditions(
         new RefCondition(KEYS.EVENT_TARGET, KEYS.SOURCE, false),
         new NumericCondition("{EVENT_AMOUNT}",
          "{SOURCE_TOUGHNESS}*{SOURCE_INTERRUPT_DAMAGE}/100*{ACTIVE_INTERRUPT_DAMAGE}/100"));

    }

    public static Condition getConditionsAlert() {
        return new Conditions(new RefCondition(KEYS.EVENT_TARGET, KEYS.TARGET, false),
         new NotCondition(new OwnershipCondition(KEYS.TARGET, KEYS.SOURCE)),
         new NumericCondition("{EVENT_AMOUNT}", "1"));

    }

    public static void interrupt(Unit target) {
        getEffect().apply(Ref.getSelfTargetingRefCopy(target));
        new InterruptEffect().apply(Ref.getSelfTargetingRefCopy(target));
    }

}
