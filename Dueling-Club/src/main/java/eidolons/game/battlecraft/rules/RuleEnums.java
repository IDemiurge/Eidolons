package eidolons.game.battlecraft.rules;

public class RuleEnums {
    public enum COMBAT_RULES {
        UNCONSCIOUS, FOCUS, ESSENCE, ESSENCE_KILL, TOUGHNESS, WOUNDS, BLEEDING, WEIGHT,
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
        CLEAVE(RULE_SCOPE.BASIC), HEARING(RULE_SCOPE.FULL), INTENTS(RULE_SCOPE.ADVANCED), INTEGRITY(RULE_SCOPE.ADVANCED);

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
