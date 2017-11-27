package main.data.ability.construct;

import main.content.ContentManager;
import main.content.values.parameters.PARAMETER;
import main.content.values.parameters.Param;
import main.content.values.properties.PROPERTY;
import main.content.values.properties.Prop;
import main.data.ability.AE_Item;
import main.data.ability.ARGS;
import main.data.ability.Argument;
import main.data.ability.Mapper;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.math.Formula;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class Construct {

    private String className;
    private List<Construct> constructs;
    // private List<Object> args;
    private boolean primitive = false;
    private String content;
    private boolean ENUM;
    private Class CLASS;
    private Argument arg;
    private boolean genericPrimitive;
    private   String xml;

    public Construct(String className, List<Construct> constructs, String xml) {

        LogMaster.log(LogMaster.CONSTRUCTION_DEBUG, "constructed: "
                + className + " : " + constructs);
        this.className = className;
        this.constructs = constructs;
        this.CLASS = Mapper.getMappedClass(className);
        this.setArg(Mapper.translateToArg(CLASS));
        this.xml=xml;
    }

    /**
     * Primitive
     *
     * @param textContent
     */
    public Construct(String className, String textContent) {
        LogMaster.log(LogMaster.CONSTRUCTION_DEBUG, "constructed primitive: "
                + className + " : " + textContent);
        this.className = className;
        this.primitive = true;
        this.content = textContent;
        this.CLASS = getPrimitiveClass(className);
        this.setArg(getPrimitiveArg(className));

    }

    public Construct(String className, String textContent, boolean ENUM) {
        LogMaster.log(LogMaster.CONSTRUCTION_DEBUG, "constructed enum: "
                + className + " : " + textContent);
        this.className = className;
        this.content = textContent;
        this.ENUM = ENUM;
        this.CLASS = Mapper.getMappedClass(className);
    }

    public Construct(String text, boolean genericPrimitive) {
        this.genericPrimitive = genericPrimitive;
        this.content = text;
    }

    public String toXml() {
        return xml;
    }
    private ARGS getPrimitiveArg(String className) {
        switch (className) {
            case "FORMULA": {
                return ARGS.FORMULA;
            }
            case "BOOLEAN": {
                return ARGS.BOOLEAN;
            }
            case "INTEGER": {
                return ARGS.INTEGER;
            }
            case "STRING": {
                return ARGS.STRING;
            }
        }
        return null;
    }

    private Class getPrimitiveClass(String className) {
        switch (className) {
            case "FORMULA": {
                return Formula.class;
            }
            case "BOOLEAN": {
                return Boolean.class;
            }
            case "INTEGER": {
                return Integer.class;
            }
            case "STRING": {
                return String.class;
            }
        }
        return null;
    }

    private Object constructEnum() {

        if (!CLASS.isEnum()) {
            if (CLASS == PARAMETER.class || CLASS == Param.class) {

                return ContentManager.getPARAM(content);
            }
            if (CLASS == PROPERTY.class || CLASS == Prop.class) {
                return ContentManager.getPROP(content);
            }

            throw new RuntimeException();

        }
        Object obj = new EnumMaster().getEnum(content, CLASS.getEnumConstants());
        if (obj == null) {
            LogMaster.log(LogMaster.CONSTRUCTION_DEBUG, "*** constructEnum:"
                    + content);
        }
        return obj;
    }

    private Object constructPrimitive() {
        LogMaster.log(LogMaster.CONSTRUCTION_DEBUG,
                "...constructing primitive: " + className + " : " + content);

        Object object;
        switch (className) {
            case "FORMULA": {
                object = new Formula(content);
                break;
            }
            case "BOOLEAN": {
                object = new Boolean(content);
                break;
            }
            case "INTEGER": {
                object = new Integer(content);
                break;
            }
            case "STRING": {
                object = new String(content);
                break;
            }
            // case "PARAMETER": {
            // object = new Param(content);
            // break;
            // }
            // case "PROPERTY": {
            // object = new Prop(content);
            // break;
            // }
            default: {
                object = content;

            }
        }
        if (object == null) {

            LogMaster.log(LogMaster.CONSTRUCTION_DEBUG,
                    "*** PRIMITIVE CONSTRUCTION FAILED! " + className);
        }
        LogMaster.log(LogMaster.CONSTRUCTION_DEBUG,
                ">>> PRIMITIVE CONSTRUCT: " + object);
        return object;
    }

    @SuppressWarnings("rawtypes")
    public Object construct() {
        if (primitive) {
            return constructPrimitive();
        }
        if (ENUM) {
            Object obj = constructEnum();
            return obj;
        }

        Object result = null;
        ArrayList<Object> args = new ArrayList<>();
        for (Construct construct : constructs) {
            Object object = construct.construct();
            if (object instanceof Reconstructable) {
                ((Reconstructable) object).setConstruct(construct);
            }
            if (object == null) {
                LogMaster.log(1, "null object in construct: " + construct);
            }
            args.add(object);
        }

        ArrayList<Argument> argList = new ArrayList<>();
        for (Construct constr : constructs) {
            if (constr.getArg() == null) {
                constr.setArg(Mapper.translateToArg(Mapper.getMappedClass(constr.getClassName())));
            }
            argList.add(constr.getArg());
        }
        AE_Item item = Mapper.getItem(className, argList);
        if (item != null) {
            if (!item.isContainer()) {
                if (item.getConstr() != null) {

                    Constructor<?> constructor = item.getConstr();
                    result = invokeConstructor(constructor, args);
                    return result;
                }
            }
        }
        Class<?> CLASS = Mapper.getMappedClass(className);

        if (CLASS == null) {
            LogMaster.log(LogMaster.CONSTRUCTION_DEBUG,
                    "no class found for construct: " + className);
            return null;
        }
        for (Constructor<?> constructor : CLASS.getConstructors()) {
            if (!checkConstructor(constructor, constructs)) {
                continue;
            }
            result = invokeConstructor(constructor, args);

        }

        return result;
    }

    private Object invokeConstructor(Constructor<?> constructor, ArrayList<Object> args) {
        Object result = null;
        try {
            LogMaster.log(LogMaster.CONSTRUCTION_DEBUG, "=> constructing: " + className
                    + "'s constructor " + constructor + " with args: " + args);
            result = constructor.newInstance(args.toArray());
            LogMaster.log(LogMaster.CONSTRUCTION_DEBUG,
                    "==> instantiation successful!  ");

        } catch (Exception e) {
            e.printStackTrace();
            LogMaster.log(LogMaster.CONSTRUCTION_DEBUG,
                    "*** construction failed *** " + className + "'s constructor " + constructor
                            + " with args: " + args);

        }
        return result;
    }

    private boolean checkConstructor(Constructor<?> constructor, List<Construct> constructs) {
        Class<?>[] argTypes = constructor.getParameterTypes();
        if (argTypes.length != constructs.size()) {
            return false;
        }
        int i = 0;
        for (Construct c : constructs) {
            if (Mapper.translateToCoreClass(c.getClassName()) != argTypes[i]) {
                return false;
            }
            i++;
            // c.getCLASS() !=
        }

        return true;
    }

    @Override
    public String toString() {
        return className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<Construct> getConstructs() {
        return constructs;
    }

    public void setConstructs(List<Construct> constructs) {
        this.constructs = constructs;
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isENUM() {
        return ENUM;
    }

    public void setENUM(boolean eNUM) {
        ENUM = eNUM;
    }

    public Class getCLASS() {
        return CLASS;
    }

    public void setCLASS(Class cLASS) {
        CLASS = cLASS;
    }

    public Argument getArg() {
        return arg;
    }

    public void setArg(Argument arg) {
        this.arg = arg;
    }

}
