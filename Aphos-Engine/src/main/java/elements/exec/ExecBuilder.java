package elements.exec;

import elements.stats.ActionProp;
import framework.entity.sub.UnitAction;

/**
 * Created by Alexander on 8/22/2023
 */
public class ExecBuilder {

    public static Executable initExecutable(UnitAction unitAction) {
        String data = unitAction.getString(ActionProp.Exec_data);
        ActionExecutable executable = new ActionExecutable();
        // executable.setTargeting(targeting);
        // executable.addEffect(targeting, effect);
//TODO
        return null;
    }
}
