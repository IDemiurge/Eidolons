package eidolons.game.battlecraft.rules.magic;

import eidolons.ability.effects.common.LightEmittingEffect;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import main.content.enums.GenericEnums;
import main.entity.Ref;
import main.system.threading.WaitMaster;

public class SpellIlluminationRule {

    private static final int range = 10;
    private int delay = 500;

    public void spellResolves(DC_ActiveObj spell) {
        Unit caster = spell.getOwnerObj();
        Boolean circular = true;
        // VisionManager.getSpectrumCoordinates(range, 2, 0, caster, false,
        // caster.getFacing());
        // distance reductionBoolean circular = true;
        if (spell.checkBool(GenericEnums.STD_BOOLS.SPECTRUM_LIGHT)) {
            circular = false;
        }
        int value = spell.getIntParam(PARAMS.SPELL_DIFFICULTY);
        LightEmittingEffect effect = new LightEmittingEffect(("" + value), circular);
        effect.apply(new Ref(caster));

        caster.getGame().getManager().reset();
        WaitMaster.WAIT(delay);
        caster.getGame().getVisionMaster().refresh();
        caster.getGame().getBattleField().getGrid().refresh();

        // reduce for distance
    }

}
