package main.ability.effects.special;

import main.ability.effects.oneshot.special.AddStatusEffect;
import main.content.enums.entity.UnitEnums;

public class SilenceEffect extends AddStatusEffect {
    public SilenceEffect() {
        super(UnitEnums.STATUS.SILENCED.name());

    }

}
