package eidolons.game.battlecraft.logic.meta.scenario.dialogue;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.ability.effects.containers.AbilityEffect;
import main.ability.Abilities;
import main.ability.ActiveAbility;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.elements.conditions.Condition;
import main.elements.targeting.FixedTargeting;
import main.elements.targeting.Targeting;
import main.system.auxiliary.ContainerUtils;

import java.util.regex.Pattern;

/**
 * Created by JustMe on 5/30/2017.
 */
public class DialogueSyntax {
    // That's it![mods][[reqs]][[[script]]]
    // named scripts?
    // [trust:-5;esteem:10]
    public static final String META_DATA_SEPARATOR= "||";
    public static final String PARAM_MOD = "[";
    public static final String PARAM_MOD_CLOSE = "]";
    //    public static final String REQS = "[[";
//    public static final String REQS_CLOSE = "]]";
    public static final String SCRIPT_QUOTE = Pattern.quote("[[");
    public static final String SCRIPT=("[[");
    public static final String SCRIPT_CLOSE = "]]";
    public static final String item_separator = ";";
    public static final String pair_separator = ":";
    private static final String TIME = "time=";
    private static final String TIME_CLOSE = ".";

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
        for (String substring : ContainerUtils.open(text, item_separator)) {
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


    public static Condition getConditions(String text) {
        return null;
    }

    public static String getScriptPart(String text) {
        int from = text.lastIndexOf(SCRIPT_QUOTE);
        if (from < 0) {
            return "";
        }
        int to = text.indexOf(SCRIPT_CLOSE);
        if (to < 1) {
            return "";
        }
        return text.substring(from+2, to);
    }


    public static Integer getTime(String text) {
        int from = text.lastIndexOf(TIME);
        if (from < 0) {
            return null ;
        }
        int to = text.indexOf(TIME_CLOSE);
        if (to < 1) {
            return null ;
        }
        return Integer.valueOf(text.substring(from +TIME.length() , to ));
    }
}
