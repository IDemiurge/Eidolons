package main.system.options;

import main.system.options.SoundOptions.SOUND_OPTION;

public class SoundOptions extends Options<SOUND_OPTION> {
    @Override
    public Class<Boolean> getValueClass(Enum option) {
        SOUND_OPTION soundOption = (SOUND_OPTION) option;
        switch (soundOption) {
            case ALL_OFF:
            case VOICE_OFF:
                return Boolean.class;

        }
        return null;
    }

    public enum SOUND_OPTION {
        ALL_OFF, VOICE_OFF,
    }
}
