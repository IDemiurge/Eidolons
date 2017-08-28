package main.system.options;

import main.system.options.GraphicsOptions.GRAPHIC_OPTION;

public class GraphicsOptions extends Options<GRAPHIC_OPTION,GRAPHIC_OPTION>{
static {
        //set default values


     //caching

}

    @Override
    protected Class getOptionClass() {
        return GRAPHIC_OPTION.class;
    }

    public enum GRAPHIC_OPTION implements Options.OPTION {
        AMBIENCE(false),
        PARTICLE_EFFECTS(false),
        ANIMATED_UI(false),
        SHADOWMAP(false),
        AMBIENCE_MOVE_SUPPORTED(false), OPTIMIZATION_ON(true),

        SPRITE_CACHE_ON(true), OUTLINES(false);
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

        GRAPHIC_OPTION(Integer min, Integer max, Object... options) {
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
