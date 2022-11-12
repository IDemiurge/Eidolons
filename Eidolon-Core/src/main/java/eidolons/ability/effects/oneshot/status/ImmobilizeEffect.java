package eidolons.ability.effects.oneshot.status;

import main.ability.effects.common.AddStatusEffect;
import main.content.enums.entity.UnitEnums;

public class ImmobilizeEffect extends AddStatusEffect {

    public ImmobilizeEffect() {
        super(UnitEnums.STATUS.IMMOBILE.name());
    }

}
