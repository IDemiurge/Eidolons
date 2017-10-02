package main.content.enums.entity;

import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 2/14/2017.
 */
public class ActionEnums {
    public enum ACTION_TYPE_GROUPS {
        MOVE, TURN, ATTACK, SPELL, SPECIAL, HIDDEN, MODE, ITEM, ORDER,
        DUNGEON, // security fix...
        STANDARD

    }

    public static enum CUSTOM_HERO_GROUP {
        PLAYTEST, ERSIDRIS, EDALAR, TEST,

    }

    public enum ACTION_TAGS {
        FLYING,
        DUAL,
        UNARMED,
        OFF_HAND,
        MAIN_HAND,
        TWO_HANDED,
        RANGED_TOUCH,
        ATTACK_OF_OPPORTUNITY,
        ATTACK_OF_OPPORTUNITY_ACTION,
        INSTANT,
        RANGED,
        THROW,
        MISSILE,
        INSTANT_ATTACK,
        RESTORATION, TOP_DOWN;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
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

    public enum STANDARD_ACTION_PASSIVES {
        AGILE, DEXTEROUS,
    }
}
