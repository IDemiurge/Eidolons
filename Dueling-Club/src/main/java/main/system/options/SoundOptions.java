package main.system.options;

import main.system.options.SoundOptions.SOUND_OPTION;

public class SoundOptions extends Options<SOUND_OPTION,SOUND_OPTION> {
    @Override
    protected Class<? extends SOUND_OPTION> getOptionClass() {
        return SOUND_OPTION.class;
    }



    public enum SOUND_OPTION implements  Options.OPTION {
        ALL_OFF, VOICE_OFF,
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
