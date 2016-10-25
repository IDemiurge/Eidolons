package main.ability.effects;

import main.ability.Abilities;
import main.entity.Ref;

public interface EffectManager {

    boolean checkNotResisted(Effect effect);

    void setEffectRefs(Abilities abilities);

    void setEffectRefs(Abilities abilities, Ref ref);

}
