package main.data.ability;

public interface Argument {

    Class<?> getCoreClass();

    AE_ELEMENT_TYPE getElementType();

    String getEmptyName();

    boolean isPrimitive();

    void setPrimitive(boolean primitive);

    boolean isENUM();

    Object[] getEnumList();

    String name();

    boolean isContainer();

}
