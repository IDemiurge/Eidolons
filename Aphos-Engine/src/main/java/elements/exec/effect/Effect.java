package elements.exec.effect;

import elements.exec.EntityRef;
import system.log.result.EffectResult;
import framework.data.TypeData;
import system.log.SysLog;

/**
 * Created by Alexander on 8/21/2023
 */
public abstract class Effect {
    protected TypeData data;
    protected EffectResult effectResult;

    public TypeData getData() {
        return data;
    }

    public final String[] getArgNames() {
        return getArgs().split("\\|");
    }

    public String getArgs() {
        return "";
    }

    public void setData(TypeData effectData) {
        this.data = effectData;
    }

    public EffectResult apply(EntityRef ref) {
        effectResult = new EffectResult();
        SysLog.printOut(SysLog.LogChannel.Main, "Applying", this, "on", ref.getTarget());
        applyThis(ref);
        SysLog.printOut(SysLog.LogChannel.Main, "Applied", this, "on", ref.getTarget(), effectResult);
        return effectResult;
    }

    protected abstract void applyThis(EntityRef ref);

    public Effect setValue(int index, Object value) {
        return setValue(getArgNames()[index], value);
    }

    public Effect setValue(String key, Object value) {
        if (getData() == null) {
            data = new TypeData();
        }
        getData().set(key, value);
        return this;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String toString() {
        if (data == null)
            return getClass().getSimpleName();
        return getClass().getSimpleName() + " with " + data.toSimpleString();
    }

    public EffectResult getResult() {
        return effectResult;
    }

}
