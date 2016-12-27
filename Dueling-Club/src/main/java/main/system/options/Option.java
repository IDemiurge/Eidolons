package main.system.options;


import main.swing.components.menus.OptionsPanel;

public class Option implements OptionsPanel.OPTION {
    Object[] options;
    Integer min;
    Integer max;
    Boolean defaultValue;

    public Option(Integer min, Integer max) {

    }

    public Option(Object[] options) {

    }

    public Option(Boolean defaultValue) {

    }

    public Object[] getOptions() {
        return options;
    }

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

    public Boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Boolean isExclusive() {
        return null;
    }
}
