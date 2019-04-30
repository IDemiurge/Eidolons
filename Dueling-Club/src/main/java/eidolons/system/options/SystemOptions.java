package eidolons.system.options;

import eidolons.system.options.SystemOptions.SYSTEM_OPTION;

/**
 * Created by JustMe on 8/27/2017.
 */
public class SystemOptions
 extends Options<SYSTEM_OPTION,SYSTEM_OPTION>
{

    protected Class<? extends SYSTEM_OPTION> getOptionClass() {

        return SYSTEM_OPTION.class;
    }

    public  enum SYSTEM_OPTION implements Options.OPTION {
        LOGGING(true) ,
        LOG_TO_FILE(true),
        RESET_COSTS(false),
        MESSAGES_OFF(false),
        CACHE,
        PRECONSTRUCT,

        LAZY,
        ;

        boolean exclusive;
        Object[] options;
        Integer min;
        Integer max;
        Object defaultValue;

        SYSTEM_OPTION() {

        }

        SYSTEM_OPTION(Integer defaultValue, Integer min, Integer max) {
            this.defaultValue = defaultValue;
            this.max = max;
            this.min = min;
        }

        SYSTEM_OPTION(Object[] options) {
            this.options = options;
        }

        SYSTEM_OPTION(boolean defaultValue) {this.exclusive = defaultValue;
            this.defaultValue = defaultValue;
        }

        public Object[] getOptions() {
            return options;
        }

        public Integer getMin() {
            return min;
        }

        public Integer getMax() {
            return max;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        @Override
        public Boolean isExclusive() {
            return exclusive;
        }
    }
}
