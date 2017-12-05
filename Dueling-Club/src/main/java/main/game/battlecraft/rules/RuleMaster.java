package main.game.battlecraft.rules;

import main.data.XLinkedMap;
import main.entity.obj.unit.Unit;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.controls.Controller;
import main.system.options.GameplayOptions.GAMEPLAY_OPTION;
import main.system.options.OptionsMaster;

import java.util.HashMap;
import java.util.Map;

public class RuleMaster implements Controller {

    static Map<Object, Boolean> overrideMap = new HashMap<>();
    private static Map<RULE, Boolean> map = new XLinkedMap<>();
    private static Map<RULE, Boolean> mapTest = new XLinkedMap<>();
    private static RULE_SCOPE scope = RULE_SCOPE.BASIC;
    private static RuleMaster instance;

    private RuleMaster() {
        instance = this;
    }

    public static void init() {
        try {
            scope = new EnumMaster<RULE_SCOPE>().retrieveEnumConst(RULE_SCOPE.class,
             OptionsMaster.getGameplayOptions().getValue(GAMEPLAY_OPTION.RULES_SCOPE));
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        for (RULE r : RULE.values()) {
            Boolean on = checkStatus(getScopeForRule(r));
            map.put(r, on);
            if (getScope() == RULE_SCOPE.TEST) {
                if (getScopeForRule(r) == RULE_SCOPE.TEST) {
                    mapTest.put(r, true);
                }
            }
        }
    }

    private static Boolean checkStatus(RULE_SCOPE statusForRule) {
        switch (statusForRule) {
            case BASIC:
                switch (getScope()) {
                    case ADVANCED:
                    case BASIC:
                    case FULL:
                    case TEST:
                        return true;
                }
                break;
            case FULL:
                switch (getScope()) {
                    case ADVANCED:
                    case FULL:
                    case TEST:
                        return true;
                }
                break;
            case ADVANCED:
                switch (getScope()) {
                    case TEST:
                    case ADVANCED:
                        return true;
                }
            case TEST:
                switch (getScope()) {
                    case TEST:
                        return true;
                }
                break;
        }
        return false;
    }

    public static boolean checkRuleGroupIsOn(RULE_GROUP group) {
        if (overrideMap.containsKey(group)) {
            return overrideMap.get(group);
        }
        return checkStatus(getScopeForRuleGroup(group));
    }

    public static RULE_SCOPE getScopeForRuleGroup(RULE_GROUP r) {
        switch (r) {
//            case EXTRA_ATTACKS:
//                return RULE_SCOPE.TEST;
        }
        return RULE_SCOPE.BASIC;
    }

    public static RULE_SCOPE getScopeForRule(RULE r) {
        if (r.scope != null) {
            return r.scope;
        }
        switch (r) {
            case INJURY:
                return RULE_SCOPE.TEST;
            case ATTACK_OF_OPPORTUNITY:
            case INSTANT_ATTACK:
                return RULE_SCOPE.FULL;
            case FORCE:
            case TRAMPLE:
            return RULE_SCOPE.ADVANCED;

        }
        return RULE_SCOPE.BASIC;
        // UNCONSCIOUS, FOCUS, MORALE, MORALE_KILL, STAMINA, WOUNDS, BLEEDING,
        // WEIGHT,
    }

    public static void setRuleIsOn(Boolean on, RULE rule) {
        map.put(rule, on);
    }

    public static void setRuleTestIsOn(Boolean on, RULE rule) {
        mapTest.put(rule, on);
    }


    public static boolean isRuleOn(String id) {
        return true;
    }

    public static boolean isRuleOnInExploreMode(RULE rule) {
        switch (rule) {
            case CHANNELING:
            case ATTACK_OF_OPPORTUNITY:
            case INSTANT_ATTACK:
            case TIME:
                return false;

        }
        return true;
    }
        public static boolean isRuleTestOn(RULE rule) {
        if (BooleanMaster.isTrue(mapTest.get(rule))) {
            return true;
        }
        return false;
    }

    public static boolean isRuleOn(RULE rule) {
        if (ExplorationMaster.isExplorationOn()) {
            if (!isRuleOnInExploreMode(rule))
                return false;
        }
        if (overrideMap.containsKey(rule)) {
            return overrideMap.get(rule);
        }
        return checkStatus(getScopeForRule(rule));

    }

    public static boolean checkFeature(FEATURE feature) {
        // if (hardFeatures.contains(feature))
        // return true;
        // if (featureLevel < feature.getFeatureLevel().getValue())
        // return false;
        //
        // if (blockedFeatures.contains(feature))
        // return false;
        switch (getScope()) {

            case TEST:
                break;
            case BASIC:
                switch (feature) {

                    case ORDERS:
                    case WATCH:
//                    case USE_INVENTORY:
//                    case DUAL_ATTACKS:
//                    case VISIBILITY:
                        return false;
                }
                break;
            case FULL:
                switch (feature) {
                    case ORDERS:
//                    case DUAL_ATTACKS:
                        return false;
                }
                break;
            case ADVANCED:
                break;
        }

        return true;
    }

    public static void applyCompensation(Unit unit) {

    }

    public static String getRuleLogText(TURN_RULES rule, int amount) {
        switch (rule) {
            case MORALE:
                // if (amount > 0)
                // return " regains " + amount + " Morale!";
                // return " calms down, giving up " + amount + " Morale";
                if (amount > 0) {
                    return "'s Fright subsides, " + amount + " Morale regained!";
                }
                return "'s Inspiration subsides, Morale reduced by " + amount;
            case FOCUS:
                if (amount > 0) {
                    return "'s Dizziness subsides, " + amount + " Focus regained!";
                }
                return "'s Sharpness subsides, Focus reduced by " + amount;

            case BLEEDING:
                if (amount != 0) {
                    return " suffers " + amount + " damage from bleeding!";
                }
                return " is bleeding!";
            case DISEASE:
                if (amount != 0) {
                    return " suffers " + amount + " damage from disease!";
                }
                return " is diseased!";
            case POISON:
                if (amount != 0) {
                    return " suffers " + amount + " damage from poison!";
                }
                return " is poisoned!";
            default:
                break;

        }
        return null;

    }

    public static String getRuleLogText(COMBAT_RULES rule, int level) {
        switch (rule) {
            case BLEEDING:
                break;
            case FOCUS:
                if (level == 2) {
                    return "'s focus is razorsharp!";
                }
                if (level == 1) {
                    return " is dizzy!";
                }
                if (level == 0) {
                    return " is confused!";
                }
            case MORALE:
                if (level == 2) {
                    return " takes heart!";
                }
                if (level == 1) {
                    return " loses heart!";
                }
                if (level == 0) {
                    return " panics!";
                }
            case MORALE_KILL:
                break;
            case STAMINA:
                if (level == 2) {
                    return " is full of energy!";
                }
                if (level == 1) {
                    return " is fatigued!";
                }
                if (level == 0) {
                    return " is exhausted!";
                }
            case WEIGHT:
                if (level == 1) {
                    return " is encumbered!";
                }
                if (level == 0) {
                    return " is overburdened!";
                }
            case WOUNDS:
                if (level == 1) {
                    return " is wounded!";
                }
                if (level == 0) {
                    return " is critically wounded!";
                }
            default:
                break;

        }
        return null;
    }

    public static RULE_SCOPE getScope() {
        return scope;
    }

    public static void setScope(RULE_SCOPE scope) {
        RuleMaster.scope = scope;
    }

    public static RuleMaster getInstance() {
        if (instance == null) {
            instance = new RuleMaster();
        }
        return instance;
    }

    public static void setInstance(RuleMaster instance) {
        RuleMaster.instance = instance;
    }

    @Override
    public boolean charTyped(char c) {
        switch (c) {
            case 's':
                RULE_SCOPE scope = new EnumMaster<RULE_SCOPE>().selectEnum(RULE_SCOPE.class);
                if (scope != null) {
                    setScope(scope);
                }
                return true;
            case 'r':
                clearOverrides();
            case 'f':
            case 'n':
                RULE rule = new EnumMaster<RULE>().selectEnum(RULE.class);
                setOverride(rule, c == 'n');
                return true;
            case 'F':
            case 'N':
                RULE_GROUP group = new EnumMaster<RULE_GROUP>().selectEnum(RULE_GROUP.class);
                setOverride(group, c == 'N');
                return true;
        }
        return false;
    }

    private void clearOverrides() {
        overrideMap.clear();
    }


    private void setOverride(Object obj, boolean b) {
        overrideMap.put(obj, b);
    }

    public enum COMBAT_RULES {
        UNCONSCIOUS, FOCUS, MORALE, MORALE_KILL, STAMINA, WOUNDS, BLEEDING, WEIGHT,
    }

    public enum COUNTER_RULES {
BLEEDING, BLAZE, FREEZE, POISON, DISEASE,
    }

    public enum FEATURE {
        USE_INVENTORY, WATCH, FLEE, DIVINATION, TOSS_ITEM, PICK_UP,
        ENTER, DUAL_ATTACKS,
        VISIBILITY, ORDERS;
        int featureLevel;
    }

    public enum GENERAL_RULES {

    }

    public enum RULE {
        FORCE(RULE_SCOPE.FULL),
        CHANNELING(RULE_SCOPE.TEST),
        ATTACK_OF_OPPORTUNITY(RULE_SCOPE.FULL),
        INSTANT_ATTACK(RULE_SCOPE.FULL),
        COUNTER_ATTACK(RULE_SCOPE.BASIC),
        TIME(RULE_SCOPE.BASIC),
        VISIBILITY(RULE_SCOPE.FULL),
        CLEAR_SHOT(RULE_SCOPE.BASIC),
        PARRYING(RULE_SCOPE.FULL),
        STEALTH(RULE_SCOPE.BASIC),
        // C
        DURABILITY,
        UNCONSCIOUS,
        FOCUS,
        MORALE,
        MORALE_KILL,
        STAMINA,
        WOUNDS,
        BLEEDING,
        WEIGHT,
        INJURY(RULE_SCOPE.FULL),
        CRITICAL_ATTACK(),
        DODGE(), GUARD(), MISSED_ATTACK_REDIRECTION(RULE_SCOPE.ADVANCED), TRAMPLE(), WATCH(RULE_SCOPE.ADVANCED);

        String tooltip;
        RULE_SCOPE scope;

        RULE(RULE_SCOPE scope) {
            this.scope = scope;
        }

        RULE() {
//scope = RULE_SCOPE.BASIC; TODO
        }

        RULE(String tooltip) {
            this.tooltip = tooltip;
        }

        public String getTooltip() {
            return tooltip;
        }

        public void setTooltip(String tooltip) {
            this.tooltip = tooltip;
        }
    }

    public enum RULE_GROUP {
        BUFF_RULES, PARAM_RULES, PARAM_BUFF_RULES, COUNTER_RULES, EXTRA_ATTACKS,
    }

    public enum RULE_SCOPE {
        TEST, BASIC, FULL, ADVANCED,
    }

    public enum SPELLCASTING_RULES {

    }

    public enum TURN_RULES {
        BLEEDING, POISON, DISEASE, FOCUS, MORALE,
    }

}
