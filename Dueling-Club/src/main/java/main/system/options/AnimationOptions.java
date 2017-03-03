package main.system.options;

import main.swing.components.menus.OptionsPanel;
import main.system.options.AnimationOptions.ANIMATION_OPTION;

public class AnimationOptions extends Options<ANIMATION_OPTION> {
    @Override
    public Class<Boolean> getValueClass(Enum option) {
        // TODO Auto-generated method stub
        return null;
    }

    // AI_PARAMS,
    public enum ANIMATION_OPTION implements OptionsPanel.OPTION {
        INFO_LEVEL(1), SPEED(100), PHASE_TIME(500), OFFSET_FOR_OVERLAP(true),;

        Object[] options;
        Integer min;
        Integer max;
        Object defaultValue;

        ANIMATION_OPTION() {

        }

        ANIMATION_OPTION(Integer defaultValue, Integer min, Integer max) {
            this.defaultValue = defaultValue;
            this.max = max;
            this.min = min;
        }

        ANIMATION_OPTION(Object[] options) {
            this.options = options;
        }

        ANIMATION_OPTION(Object defaultValue) {
            this.defaultValue = defaultValue;
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

        public Object getDefaultValue() {
            return defaultValue;
        }

        @Override
        public Boolean isExclusive() {
            return null;
        }
    }

}
