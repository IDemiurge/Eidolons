package eidolons.game.battlecraft.rules;

import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.system.controls.Controller;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.data.XLinkedMap;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.secondary.Bools;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

import java.util.HashMap;
import java.util.Map;

public class RuleKeeper implements Controller {

    private static final RuleEnums.RULE[] RULES_BEING_TESTED = {
//            RULE.PARRYING,
//            RULE.SHIELD,
    };
    static Map<Object, Boolean> overrideMap = new HashMap<>();
    private static final Map<RuleEnums.RULE, Boolean> map = new XLinkedMap<>();
    private static final Map<RuleEnums.RULE, Boolean> mapTest = new XLinkedMap<>();
    private static RuleEnums.RULE_SCOPE scope = RuleEnums.RULE_SCOPE.BASIC;
    private static RuleKeeper instance;

    private RuleKeeper() {
        instance = this;
    }

    public static boolean isCooldownOn() {
        return false;
    }

    public static void init() {
        try {
            setScope(new EnumMaster<RuleEnums.RULE_SCOPE>().retrieveEnumConst(RuleEnums.RULE_SCOPE.class,
                    OptionsMaster.getGameplayOptions().getValue(GAMEPLAY_OPTION.RULES_SCOPE)));
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        for (RuleEnums.RULE r : RuleEnums.RULE.values()) {
            Boolean on = checkStatus(getScopeForRule(r));
            map.put(r, on);
            if (getScope() == RuleEnums.RULE_SCOPE.TEST) {
                if (getScopeForRule(r) == RuleEnums.RULE_SCOPE.TEST) {
                    mapTest.put(r, true);
                }
            }
        }
        if (Flags.isIDE())
            if (Flags.isLiteLaunch())
                for (RuleEnums.RULE r : RULES_BEING_TESTED) {
                    mapTest.put(r, true);
                }
    }

    private static Boolean checkStatus(RuleEnums.RULE_SCOPE statusForRule) {
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

    public static boolean checkRuleGroupIsOn(RuleEnums.RULE_GROUP group) {
        if (overrideMap.containsKey(group)) {
            return overrideMap.get(group);
        }
        return checkStatus(getScopeForRuleGroup(group));
    }

    public static RuleEnums.RULE_SCOPE getScopeForRuleGroup(RuleEnums.RULE_GROUP r) {
        switch (r) {
//            case EXTRA_ATTACKS:
//                return RULE_SCOPE.TEST;
        }
        return RuleEnums.RULE_SCOPE.BASIC;
    }

    public static RuleEnums.RULE_SCOPE getScopeForRule(RuleEnums.RULE r) {
        if (r.scope != null) {
            return r.scope;
        }
        switch (r) {
            case INJURY:
                return RuleEnums.RULE_SCOPE.TEST;
            case ATTACK_OF_OPPORTUNITY:
            case INSTANT_ATTACK:
            case FORCE:
                return RuleEnums.RULE_SCOPE.FULL;
            case TRAMPLE:
                return RuleEnums.RULE_SCOPE.ADVANCED;

        }
        return RuleEnums.RULE_SCOPE.BASIC;
        // UNCONSCIOUS, FOCUS, MORALE, MORALE_KILL, STAMINA, WOUNDS, BLEEDING,
        // WEIGHT,
    }

    public static void setRuleIsOn(Boolean on, RuleEnums.RULE rule) {
        map.put(rule, on);
    }

    public static void setRuleTestIsOn(Boolean on, RuleEnums.RULE rule) {
        mapTest.put(rule, on);
    }


    public static boolean isRuleOn(String id) {
        return true;
    }

    public static boolean isRuleOnInExploreMode(RuleEnums.RULE rule) {
        switch (rule) {
            case CHANNELING:
            case ATTACK_OF_OPPORTUNITY:
            case INSTANT_ATTACK:
            case TIME:
                return false;

        }
        return true;
    }

    public static boolean isRuleTestOn(RuleEnums.RULE rule) {
        if (Eidolons.getGame().isDebugMode())
            return false;
        return Bools.isTrue(mapTest.get(rule));
    }

    public static boolean isRuleOn(RuleEnums.RULE rule) {
        if (CoreEngine.TEST_LAUNCH){
            switch (rule) {
                case DURABILITY:
                // case SOULFORCE:
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

    public static boolean checkFeature(RuleEnums.FEATURE feature) {
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

    public static RuleEnums.RULE_SCOPE getScope() {
        return scope;
    }

    public static void setScope(RuleEnums.RULE_SCOPE scope) {
        if (scope != null) {
            RuleKeeper.scope = scope;
        }
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
                RuleEnums.RULE_SCOPE scope = new EnumMaster<RuleEnums.RULE_SCOPE>().selectEnum(RuleEnums.RULE_SCOPE.class);
                if (scope != null) {
                    setScope(scope);
                }
                return true;
            case 'r':
                clearOverrides();
            case 'f':
            case 'n':
                RuleEnums.RULE rule = new EnumMaster<RuleEnums.RULE>().selectEnum(RuleEnums.RULE.class);
                setOverride(rule, c == 'n');
                return true;
            case 'F':
            case 'N':
                RuleEnums.RULE_GROUP group = new EnumMaster<RuleEnums.RULE_GROUP>().selectEnum(RuleEnums.RULE_GROUP.class);
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


}
