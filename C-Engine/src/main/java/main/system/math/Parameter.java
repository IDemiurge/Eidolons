package main.system.math;

import main.content.ContentManager;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.ability.construct.VariableManager.AUTOVAR;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;

public class Parameter extends DynamicValue {
    private PARAMETER param;

    public Parameter(String obj_ref, String value) {
        this(obj_ref, value, false);
    }

    public Parameter(String obj_ref, String value, boolean base) {
        this.value_string = value;
        this.obj_string = obj_ref;
        this.base = base;
    }

    public Parameter(String obj_ref) {
        super(obj_ref);
    }

    public Integer getInt(Ref ref) {
        setRef(ref);
        checkRefReplacement();
        return getInt();
    }

    public int getInt() {
        if (obj_string == null) {
            Integer result = null;
            try {
                result = ref.getInteger(value_string);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result == null) {
                obj_string = KEYS.SOURCE.toString();
            } else {
                return result;
            }
        }
        // else
        if (obj_string.equalsIgnoreCase(Ref.KEYS.EVENT.name())) {
            if (ref.getEvent().getRef().getInteger(value_string) != null) {
                return Integer.valueOf(ref.getEvent().getRef().getInteger(value_string));
            }
            return new Formula(value_string).wrapObjRef().getInt(ref.getEvent().getRef());
        }
        Integer id = ref.getId(obj_string);

        LogMaster.log(0, "Queried Obj: " + obj_string);
        if (id==null )
            entity = ref.getType(obj_string);
        else {
            entity = game.getObjectById(id);
            if (entity == null) {
                entity = game.getTypeById(id);

            }
        }
        if (entity == null) {
            LogMaster.log(0, obj_string + "'s " + value_string
             + " - Queried Obj not found; ref: " + ref);
            return 0;
        }
        if (value_string.equalsIgnoreCase(StringMaster.MASTERY)) {
            return (int) AUTOVAR.MASTERY.evaluate(entity, null);
            // return FunctionManager.FUNCTIONS.AV.evaluate(ref, value_ref, );
        } else {
            param = (ContentManager.getPARAM(value_string));

            if (getParam() == null) {
                param = (ContentManager.getPARAM(value_string + " Mastery"));
            }
            if (getParam() == null) {
                return entity.getCounter(value_string);
            }
        }

        LogMaster.log(0, "Retrieving " + getParam() + " from " +

         obj_string);
        int x;

        x = Integer.valueOf((entity).getIntParam(getParam(), ref.isBase()));

        LogMaster.log(0, "Parameter value evaluated: " + x + " for obj "
                + entity.getProperty(G_PROPS.NAME));
        return x;

    }

    public PARAMETER getParam() {
        return param;
    }

}
