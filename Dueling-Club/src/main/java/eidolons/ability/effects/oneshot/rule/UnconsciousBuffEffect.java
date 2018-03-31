package eidolons.ability.effects.oneshot.rule;

import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.game.battlecraft.ai.tools.target.EffectFinder;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.common.AddStatusEffect;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref;

public class UnconsciousBuffEffect extends AddBuffEffect {
    private static final String BUFF_TYPE_NAME = "Unconscious";
    private static final String PARAM_MOD_EFFECTS_STRING = "" + "defense([set]0);"
     + "willpower([mod]-50);" + "";

    public UnconsciousBuffEffect() {
        super(BUFF_TYPE_NAME, getEffects());
    }

    private static Effect getEffects() {
        Effects effects = EffectFinder.initParamModEffects(PARAM_MOD_EFFECTS_STRING, new Ref());
        effects.add(new AddStatusEffect(UnitEnums.STATUS.PRONE));
        effects.add(new AddStatusEffect(UnitEnums.STATUS.IMMOBILE));
        effects.add(new AddStatusEffect(UnitEnums.STATUS.UNCONSCIOUS));
        return effects;
    }

}
