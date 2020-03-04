package main.level_editor.metadata.settings;


import eidolons.system.options.Options;

public class LE_Options extends Options<LE_Options.EDITOR_OPTIONS, LE_Options.EDITOR_OPTIONS> {



    @Override
    protected Class<? extends EDITOR_OPTIONS> getOptionClass() {
        return EDITOR_OPTIONS.class;
    }

    public enum EDITOR_OPTIONS implements eidolons.system.options.Options.OPTION {
//        ui
PALETTE_SCALE(25, 150, 50),

//        controls
        zoom,

//        view
        real_view_enabled,

        colored_layers,




        ;

        EDITOR_OPTIONS() {
        }

        EDITOR_OPTIONS(Integer min, Integer max, Object defaultValue) {
            this.min = min;
            this.max = max;
            this.defaultValue = defaultValue;
        }

        private Boolean exclusive;
        private Integer min;
        private Integer max;
        private Object[] options;
        private Object defaultValue;

        @Override
        public Integer getMin() {
            return min;
        }

        @Override
        public Integer getMax() {
            return max;
        }

        @Override
        public Object[] getOptions() {
            return options;
        }

        @Override
        public String getName() {
            return name();
        }

        @Override
        public boolean isHidden() {
            return false;
        }

        @Override
        public boolean isDevOnly() {
            return false;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }

        @Override
        public Boolean isExclusive() {
            return null;
        }
    }
}
