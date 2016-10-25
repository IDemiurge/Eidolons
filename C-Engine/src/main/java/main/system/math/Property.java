package main.system.math;

import main.content.ContentManager;
import main.content.properties.PROPERTY;
import main.entity.Ref;
import main.system.auxiliary.StringMaster;
import main.system.math.FunctionManager.FUNCTIONS;

public class Property extends DynamicValue {
    PROPERTY property;

    private String string;

    private boolean autovar;

    private boolean strict;

    public Property(String obj_ref, String value_ref) {
        this.value_ref = value_ref;
        this.obj_ref = obj_ref;
        base = false;
    }

    public Property(String obj_ref, String value_ref, boolean base) {
        super(value_ref, obj_ref, base);

    }

    public Property(String string, boolean autovar) {
        super();
        this.string = string;
        this.autovar = autovar;
    }

    public Property(boolean s, String value_ref) {
        this(value_ref);
        this.strict = s;
    }

    public Property(String value_ref) {
        super(value_ref);

    }

    public String getStr() {
        if (autovar) {
            return FunctionManager.evaluateFunction(ref, FUNCTIONS.AUTOVAR,
                    string);
        }
        if (obj_ref == null) {
            if (str == null)
                return ref.getValue(value_ref);
            return str;
        }

        entity = ref.getObj(obj_ref);
        if (entity == null)
            entity = ref.getType(obj_ref);

        if (entity == null) {
            if (obj_ref.equalsIgnoreCase("EVENT")) {
                Ref REF = ref.getEvent().getRef();
                if (!value_ref.contains(StringMaster.FORMULA_REF_SEPARATOR)
                        && REF.getValue(value_ref) != null) {
                    return REF.getValue(value_ref);
                } else {
                    return new Property(
                            StringMaster.wrapInCurlyBraces(value_ref))
                            .getStr(REF);
                }

            }
            return ref.getValue(value_ref);
        }
        String str = (strict) ? entity.getProperty(
                ContentManager.getPROP(value_ref, true), base) : entity
                .getProperty(ContentManager.getPROP(value_ref), base);

        return str;
    }

    public String getStr(Ref ref) {
        setRef(ref);
        String str = getStr();
        if (str == null)
            return "";
        return str.trim();
    }

    @Override
    public String toString() {
        if (str != null)
            return str;
        return obj_ref + value_ref;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

}
