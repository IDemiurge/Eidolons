package eidolons.system.options;

import eidolons.system.audio.MusicMaster;
import eidolons.system.audio.MusicMaster.MUSIC_VARIANT;
import eidolons.system.options.SoundOptions.SOUND_OPTION;

public class SoundOptions extends Options<SOUND_OPTION, SOUND_OPTION> {
    static {
        SOUND_OPTION.MUSIC_VARIANT.setDefaultValue(MUSIC_VARIANT.EIDOLONS_SCORE);
    }

    @Override
    protected Class<? extends SOUND_OPTION> getOptionClass() {
        return SOUND_OPTION.class;
    }

    public enum SOUND_OPTION implements Options.OPTION {
        SOUNDS_OFF(false),
        VOICE_OFF(false),
        FOOTSTEPS_OFF(false),
        MUSIC_OFF(false),
        MASTER_VOLUME(70, 0, 100),
        MUSIC_VOLUME(45, 0, 100),
        VOICE_VOLUME(75, 0, 100),
        AMBIENCE_VOLUME(40, 0, 100),
        //        EFFECT_VOLUME(100, 0, 100),
        MUSIC_VARIANT(MusicMaster.MUSIC_VARIANT.values()) {
            @Override
            public boolean isDevOnly() {
                return true;
            }
        };
        private Boolean exclusive;
        private Integer min;
        private Integer max;
        private Object[] options;
        private Object defaultValue;

        SOUND_OPTION(Boolean exclusive) {
            this.exclusive = exclusive;
            defaultValue = exclusive;
        }

        SOUND_OPTION(Object... options) {
            this.options = options;
        }

        SOUND_OPTION(Integer defaultValue, Integer min, Integer max) {
            this.min = min;
            this.max = max;
            this.defaultValue = defaultValue;

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
