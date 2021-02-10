package eidolons.system.options;


public class Option implements Options.OPTION {
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
