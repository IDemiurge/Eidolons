package eidolons.ability.ignored.oneshot.rule;

import eidolons.ability.effects.DC_Effect;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.core.master.EffectMaster;
import main.ability.effects.Effects;
import main.ability.effects.OneshotEffect;
import main.content.CONTENT_CONSTS2.INJURY;
import main.content.CONTENT_CONSTS2.INJURY_TYPE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

//Untested
public class InjuryEffect extends DC_Effect implements OneshotEffect {

    private INJURY template;
    private boolean old;
    private INJURY_TYPE type;

    public InjuryEffect(INJURY_TYPE type) {
        this();
        this.type = type;
    }

    public InjuryEffect() {
        boolean random = true;
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
        // preCheck applicable
        getTarget().addProperty(PROPS.INJURIES,
         StringMaster.format(injury.toString()));
        Effects effects = EffectMaster.initParamModEffects(injury.getModString(), ref);
        // TODO ++ PROPS
        if (mod != 100) {
            effects.appendFormulaByMod(mod);
        }
        effects.apply(ref);
        return true;
    }
}
