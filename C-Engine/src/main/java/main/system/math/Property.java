package main.system.math;

import main.content.ContentValsManager;
import main.entity.Ref;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.math.FunctionManager.FUNCTIONS;

public class Property extends DynamicValue {
    private String string;
    private boolean autovar;
    private boolean strict;

    public Property(String obj_ref, String value_ref) {
        this.value_string = value_ref;
        this.obj_string = obj_ref;
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

    public String getFullString() {
        if (autovar) {
            return FunctionManager.evaluateFunction(ref, FUNCTIONS.AUTOVAR,
             string);
        }
        if (obj_string == null) {
            if (fullString == null) {
                return ref.getValue(value_string);
            }
            return fullString;
        }

        entity = ref.getObj(obj_string);
        if (entity == null) {
            entity = ref.getType(obj_string);
        }

        if (entity == null) {
            if (obj_string.equalsIgnoreCase("EVENT")) {
                Ref REF = ref.getEvent().getRef();
                if (!value_string.contains(Strings.FORMULA_REF_SEPARATOR)
                 && REF.getValue(value_string) != null) {
                    return REF.getValue(value_string);
                } else {
                    return new Property(
                     StringMaster.wrapInCurlyBraces(value_string))
                     .getStr(REF);
                }

            }
            return ref.getValue(value_string);
        }
        String str = (strict) ? entity.getProperty(
         ContentValsManager.getPROP(value_string), base) : entity
         .getProperty(ContentValsManager.getPROP(value_string), base);

        return str;
    }

    public String getStr(Ref ref) {
        setRef(ref);
        String str = getFullString();
        if (str == null) {
            return "";
        }
        return str.trim();
    }

    @Override
    public String toString() {
        if (fullString != null) {
            return fullString;
        }
        return obj_string + value_string;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

}
