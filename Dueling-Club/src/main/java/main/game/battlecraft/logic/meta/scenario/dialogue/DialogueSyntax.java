package main.game.battlecraft.logic.meta.scenario.dialogue;

import main.ability.Abilities;
import main.ability.ActiveAbility;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.common.ModifyValueEffect;
import main.ability.effects.containers.AbilityEffect;
import main.elements.conditions.Condition;
import main.elements.targeting.FixedTargeting;
import main.elements.targeting.Targeting;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/30/2017.
 */
public class DialogueSyntax {
    // That's it![mods][[reqs]][[[script]]]
    // named scripts?
    // [trust:-5;esteem:10]
    public static final String PARAM_MOD = "[";
    public static final String REQS = "[[";
    public static final String SCRIPT = "[[[";
    public static final String PARAM_MOD_CLOSE = "]";
    public static final String REQS_CLOSE = "]]";
    public static final String SCRIPT_CLOSE = "]]]";
    public static final String item_separator = ";";
    public static final String pair_separator = ":";

    public static Abilities getAbilities(String text) {
        if (!text.contains(PARAM_MOD))
            return null;
        String parsedPart = text.substring(text.indexOf(PARAM_MOD),
         text.indexOf(PARAM_MOD_CLOSE));
        //specify KEY
        Targeting t = new FixedTargeting();
        Effects paramModEffects = getParamModEffects(parsedPart);

        if (!paramModEffects.getEffects().isEmpty()) {
            return new Abilities(new ActiveAbility(t, paramModEffects));
        }
        return new Abilities(new ActiveAbility(t, new AbilityEffect(parsedPart)));
    }

    private static Effects getParamModEffects(String text) {
        Effects e = new Effects();
        for (String substring : StringMaster.open(text, item_separator)) {
            //TODO permanent for non-dynamic? base?
            MOD mod = MOD.MODIFY_BY_CONST;
            String param = text.split(pair_separator)[0];
            String formula = text.split(pair_separator)[1];
            ModifyValueEffect ef = new ModifyValueEffect(param, mod, formula);
//            ef.setBase()
            e.add(ef);
        }

        return e;
    }

    public static String getRawText(String text) {
        if (text.contains(PARAM_MOD)) {
            try {
                return text.substring(0, text.indexOf(PARAM_MOD));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return text;
    }

    public static Condition getConditions(String text) {
        if (!text.contains(PARAM_MOD))
            return null;
        return null;
    }

    public static String getScript(String text) {
        if (!text.contains(PARAM_MOD))
            return null;
        return null;
    }


}
