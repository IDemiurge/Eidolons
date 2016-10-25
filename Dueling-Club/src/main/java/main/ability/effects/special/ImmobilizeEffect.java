package main.ability.effects.special;

import main.ability.effects.oneshot.special.AddStatusEffect;
import main.content.CONTENT_CONSTS;

public class ImmobilizeEffect extends AddStatusEffect {

    public ImmobilizeEffect() {
        super(CONTENT_CONSTS.STATUS.IMMOBILE.name());
    }

}
