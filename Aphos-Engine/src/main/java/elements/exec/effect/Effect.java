package elements.exec.effect;

import elements.exec.EntityRef;
import framework.data.TypeData;

/**
 * Created by Alexander on 8/21/2023
 */
public abstract class Effect {
    protected TypeData data;

    public TypeData getData() {
        return data;
    }

    public String[] getArgNames() {
        return new String[0];
    }
    public void setData(TypeData effectData) {
        this.data = effectData;
    }

    public abstract boolean apply(EntityRef ref);
}
