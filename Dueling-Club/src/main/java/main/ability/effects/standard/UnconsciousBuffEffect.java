package main.ability.effects.standard;

import main.ability.effects.AddBuffEffect;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.special.AddStatusEffect;
import main.content.CONTENT_CONSTS.STATUS;
import main.entity.Ref;
import main.game.ai.tools.target.EffectMaster;

public class UnconsciousBuffEffect extends AddBuffEffect {
    private static final String BUFF_TYPE_NAME = "Unconscious";
    private static final String PARAM_MOD_EFFECTS_STRING = "" + "defense([set]0);"
            + "willpower([mod]-50);" + "";

    public UnconsciousBuffEffect() {
        super(BUFF_TYPE_NAME, getEffects());
    }

    private static Effect getEffects() {
        Effects effects = EffectMaster.initParamModEffects(PARAM_MOD_EFFECTS_STRING, new Ref());
        effects.add(new AddStatusEffect(STATUS.PRONE));
        effects.add(new AddStatusEffect(STATUS.IMMOBILE));
        effects.add(new AddStatusEffect(STATUS.UNCONSCIOUS));
        return effects;
    }

}
