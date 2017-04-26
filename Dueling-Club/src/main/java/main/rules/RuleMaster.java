package main.rules;

import main.data.XLinkedMap;
import main.entity.obj.unit.Unit;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.controls.Controller;

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
                    case BASIC:
                        return true;
                    case FULL:
                        return true;
                    case TEST:
                        return true;
                }
            case FULL:
                switch (getScope()) {
                    case BASIC:
                        return false;
                    case FULL:
                        return true;
                    case TEST:
                        return true;
                }
            case TEST:
                switch (getScope()) {
                    case BASIC:
                        return false;
                    case FULL:
                        return false;
                    case TEST:
                        return true;
                }
        }
        return null;
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
            case FORCE:
            case INJURY:
                return RULE_SCOPE.TEST;
            case ATTACK_OF_OPPORTUNITY:
            case INSTANT_ATTACK:
                return RULE_SCOPE.FULL;

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

    public static boolean isRuleTestOn(RULE rule) {
        if (BooleanMaster.isTrue(mapTest.get(rule))) {
            return true;
        }
        return false;
    }

    public static boolean isRuleOn(RULE rule) {
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

    }

    public enum FEATURE {
        USE_INVENTORY, WATCH, FLEE, DIVINATION, TOSS_ITEM, PICK_UP, ENTER;
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
        INJURY(RULE_SCOPE.FULL);

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
        TEST, BASIC, FULL,
    }

    public enum SPELLCASTING_RULES {

    }

    public enum TURN_RULES {
        BLEEDING, POISON, DISEASE, FOCUS, MORALE,
    }

}
