package elements.exec.effect;

import content.LinkedStringMap;
import elements.exec.EntityRef;
import elements.exec.effect.framework.EffectResult;
import framework.data.TypeData;
import system.log.SysLog;

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
        SysLog.printOut(SysLog.LogChannel.Main, "Applying", getName(), "on", ref);
        applyThis(ref);
        SysLog.printOut(SysLog.LogChannel.Main, "Applied", getName(), "on", ref);
        return effectResult;
    }

    protected abstract void applyThis(EntityRef ref);


    public String getName() {
        return getClass().getSimpleName();
    }
    @Override
    public String toString() {
        return getClass().getSimpleName()+ " with data: " + data;
    }

    public EffectResult getResult() {
        return effectResult;
    }

}
