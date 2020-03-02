package main.level_editor.metadata.settings;


import eidolons.system.options.Options;

public class LE_Options extends Options<LE_Options.EDITOR_OPTIONS, LE_Options.EDITOR_OPTIONS> {



    @Override
    protected Class<? extends EDITOR_OPTIONS> getOptionClass() {
        return EDITOR_OPTIONS.class;
    }

    public enum EDITOR_OPTIONS implements eidolons.system.options.Options.OPTION {
        PALETTE_SCALE,
        ;

        @Override
        public Integer getMin() {
            return null;
        }

        @Override
        public Integer getMax() {
            return null;
        }

        @Override
        public Object getDefaultValue() {
            return null;
        }

        @Override
        public Boolean isExclusive() {
            return null;
        }

        @Override
        public Object[] getOptions() {
            return new Object[0];
        }
    }
}
