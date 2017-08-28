package main.system.options;

import main.system.options.AnimationOptions.ANIMATION_OPTION;

public class AnimationOptions extends Options<ANIMATION_OPTION,ANIMATION_OPTION> {
    @Override
    protected Class getOptionClass() {
        return ANIMATION_OPTION.class;
    }


    // AI_PARAMS,
    public enum ANIMATION_OPTION implements Options.OPTION {
        WAIT_FOR_ANIM(false ),
        INFO_LEVEL(1, 0, 1), SPEED(100, 0, 100), PHASE_TIME(500, 0, 1000), OFFSET_FOR_OVERLAP(true),
        GENERATE_AFTER_EFFECTS(false),
          PARALLEL_DRAWING (true) ,
        MAX_ANIM_WAIT_TIME(1200, 0, 3500),
        ;

        boolean exclusive;
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

        ANIMATION_OPTION(boolean defaultValue) {this.exclusive = defaultValue;
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
            return exclusive;
        }
    }

}
