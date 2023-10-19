package elements.exec.effect.framework;

import elements.exec.effect.Effect;
import elements.exec.effect.KillEffect;
import elements.exec.effect.ModifyStatEffect;
import elements.exec.effect.attack.DamageAttackEffect;
import elements.exec.effect.counter.BashEffect;

import java.util.function.Supplier;

/**
 * Created by Alexander on 8/24/2023
 */
public enum EffectTemplate {
    MODIFY(() -> new ModifyStatEffect()),
    ATTACK(()-> new DamageAttackEffect()),
    BASH(()-> new BashEffect()),
    KILL(()-> new KillEffect()),
    EFFECT(()-> null),

    ;
    public final Supplier<Effect> supplier;

    EffectTemplate(Supplier<Effect> supplier) {
        this.supplier = supplier;
    }
}
