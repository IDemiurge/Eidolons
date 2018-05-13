package eidolons.system.options;

import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.RuleKeeper.RULE_SCOPE;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import main.content.enums.GenericEnums;
import main.content.enums.rules.VisionEnums.INFO_LEVEL;

public class GameplayOptions extends Options<GAMEPLAY_OPTION, GAMEPLAY_OPTION> {

    static {
        GAMEPLAY_OPTION.RULES_SCOPE.setDefaultValue(RULE_SCOPE.BASIC);
        GAMEPLAY_OPTION.GAME_DIFFICULTY.setDefaultValue(GenericEnums.DIFFICULTY.NOVICE);
        GAMEPLAY_OPTION.INFO_DETAIL_LEVEL.setDefaultValue(INFO_LEVEL.NORMAL);

        GAMEPLAY_OPTION.NEXT_SCENARIO_INDEX.setHidden(true);
    }

    @Override
    protected Class<? extends GAMEPLAY_OPTION> getOptionClass() {
        return GAMEPLAY_OPTION.class;
    }

    public enum GAMEPLAY_OPTION implements Options.OPTION {
        RULES_SCOPE(RuleKeeper.RULE_SCOPE.values()),
        GAME_DIFFICULTY(GenericEnums.DIFFICULTY.values()),

        //        AI_SPEED,
        DEFAULT_ACTIONS(true),
//        ALT_DEFAULT_ACTIONS,

        RANDOM_HERO(true),
        MANUAL_CONTROL(false),
        DEBUG_MODE(false),

        INFO_DETAIL_LEVEL(INFO_LEVEL.values()),
        DEFAULT_WAIT_TIME(60, 10, 300),
        HP_BARS_ALWAYS_VISIBLE(true), GAME_SPEED(100, 10, 300),
        ATB_WAIT_TIME(5, 0, 10)
        , SHUFFLE_LEVELS(false)
        , REVERSE_LEVELS(false),
        GHOST_MODE(false), AI_TIME_LIMIT_MOD(100, 10, 300), NEXT_SCENARIO_INDEX(0,0,6);
        private Boolean exclusive;
        private Integer min;
        private Integer max;
        private Object[] options;
        private Object defaultValue;
        private boolean hidden;

        GAMEPLAY_OPTION(Boolean exclusive) {
            this.exclusive = exclusive;
            defaultValue = exclusive;
        }

        GAMEPLAY_OPTION(Object... options) {
            this.options = options;
        }

        GAMEPLAY_OPTION(Integer defaultValue, Integer min, Integer max) {
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

        public void setHidden(boolean hidden) {
            this.hidden = hidden;
        }
        @Override
        public boolean isHidden() {
            return hidden;
        }
    }
}
