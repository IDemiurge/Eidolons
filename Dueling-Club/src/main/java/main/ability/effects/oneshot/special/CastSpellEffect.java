package main.ability.effects.oneshot.special;

import main.ability.effects.oneshot.MicroEffect;
import main.data.ability.OmittedConstructor;
import main.entity.Ref.KEYS;
import main.entity.obj.top.DC_ActiveObj;
import main.system.ai.logic.target.ReasonMaster;
import main.system.auxiliary.ListMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.util.List;

public class CastSpellEffect extends MicroEffect {
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
                e.printStackTrace();
            }
        }
        List<String> reasons = ReasonMaster.getReasonsCannotActivate(active,
                ref);
        if (!ListMaster.contains(reasons, "cooldown", false))
            if (!active.canBeActivated(ref, true)) {
                game.getLogManager().log(
                        active.getOwnerObj().getName()
                                + "'s channeling has failed ("
                                + active.getName() + ")");
                SoundMaster.playStandardSound(STD_SOUNDS.FAIL);
                return false;
            }

        if (isForceTargeting()) {
            // ref.setTarget(null);
            if (!active.selectTarget(ref)) {
                game.getLogManager().log(
                        active.getOwnerObj().getName()
                                + "'s channeling has failed ("
                                + active.getName() + ")");
                SoundMaster.playStandardSound(STD_SOUNDS.SPELL_CANCELLED);
                return false;
            }
        }
        boolean activate = active.activate(ref);
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
