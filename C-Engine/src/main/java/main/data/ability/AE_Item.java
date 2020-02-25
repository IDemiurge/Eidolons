package main.data.ability;

import main.system.auxiliary.StringMaster;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Represents either a
 * :: public constructor of a relevant class
 * :: primitive value
 * :: enum const
 */
public class AE_Item implements Comparable<AE_Item> {

    public static final String SEPARATOR = StringMaster.getPairSeparator();
    private List<Argument> argList;
    private String name;
    private Argument arg;
    private Class<?> concreteClass;
    private boolean container;
    private Constructor<?> constr;

    public AE_Item(String name, Argument mappedArg, List<Argument> argList2,
                   Class<?> CLASS, boolean container) {
        this.container = container;
        this.arg = mappedArg;
        this.name = name;
        this.argList = argList2;
        this.concreteClass = CLASS;
    }

    public AE_Item(String name, Argument mappedArg, List<Argument> argList2,
                   Class<?> CLASS, boolean contains, Constructor<?> constr) {
        this(name, mappedArg, argList2, CLASS, contains);
        this.setConstr(constr);
    }

    @Override
    public String toString() {
        if (constr == null) {
            return name;
        }
        StringBuilder args = new StringBuilder(": ");
        try {
            for (String str : constr
             .getAnnotation(AE_ConstrArgs.class).argNames()) {
                args.append(str).append(", ");
            }
            args = new StringBuilder(args.substring(0, args.length() - 2));
        } catch (Exception e) {
            if (constr.getParameterTypes().length < 1) {
                return name;
            }
            for (Class<?> type : constr.getParameterTypes()) {
                args.append(type.getSimpleName()).append(", ");
            }
            args = new StringBuilder(args.substring(0, args.length() - 2));
        }

        String string = name + args;
        return string;
    }

    public List<Argument> getArgList() {
        return argList;
    }

    public String getName() {
        return name;
    }

    public String getEmptyName() {
        return "(Empty)" + getName();
    }

    public Argument getArg() {
        return arg;
    }

    public Class<?> getConcreteClass() {
        return concreteClass;
    }

    public boolean isContainer() {
        return container;
    }

    public boolean isPrimitive() {
        return arg.isPrimitive();
    }

    public boolean isENUM() {
        return arg.isENUM();
    }

    public Constructor<?> getConstr() {
        return constr;
    }

    public void setConstr(Constructor<?> constr) {
        this.constr = constr;
    }

    @Override
    public int compareTo(AE_Item item) {
        if (toString().equals(item.toString())) {
            return 0;
        }

        return toString().compareTo(item.toString());
    }
}
