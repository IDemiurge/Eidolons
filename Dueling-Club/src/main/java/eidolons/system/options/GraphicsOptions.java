package eidolons.system.options;

import eidolons.system.graphics.RESOLUTION;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;


public class GraphicsOptions extends Options<GRAPHIC_OPTION, GRAPHIC_OPTION> {


    static {
        GRAPHIC_OPTION.RESOLUTION.setDefaultValue(RESOLUTION._1920x1080);
    }

    @Override
    protected Class getOptionClass() {
        return GRAPHIC_OPTION.class;
    }

    public enum PERFORMANCE_BOOST_LEVEL {
        NONE,
        DISABLE_ALL_VISUALS,
        DISABLE_ALL_BACKGROUND_OPERATIONS,
        DISABLE_ALL,
        ;
    }

    public enum GRAPHIC_OPTION implements Options.OPTION {
        SPRITES_OFF(false),
        REDUCED_SPRITES(false),
        GRID_SPRITES_OFF(false),
        UNIT_SPRITES_OFF(false),
        UI_SPRITES_OFF(false),
        LARGE_SPRITES_OFF(false),
        BACKGROUND_SPRITES_OFF(true),
        SHADOW_MAP_OFF(false),

        FULLSCREEN(true),
        //        GAMMA(),
        ALT_ASSET_LOAD(false),
        AMBIENCE_VFX(true),
        AMBIENCE_DENSITY(10, 0, 50),
        VIDEO(true),
        AMBIENCE_MOVE_SUPPORTED(true) {
            @Override
            public boolean isDevOnly() {
                return true;
            }
        },
        PERFORMANCE_BOOST(20, 0, 100),
        SHARD_VFX(true),
        COLOR_TEXT_LOG(true),
        RESOLUTION(eidolons.system.graphics.RESOLUTION.values()),
        //        PERFORMANCE_BOOST(PERFORMANCE_BOOST_LEVEL.values()),
        VSYNC(true),
        GRID_VFX(true),
        UI_VFX(false),
        SPECIAL_EFFECTS(70, 15, 200),

            FONT_SIZE(100, 50, 200) {
            @Override
            public boolean isDevOnly() {
                return true;
            }
        },
        UI_SCALE(100, 50, 200) {
            @Override
            public boolean isDevOnly() {
                return true;
            }
        },
        SPRITE_CACHE_ON(false) {
            @Override
            public boolean isDevOnly() {
                return true;
            }
        },
        BRIGHTNESS(80, 20, 100) {
            @Override
            public boolean isDevOnly() {
                return true;
            }
        }, ADDITIVE_LIGHT(false) {
            @Override
            public boolean isDevOnly() {
                return true;
            }
        },
        LIGHT_OVERLAYS_OFF(false),
        UI_ATLAS(true) {
            @Override
            public boolean isDevOnly() {
                return true;
            }
        },
        FULL_ATLAS(false) {
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
