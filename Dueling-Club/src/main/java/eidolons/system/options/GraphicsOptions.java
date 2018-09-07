package eidolons.system.options;

import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.graphics.RESOLUTION;

public class GraphicsOptions extends Options<GRAPHIC_OPTION, GRAPHIC_OPTION> {
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

    public enum PERFORMANCE_BOOST_LEVEL  {
        NONE,
        DISABLE_ALL_VISUALS,
        DISABLE_ALL_BACKGROUND_OPERATIONS,
        DISABLE_ALL,;
    }

    public enum GRAPHIC_OPTION implements Options.OPTION {

        FULLSCREEN(true),
//        GAMMA(),

        AMBIENCE(true),
        AMBIENCE_DENSITY(50, 0, 100),
        VIDEO(true),
        AMBIENCE_MOVE_SUPPORTED(true),

        FRAMERATE(60, 20, 80),
        RESOLUTION(eidolons.system.graphics.RESOLUTION.values()),
//        PERFORMANCE_BOOST(PERFORMANCE_BOOST_LEVEL.values()),
        VSYNC(true),
        SHADOW_MAP_OFF(false),
        UI_EMITTERS(false),
        SPRITE_CACHE_ON(false){
            @Override
            public boolean isDevOnly() {
                return true;
            }
        },
        ;
        private Boolean exclusive;
        private Integer min;
        private Integer max;
        private Object[] options;
        private Object defaultValue;

        GRAPHIC_OPTION(Boolean exclusive) {
            this.exclusive = exclusive;
            defaultValue = exclusive;
        }

        GRAPHIC_OPTION(Object... options) {
            this.options = options;
        }

        GRAPHIC_OPTION(Integer defaultValue, Integer min, Integer max) {
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
