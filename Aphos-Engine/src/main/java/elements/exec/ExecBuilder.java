package elements.exec;

import elements.stats.ActionProp;
import framework.entity.sub.UnitAction;

import static elements.exec.preset.ExecPresets.presets;

/**
 * Created by Alexander on 8/22/2023
 */
public class ExecBuilder {

    public static Executable initExecutable(UnitAction unitAction) {
        String data = unitAction.get(ActionProp.Exec_data).toString();
        if (data.isEmpty())
            data = unitAction.getName();
        if (!data.contains("XML")){
            return getPresetExecutable(data);
        }
        // ActionExecutable executable = new ActionExecutable();
        // executable.setTargeting(targeting);
        // executable.addEffect(targeting, effect);
        //TODO xml construction
        return null;
    }

    private static Executable getPresetExecutable(String data) {
        return presets.get(data);
    }
}
