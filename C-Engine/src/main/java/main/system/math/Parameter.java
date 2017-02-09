package main.system.math;

import main.content.ContentManager;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.data.ability.construct.VariableManager.AUTOVAR;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.StringMaster;

public class Parameter extends DynamicValue {
    private PARAMETER param;

    public Parameter(String obj_ref, String value) {
        this(obj_ref, value, false);
    }

    public Parameter(String obj_ref, String value, boolean base) {
        this.value_ref = value;
        this.obj_ref = obj_ref;
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
        if (obj_ref == null) {
            Integer result = null;
            try {
                result = ref.getInteger(value_ref);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result == null) {
                obj_ref = KEYS.SOURCE.toString();
            } else {
                return result;
            }
        }
        // else
        if (obj_ref.equalsIgnoreCase(Ref.KEYS.EVENT.name())) {
            if (ref.getEvent().getRef().getInteger(value_ref) != null) {
                return Integer.valueOf(ref.getEvent().getRef().getInteger(value_ref));
            }
            return new Formula(value_ref).wrapObjRef().getInt(ref.getEvent().getRef());
        }
        Integer id = ref.getId(obj_ref);

        main.system.auxiliary.LogMaster.log(0, "Queried Obj: " + obj_ref);
        entity = game.getObjectById(id);
        if (entity == null) {
            entity = game.getTypeById(id);
            if (entity == null) {
                main.system.auxiliary.LogMaster.log(0, obj_ref + "'s " + value_ref
                        + " - Queried Obj not found; ref: " + ref);
                return 0;
            }
        }
        if (value_ref.equalsIgnoreCase(StringMaster.MASTERY)) {
            return (int) AUTOVAR.MASTERY.evaluate(entity, null);
            // return FunctionManager.FUNCTIONS.AV.evaluate(ref, value_ref, );
        } else {
            param = (ContentManager.getPARAM(value_ref));

            if (getParam() == null) {
                param = (ContentManager.getPARAM(value_ref + " Mastery"));
            }
            if (getParam() == null) {
                return entity.getCounter(value_ref);
            }
        }

        main.system.auxiliary.LogMaster.log(0, "Retrieving " + getParam() + " from " +

                obj_ref);
        int x;

        x = Integer.valueOf((entity).getIntParam(getParam(), ref.isBase()));

        main.system.auxiliary.LogMaster.log(0, "Parameter value evaluated: " + x + " for obj "
                + entity.getProperty(G_PROPS.NAME));
        return x;

    }

    public PARAMETER getParam() {
        return param;
    }

}
