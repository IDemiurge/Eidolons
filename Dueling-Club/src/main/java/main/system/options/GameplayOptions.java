package main.system.options;

import main.system.options.GameplayOptions.GAMEPLAY_OPTION;

public class GameplayOptions extends  Options<GAMEPLAY_OPTION,GAMEPLAY_OPTION>{

    @Override
    protected Class<? extends GAMEPLAY_OPTION> getOptionClass() {
        return GAMEPLAY_OPTION.class;
    }

    public enum GAMEPLAY_OPTION implements Options.OPTION {
RULE_SCOPE,

        AI_SPEED,
        DEFAULT_ACTIONS,
        ALT_DEFAULT_ACTIONS,

        MANUAL_CONTROL,
        DEBUG_MODE,

     ;
     private Boolean exclusive;
     private Integer min;
     private Integer max;
     private Object[] options;
     private Object defaultValue;

     public void setDefaultValue(Object defaultValue) {
         this.defaultValue = defaultValue;
     }

     GAMEPLAY_OPTION(Boolean exclusive) {
         this.exclusive = exclusive;
         defaultValue = exclusive;
     }

     GAMEPLAY_OPTION(Object... options) {
         this.options = options;
     }

     GAMEPLAY_OPTION(Integer min, Integer max, Object... options) {
         this.min = min;
         this.max = max;

     }

     GAMEPLAY_OPTION(Boolean exclusive, Integer min, Integer max, Object... options) {
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
