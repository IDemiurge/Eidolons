package main.swing.components.menus;

public interface OPTION {
    Integer getMin();

    Integer getMax();

    Object getDefaultValue();

    Boolean isExclusive();

    Object[] getOptions();

}