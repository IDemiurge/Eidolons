package eidolons.ability.effects.oneshot.activation;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.ai.tools.target.ReasonMaster;
import eidolons.system.audio.DC_SoundMaster;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.data.ability.OmittedConstructor;
import main.entity.Ref.KEYS;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.util.List;

public class CastSpellEffect extends MicroEffect implements OneshotEffect {
    private DC_ActiveObj active;
    private KEYS key;
    private boolean forceTargeting;

    @OmittedConstructor
    public CastSpellEffect(DC_ActiveObj active) {
        this.active = active;
    }

    public CastSpellEffect(KEYS key) {
        this.key = key;
    }

    @Override
    public boolean applyThis() {
        if (active == null) {
            try {
                active = (DC_ActiveObj) ref.getObj(key);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        List<String> reasons = ReasonMaster.getReasonsCannotActivate(active,
         ref);
            if (!active.canBeActivated(ref, true)) {
                game.getLogManager().log(
                 active.getOwnerUnit().getName()
                  + "'s channeling has failed ("
                  + active.getName() + ")");
                DC_SoundMaster.playStandardSound(STD_SOUNDS.FAIL);
                return false;
            }

        if (isForceTargeting()) {
            ref.setTarget(null);
            if (!active.selectTarget(ref)) {
                game.getLogManager().log(
                 active.getOwnerUnit().getName()
                  + "'s channeling has been cancelled ("
                  + active.getName() + ")");
                DC_SoundMaster.playStandardSound(STD_SOUNDS.SPELL_CANCELLED);
                return false;
            }
        }
        boolean activate = active.activatedOn(ref);
        // if (activate)
        active.actionComplete();
        return activate;
    }

    public boolean isForceTargeting() {
        return forceTargeting;
    }

    public void setForceTargeting(boolean forceTargeting) {
        this.forceTargeting = forceTargeting;
    }

}
