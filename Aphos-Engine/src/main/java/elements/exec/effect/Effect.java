package elements.exec.effect;

import content.LinkedStringMap;
import elements.exec.EntityRef;
import elements.exec.effect.framework.EffectResult;
import framework.data.TypeData;

import java.util.HashMap;

/**
 * Created by Alexander on 8/21/2023
 */
public abstract class Effect {
    protected TypeData data;
    protected EffectResult effectResult;

    public TypeData getData() {
        return data;
    }

    public String[] getArgNames() {
        return new String[0];
    }
    public void setData(TypeData effectData) {
        this.data = effectData;
    }

    public EffectResult apply(EntityRef ref) {
        effectResult = new EffectResult(new LinkedStringMap<>());
        applyThis(ref);
        return effectResult;
    }
    protected abstract void applyThis(EntityRef ref);

    public EffectResult getResult() {
        return effectResult;
    }

}
