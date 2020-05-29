package eidolons.game.battlecraft.rules;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.system.controls.Controller;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.data.XLinkedMap;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.secondary.Bools;
import main.system.launch.CoreEngine;

import java.util.HashMap;
import java.util.Map;

public class RuleKeeper implements Controller {

    private static final RULE[] RULES_BEING_TESTED = {
//            RULE.PARRYING,
//            RULE.SHIELD,
    };
    static Map<Object, Boolean> overrideMap = new HashMap<>();
    private static Map<RULE, Boolean> map = new XLinkedMap<>();
    private static Map<RULE, Boolean> mapTest = new XLinkedMap<>();
    private static RULE_SCOPE scope = RULE_SCOPE.BASIC;
    private static RuleKeeper instance;

    private RuleKeeper() {
        instance = this;
    }

    public static boolean isCooldownOn() {
        return false;
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
        if (CoreEngine.isIDE())
            if (CoreEngine.isLiteLaunch())
                for (RULE r : RULES_BEING_TESTED) {
                    mapTest.put(r, true);
                }
    }

    private static Boolean checkStatus(RULE_SCOPE statusForRule) {
        switch (statusForRule) {
            case EIDOLON:
                return true;
//                return Eidolons.getGame() instanceof NF_Game;
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
            case FORCE:
                return RULE_SCOPE.FULL;
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
        if (Eidolons.getGame().isDebugMode())
            return false;
        return Bools.isTrue(mapTest.get(rule));
    }

    public static boolean isRuleOn(RULE rule) {
        if (CoreEngine.TEST_LAUNCH){
            switch (rule) {
                case DURABILITY:
                case SOULFORCE:
                    return false;
            }
        }
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
            case ADVANCED:
                break;
            case BASIC:
                switch (feature) {

//                    case THROW_WEAPON:
                    case ORDERS:
                    case WATCH:
                    case GUARD_MODE:
                    case TOSS_ITEM:
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
        RuleKeeper.scope = scope;
    }

    public static RuleKeeper getInstance() {
        if (instance == null) {
            instance = new RuleKeeper();
        }
        return instance;
    }

    public static void setInstance(RuleKeeper instance) {
        RuleKeeper.instance = instance;
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

    public static boolean isHeroEnduranceRegenOn() {
        return false;
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
        VISIBILITY, ORDERS, GUARD_MODE, THROW_WEAPON, TOGGLE_WEAPON_SET;
        int featureLevel;
    }

    public enum GENERAL_RULES {

    }

    public enum RULE {
        SOULFORCE(RULE_SCOPE.EIDOLON),
        FORCE(RULE_SCOPE.BASIC),
        CHANNELING(RULE_SCOPE.TEST),
        ATTACK_OF_OPPORTUNITY(RULE_SCOPE.BASIC),
        INSTANT_ATTACK(RULE_SCOPE.BASIC),
        COUNTER_ATTACK(RULE_SCOPE.BASIC),
        TIME(RULE_SCOPE.BASIC),
        VISIBILITY(RULE_SCOPE.BASIC),
        CLEAR_SHOT(RULE_SCOPE.BASIC),
        PARRYING(RULE_SCOPE.BASIC),
        SHIELD(RULE_SCOPE.BASIC),
        STEALTH(RULE_SCOPE.BASIC),
        // C
        DURABILITY(RULE_SCOPE.BASIC),
        UNCONSCIOUS(RULE_SCOPE.BASIC),
        FOCUS(RULE_SCOPE.BASIC),
        MORALE(RULE_SCOPE.BASIC),
        MORALE_KILL(RULE_SCOPE.BASIC),
        STAMINA(RULE_SCOPE.BASIC),
        WOUNDS(RULE_SCOPE.BASIC),
        BLEEDING(RULE_SCOPE.BASIC),
        WEIGHT(RULE_SCOPE.BASIC),
        INJURY(RULE_SCOPE.FULL),
        CRITICAL_ATTACK(RULE_SCOPE.BASIC),
        DODGE(), GUARD(), MISSED_ATTACK_REDIRECTION(RULE_SCOPE.BASIC),
        TRAMPLE((RULE_SCOPE.ADVANCED)), WATCH(RULE_SCOPE.ADVANCED),
        CLEAVE(RULE_SCOPE.BASIC), HEARING(RULE_SCOPE.FULL), INTENTS(RULE_SCOPE.ADVANCED);

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
        TEST, BASIC, FULL, ADVANCED, EIDOLON,
    }

    public enum SPELLCASTING_RULES {

    }

    public enum TURN_RULES {
        BLEEDING, POISON, DISEASE, FOCUS, MORALE,
    }

}
