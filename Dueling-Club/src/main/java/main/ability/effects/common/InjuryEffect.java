package main.ability.effects.common;

import main.ability.effects.DC_Effect;
import main.ability.effects.Effects;
import main.content.CONTENT_CONSTS2.INJURY;
import main.content.CONTENT_CONSTS2.INJURY_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.system.ai.logic.target.EffectMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

public class InjuryEffect extends DC_Effect {

    private INJURY template;
    private boolean random;
    private boolean old;
    private INJURY_TYPE type;

    public InjuryEffect(INJURY_TYPE type) {
        this();
        this.type = type;
    }

    public InjuryEffect() {
        random = true;
    }

    public InjuryEffect(INJURY template, Boolean old) {
        this.template = template;
    }

    private INJURY getRandomInjury() {
        INJURY injury = new EnumMaster<INJURY>().getRandomEnumConst(INJURY.class);
        while (!(type == null || type == injury.getType())) {
            injury = new EnumMaster<INJURY>().getRandomEnumConst(INJURY.class);
        }
        return injury;
    }

    @Override
    public boolean applyThis() {
        int mod = 100 - getTarget().getIntParam(PARAMS.INJURY_RESISTANCE);
        if (mod <= 0) {
            return true;
        }
        INJURY injury;
        if (template != null) {
            injury = template;
        } else {
            injury = getRandomInjury();
        }
        // check applicable
        getTarget().addProperty(PROPS.INJURIES,
                StringMaster.getWellFormattedString(injury.toString()));
        Effects effects = EffectMaster.initParamModEffects(injury.getModString(), ref);
        // TODO ++ PROPS
        if (mod != 100) {
            effects.modifyFormula(mod);
        }
        effects.apply(ref);
        return true;
    }
}
