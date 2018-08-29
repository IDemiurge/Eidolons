package eidolons.game.module.dungeoncrawl.generator.pregeneration;

import eidolons.game.module.dungeoncrawl.generator.pregeneration.PregeneratorData.PREGENERATOR_VALUES;
import eidolons.system.options.Options;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.system.data.DataUnit;

/**
 * Created by JustMe on 8/18/2018.
 */
public class PregeneratorData extends DataUnit<PREGENERATOR_VALUES> {
    SUBLEVEL_TYPE[] sublevelTypes;
    LOCATION_TYPE[] locationTypes;

    public PregeneratorData(String text, SUBLEVEL_TYPE[] sublevelTypes, LOCATION_TYPE[] locationTypes) {
        super(text);
        this.sublevelTypes = sublevelTypes;
        this.locationTypes = locationTypes;
    }

    public enum PREGENERATOR_VALUES implements Options.OPTION {
        MAX_ATTEMPTS_PER_LEVEL(50, 1, 200),
        MAX_TIME,
        MIN_RATING(200, -100, 1000),
        GLOBAL_RANDOMNESS,
        LEVELS_REQUIRED(10, 1, 100),
        RANDOMIZATION_MOD(100, 0, 200);

        private Boolean exclusive;
        private Integer min;
        private Integer max;
        private Object[] options;
        private Object defaultValue;

        PREGENERATOR_VALUES(Boolean exclusive) {
            this.exclusive = exclusive;
            defaultValue = exclusive;
        }

        PREGENERATOR_VALUES(Object... options) {
            this.options = options;
            if (options.length > 0)
                defaultValue = options[0];
        }

        PREGENERATOR_VALUES(Integer defaultValue, Integer min, Integer max) {
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

/*
reqs
flags
data 'offsets'
location/subtype

 */
    }
}
