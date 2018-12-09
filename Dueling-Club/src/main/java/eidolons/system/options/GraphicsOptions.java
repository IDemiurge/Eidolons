package eidolons.system.options;

import eidolons.system.graphics.RESOLUTION;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;


public class GraphicsOptions extends Options<GRAPHIC_OPTION, GRAPHIC_OPTION> {
    static {
        //set default values
        //BRIGHTNESS.set
        //caching
    }

    static {
        GRAPHIC_OPTION.RESOLUTION.setDefaultValue(RESOLUTION._1920x1080);
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
        AMBIENCE_DENSITY(30, 0, 100),
        VIDEO(true),
        AMBIENCE_MOVE_SUPPORTED(true),
        FRAMERATE(60, 20, 80),
        RESOLUTION(eidolons.system.graphics.RESOLUTION.values()),
//        PERFORMANCE_BOOST(PERFORMANCE_BOOST_LEVEL.values()),
        VSYNC(true),
        SHADOW_MAP_OFF(false),
        UI_VFX(false),
        FONT_SIZE(100, 50, 200),
        UI_SCALE(100, 50, 200),
        SPRITE_CACHE_ON(false){
            @Override
            public boolean isDevOnly() {
                return true;
            }
        },
        BRIGHTNESS(80, 20, 100){
            @Override
            public boolean isDevOnly() {
                return true;
            }
        }
        , ADD_SHARDS_ALWAYS(true), ADD_SHARDS_NEVER(false)
        , ADDITIVE_LIGHT(false)
        ,  SIDE_LIGHT_OFF(false),
        UI_ATLAS(true), FULL_ATLAS(false);
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
