package main.entity.handlers.bf.unit;

import main.content.ContentManager;
import main.content.VALUE;
import main.content.ValuePages;
import main.content.values.parameters.PARAMETER;
import main.entity.obj.unit.Unit;
import main.entity.handlers.EntityLogger;
import main.entity.handlers.EntityMaster;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 4/5/2017.
 */
public class UnitLogger extends EntityLogger<Unit> {
    public UnitLogger(Unit entity, EntityMaster<Unit> entityMaster) {
        super(entity, entityMaster);
    }

    public void logVals(List<VALUE> vals) {
        vals.forEach(p -> logParam(p));

    }
    // FOR DEBUG
    public void logCoreParamPercentages() {
        logVals(   Arrays.stream(ValuePages.UNIT_DYNAMIC_PARAMETERS_CORE_CURRENT).map(
         (PARAMETER p) -> ContentManager.getPercentageParam(p)).
         collect(Collectors.toList()));

    }
        public void logCoreParams() {
       logVals( Arrays.asList(ValuePages.UNIT_DYNAMIC_PARAMETERS_CORE_CURRENT));

    }

    private void logParam(VALUE p) {
        main.system.auxiliary.log.LogMaster.log(1,
         getEntity().getName() + "'s " + p + " = " + getValue(p)+"\n");
    }

}
