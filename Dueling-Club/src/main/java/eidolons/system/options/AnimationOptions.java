package eidolons.system.options;

import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;

public class AnimationOptions extends Options<ANIMATION_OPTION, ANIMATION_OPTION> {
    @Override
    protected Class getOptionClass() {
        return ANIMATION_OPTION.class;
    }


    // AI_PARAMS,
    public enum ANIMATION_OPTION implements Options.OPTION {
        WAIT_FOR_ANIM(false),
        MAX_ANIM_WAIT_TIME(1000, 0, 3000),
        PARALLEL_DRAWING(false),
        SPEED(100, 1, 1500),
        TEXT_DURATION(100, 0, 300),
        //        INFO_LEVEL(1, 0, 1),
//        PHASE_TIME(500, 0, 1000),
//        OFFSET_FOR_OVERLAP(true),
        PRECAST_ANIMATIONS(true),
        CAST_ANIMATIONS(true),
        AFTER_EFFECTS_ANIMATIONS(false),
//        DEATH_ANIM(true),
//        MOVE_ANIM(true),
//        MELEE_ANIMS(true),

        HIT_ANIM_DISPLACEMENT(false);

        Boolean exclusive;
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

        ANIMATION_OPTION(boolean defaultValue) {
            this.exclusive = defaultValue;
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