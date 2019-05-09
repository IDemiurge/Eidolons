package main.content.enums.entity;

import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 2/14/2017.
 */
public class ActionEnums {
    public enum ACTION_TAGS {
        FIXED_COST, COMBAT_ONLY,
        FLYING,
        DUAL,
        UNARMED,
        OFF_HAND,
        MAIN_HAND,
        TWO_HANDED,
        TWO_HANDS,
        RANGED_TOUCH,
        ATTACK_OF_OPPORTUNITY,
        ATTACK_OF_OPPORTUNITY_ACTION,
        INSTANT,
        RANGED,
        THROW,
        MISSILE,
        INSTANT_ATTACK,
        RESTORATION, TOP_DOWN;

        private String string;

        public String toString() {
            if (string == null)
                string = StringMaster.getWellFormattedString(name());
            return string;
        }

    }

    public enum ACTION_TYPE {
        STANDARD,
        MODE,
        SPECIAL_MOVE,
        STANDARD_ATTACK,
        SPECIAL_ATTACK,
        SPECIAL_ACTION,
        HIDDEN,
        ADDITIONAL_MOVE,
    }

    public enum ACTION_TYPE_GROUPS {
        MOVE, TURN, ATTACK, SPELL, ADDITIONAL, SPECIAL, HIDDEN, MODE, ITEM, ORDER,
        DUNGEON, // security fix...
        STANDARD

    }

    public enum CUSTOM_HERO_GROUP {
        PLAYTEST, ERSIDRIS, EDALAR, TEST,

    }

    public enum STANDARD_ACTION_PASSIVES {
        AGILE, DEXTEROUS,
    }
}
