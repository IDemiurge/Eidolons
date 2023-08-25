package elements.exec.effect.framework;

import elements.exec.effect.Effect;
import elements.exec.effect.ModifyStatEffect;
import elements.exec.effect.attack.DamageAttackEffect;

import java.util.function.Supplier;

/**
 * Created by Alexander on 8/24/2023
 */
public enum EffectTemplate {
    MODIFY(() -> new ModifyStatEffect()),
    EFFECT(()-> null),
    ATTACK(()-> new DamageAttackEffect()),

    ;
    public final Supplier<Effect> supplier;

    EffectTemplate(Supplier<Effect> supplier) {
        this.supplier = supplier;
    }
}
