package main.ability.effects.special;

import main.ability.effects.oneshot.special.AddStatusEffect;
import main.content.CONTENT_CONSTS.STATUS;

public class SilenceEffect extends AddStatusEffect {
    public SilenceEffect() {
        super(STATUS.SILENCED.name());

    }

}
