package main.rules.mechanics;

import main.ability.effects.common.LightEmittingEffect;
import main.content.CONTENT_CONSTS.STD_BOOLS;
import main.content.PARAMS;
import main.entity.Ref;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.top.DC_ActiveObj;
import main.system.threading.WaitMaster;

public class SpellIlluminationRule {

    private static final int range = 10;
    private int delay = 500;

    public void spellResolves(DC_ActiveObj spell) {
        DC_HeroObj caster = spell.getOwnerObj();
        Boolean circular = true;
        // VisionManager.getSpectrumCoordinates(range, 2, 0, caster, false,
        // caster.getFacing());
        // distance reductionBoolean circular = true;
        if (spell.checkBool(STD_BOOLS.SPECTRUM_LIGHT)) {
            circular = false;
        }
        int value = spell.getIntParam(PARAMS.SPELL_DIFFICULTY);
        LightEmittingEffect effect = new LightEmittingEffect(("" + value), circular);
        effect.apply(new Ref(caster));

        caster.getGame().getManager().reset();
        WaitMaster.WAIT(delay);
        caster.getGame().getVisionManager().refresh();
        caster.getGame().getBattleField().getGrid().refresh();

        // reduce for distance
    }

}
