package main.system.options;

import main.game.battlecraft.logic.battle.universal.BattleOptions.DIFFICULTY;
import main.game.battlecraft.rules.RuleMaster;
import main.game.battlecraft.rules.RuleMaster.RULE_SCOPE;
import main.system.options.GameplayOptions.GAMEPLAY_OPTION;

public class GameplayOptions extends  Options<GAMEPLAY_OPTION,GAMEPLAY_OPTION>{

    @Override
    protected Class<? extends GAMEPLAY_OPTION> getOptionClass() {
        return GAMEPLAY_OPTION.class;
    }

    static{
        GAMEPLAY_OPTION.RULES_SCOPE.setDefaultValue(RULE_SCOPE.BASIC);
        GAMEPLAY_OPTION.GAME_DIFFICULTY.setDefaultValue(DIFFICULTY.NOVICE);
    }
    public enum GAMEPLAY_OPTION implements Options.OPTION {
        RULES_SCOPE(RuleMaster.RULE_SCOPE.values()),
        GAME_DIFFICULTY(DIFFICULTY.values()),

//        AI_SPEED,
        DEFAULT_ACTIONS(true),
//        ALT_DEFAULT_ACTIONS,

        MANUAL_CONTROL(false),
        DEBUG_MODE(false),

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

     GAMEPLAY_OPTION(Integer defaultValue,Integer min, Integer max ) {
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
