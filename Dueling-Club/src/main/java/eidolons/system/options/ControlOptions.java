package eidolons.system.options;

import eidolons.system.options.ControlOptions.CONTROL_OPTION;

/**
 * Created by JustMe on 5/23/2018.
 */
public class ControlOptions extends  Options<CONTROL_OPTION,CONTROL_OPTION> {

    @Override
    protected Class<? extends CONTROL_OPTION> getOptionClass() {
        return CONTROL_OPTION.class;
    }

    public enum CONTROL_OPTION implements Options.OPTION {
        ALT_MODE_ON(false),
//        NUMPAD_CONTROLS_ON(false),
        UNLIMITED_ZOOM(false),
        ZOOM_STEP( 5, 1, 20),
//        DRAG_OFF(false),
//        PAN_CAMERA_ON_EDGES(false),
        AUTO_CENTER_CAMERA_ON_HERO(true),
        CAMERA_FOLLOW_CURSOR_DISTANCE(0, 0, 1000),
        CENTER_CAMERA_DISTANCE_MOD(100, 50, 200),;
//        BINDING_PROFILE, ;
        private Boolean exclusive;
        private Integer min;
        private Integer max;
        private Object[] options;
        private Object defaultValue;

        CONTROL_OPTION(Boolean exclusive) {
            this.exclusive = exclusive;
            defaultValue = exclusive;
        }

        CONTROL_OPTION(Object... options) {
            this.options = options;
        }

        CONTROL_OPTION(Integer defaultValue, Integer min, Integer max) {
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;

        }

        CONTROL_OPTION(Boolean exclusive, Integer min, Integer max, Object... options) {
            this.exclusive = exclusive;
            this.min = min;
            this.max = max;
            this.options = options;
        }

        @Override
        public Integer getMin() {
            return min;
        }

        @Override
        public Integer getMax() {
            return max;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public Boolean isExclusive() {
            return exclusive;
        }

        @Override
        public Object[] getOptions() {
            return options;
        }
    }
}
