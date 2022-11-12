package eidolons.ability.effects.containers;

import eidolons.ability.effects.DC_Effect;
import main.ability.effects.Effect;
import main.system.text.TextParser;

import java.util.function.Supplier;

public class LoggedEffect extends DC_Effect {
    Effect effect;
    String message;
    Supplier<String> supplier;

    public LoggedEffect(Supplier<String> supplier ,Effect effect)
     {
        this.effect = effect;
        this.supplier = supplier;
    }
    public LoggedEffect( String message,Effect effect ) {
        this.effect = effect;
        this.message = message;
    }
//    public LoggedEffect(Effect effect, Supplier<String> supplier) {
//        this.effect = effect;
//
//    }
    @Override
    public boolean applyThis() {
        if (supplier != null) {
            message = supplier.get();
        }
        message =  TextParser.parse(message, ref);
        getGame().getLogManager().log(message);
        return effect.apply(ref);
    }
}
