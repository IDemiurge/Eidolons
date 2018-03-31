package eidolons.ability.effects.attachment;

import eidolons.ability.effects.DC_Effect;
import main.ability.effects.AttachmentEffect;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.data.ability.construct.ConstructionManager;
import main.entity.Ref;

public abstract class MultiEffect extends DC_Effect implements AttachmentEffect {
    protected Effect effect;

    @Override
    public boolean apply(Ref ref) {
        if (isReconstruct()) {
            if (effect.getConstruct() != null) {
                try {
                    effect = ((Effect) ConstructionManager.construct(effect.getConstruct()));
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        }
        return super.apply(ref);
    }

    public void addEffect(Effect effect) {
        if (this.effect == null) {
            this.effect = effect;
        }
        if (this.effect instanceof Effects) {
            ((Effects) this.effect).add(effect);
        } else {
            this.effect = new Effects(this.effect, effect);
        }

    }

}
