package main.ability.effects.special;

import main.ability.effects.oneshot.special.AddStatusEffect;
import main.content.enums.entity.UnitEnums;

public class ImmobilizeEffect extends AddStatusEffect {

    public ImmobilizeEffect() {
        super(UnitEnums.STATUS.IMMOBILE.name());
    }

}
