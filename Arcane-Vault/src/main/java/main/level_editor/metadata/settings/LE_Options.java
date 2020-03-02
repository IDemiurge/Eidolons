package main.level_editor.metadata.settings;


import eidolons.system.options.Options;

import static main.level_editor.metadata.settings.LE_Options.EDITOR_OPTIONS;

public class LE_Options extends Options<EDITOR_OPTIONS, EDITOR_OPTIONS> {



    @Override
    protected Class<? extends EDITOR_OPTIONS> getOptionClass() {
        return EDITOR_OPTIONS.class;
    }

    public enum EDITOR_OPTIONS implements OPTION {
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
