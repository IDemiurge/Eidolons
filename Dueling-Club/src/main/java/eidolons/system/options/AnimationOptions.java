package eidolons.system.options;

import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;

import static eidolons.system.options.AnimationOptions.ANIMATION_OPTION.*;

public class AnimationOptions extends Options<ANIMATION_OPTION, ANIMATION_OPTION> {
    @Override
    protected Class getOptionClass() {
        return ANIMATION_OPTION.class;
    }

static {
    WAIT_FOR_ANIM.setDevOnly(true);
    PARALLEL_DRAWING.setDevOnly(true);


    PRECAST_ANIMATIONS.setDevOnly(true);
    AFTER_EFFECTS_ANIMATIONS.setDevOnly(true);
    CAST_ANIMATIONS.setDevOnly(true);

}
    // AI_PARAMS,
    public enum ANIMATION_OPTION implements Options.OPTION {
        WEAPON_3D_ANIMS_OFF(false),
        ENEMY_3D_ANIMS_OFF(false),
        BLOOD_ANIMS_OFF(false),

        WAIT_FOR_ANIM(false),
        MIN_ANIM_WAIT_TIME(150, 100, 500),
        MIN_ANIM_WAIT_TIME_COMBAT(300, 150, 1250),

        MAX_ANIM_WAIT_TIME(1000, 0, 3000),
        MAX_ANIM_WAIT_TIME_COMBAT(1500, 0, 4500),
        PARALLEL_DRAWING(false),
        SPEED(350, 65, 1000),
        //        INFO_LEVEL(1, 0, 1),
//        PHASE_TIME(500, 0, 1000),
//        OFFSET_FOR_OVERLAP(true),
        PRECAST_ANIMATIONS(true),
        CAST_ANIMATIONS(true),
        AFTER_EFFECTS_ANIMATIONS(false),
//        DEATH_ANIM(true),
//        MOVE_ANIM(true),
//        MELEE_ANIMS(true),

        HIT_ANIM_DISPLACEMENT(true),
        FLOAT_TEXT_DURATION_MOD(100, 25, 500),
//        FLOAT_TEXT_SIZE_MOD(100, 50, 300)
        ;

        Boolean exclusive;
        Object[] options;
        Integer min;
        Integer max;
        Object defaultValue;
        private boolean devOnly;

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

        @Override
        public boolean isDevOnly() {
            return devOnly;
        }

        public void setDevOnly(boolean devOnly) {
            this.devOnly = devOnly;
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
