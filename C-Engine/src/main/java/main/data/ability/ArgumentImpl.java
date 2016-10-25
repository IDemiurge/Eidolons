package main.data.ability;

import main.system.auxiliary.StringMaster;

import java.util.List;

public class ArgumentImpl implements Argument {
    private AE_ELEMENT_TYPE ELEMENT_TYPE;
    private String name;
    private boolean primitive = false;
    private List<Class<?>> ENUM_CLASSES;
    private Class<?> c;

    ArgumentImpl(Class<?> c, String name) {
        this.name = StringMaster.getWellFormattedString(name);
        this.c = c;
        this.ELEMENT_TYPE = AE_ELEMENT_TYPE.ENUM_CHOOSING;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Argument) {
            Argument argument = (Argument) obj;
            return argument.name().equalsIgnoreCase(name());
        }
        return false;
    }

    @Override
    public Class<?> getCoreClass() {

        return c;
    }

    @Override
    public AE_ELEMENT_TYPE getElementType() {
        return ELEMENT_TYPE;
    }

    @Override
    public String getEmptyName() {
        return "<<< " + name + " >>>"; // TODO empty node HANDLED!
    }

    @Override
    public boolean isPrimitive() {
        return primitive;
    }

    @Override
    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }

    @Override
    public boolean isENUM() {
        return (ELEMENT_TYPE == AE_ELEMENT_TYPE.ENUM_CHOOSING);
    }

    @Override
    public Object[] getEnumList() {

        return c.getEnumConstants();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isContainer() {
        // TODO Auto-generated method stub
        return false;
    }

}
