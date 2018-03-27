package main.system.options;

import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.test.frontend.RESOLUTION;

public class GraphicsOptions extends Options<GRAPHIC_OPTION,GRAPHIC_OPTION>{
static {
        //set default values


     //caching

}
static {
    GRAPHIC_OPTION.RESOLUTION.setDefaultValue(RESOLUTION._1680x1050);
}
    @Override
    protected Class getOptionClass() {
        return GRAPHIC_OPTION.class;
    }

    public enum GRAPHIC_OPTION implements Options.OPTION {

    FULLSCREEN(false),
//        GAMMA(),

        AMBIENCE(false),
        VIDEO(false),
        AMBIENCE_MOVE_SUPPORTED(false),
//        SPRITE_CACHE_ON(true),

        AUTO_CAMERA(true),
        FRAMERATE(60, 20, 80),
        RESOLUTION(main.test.frontend.RESOLUTION.values()), 
        ZOOM_STEP(5, 1, 20), SPRITE_CACHE_ON(false), VSYNC(true);
        private Boolean exclusive;
        private Integer min;
        private Integer max;
        private Object[] options;
        private Object defaultValue;

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        GRAPHIC_OPTION(Boolean exclusive) {
            this.exclusive = exclusive;
            defaultValue = exclusive;
        }

        GRAPHIC_OPTION(Object... options) {
            this.options = options;
        }

        GRAPHIC_OPTION(Integer defaultValue,Integer min, Integer max ) {
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;

        }

        GRAPHIC_OPTION(Boolean exclusive, Integer min, Integer max, Object... options) {
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
