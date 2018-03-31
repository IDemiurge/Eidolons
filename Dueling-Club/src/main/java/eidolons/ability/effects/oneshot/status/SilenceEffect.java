package eidolons.ability.effects.oneshot.status;

import main.ability.effects.common.AddStatusEffect;
import main.content.enums.entity.UnitEnums;

public class SilenceEffect extends AddStatusEffect {
    public SilenceEffect() {
        super(UnitEnums.STATUS.SILENCED.name());

    }

}
