package main.content;

import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;

public class CONTENT_CONSTS2 {
    public enum AI_MODIFIERS {
        TRUE_BRUTE, COWARD, MERCIFUL, CRUEL,
    }

    // to use {INDEXES} ... I'd need a lot of smarts :) >|< separator for
    /*
     * Pestlego
	 * Astro 
	 * Syphron
	 * 
	 */

    public enum MACRO_STATUS {
        CAMPING, EXPLORING, TRAVELING, IN_AMBUSH, IDLE,
    }


    public enum ORDER_TYPE {
        PATROL, PURSUIT, MOVE, ATTACK, KILL, SPECIAL, HEAL, SUPPORT, PROTECT, HOLD, WANDER
    }

    public enum SHOP_LEVEL { // quality and materials - filter in and Type will
        // filter out?
        POOR,
        COMMON,
        QUALITY,
        OPULENT,
    }

    public enum SHOP_MODIFIER {
        HUMAN, ELVEN, DWARVEN, WIZARDING, DARK, UNDERGROUND, HOLY,
    }

    public enum SHOP_TYPE {
        // ++ per faction?
        MERCHANT("cloth", "ranged", "ammo", "potions", "poisons", "elixirs", "concoctions", "orbs", "wands"), // if
        SPECIAL_GOODS("ranged", "ammo", "daggers", "poisons"),
        JEWELER("rings", "amulets", "empty"),

        HEAVY_WEAPONS(G_PROPS.WEAPON_SIZE, "huge", "large", "medium"),
        WEAPONS(G_PROPS.WEAPON_SIZE, "large", "medium", "small"),
        LIGHT_WEAPONS(G_PROPS.WEAPON_SIZE, "medium", "small", "tiny"),

        LIGHT_ARMOR("cloth", "leather", "bucklers"),
        ARMOR("chain", "leather", "bucklers"),
        HEAVY_ARMOR("chain", "plate", "shields"), //

        ALCHEMIST("potions"),//, "poisons", "elixirs", "concoctions"),
        UNDERTAKER,
        BLACK_MARKET,

        MAGICAL_GOODS("orbs", "wands", "potions", "elixirs"),
        ARTIFACTS("orbs", "wands"),
        MISC,;
        String[] item_groups;
        PROPERTY filterProp;

        // ++ item type exceptions
        SHOP_TYPE(PROPERTY filterProp, String... item_groups) {
            this.item_groups = item_groups;
            this.filterProp = filterProp;
        }

        SHOP_TYPE(String... item_groups) {
            this.item_groups = item_groups;
        }

        public String[] getItemGroups() {
            return item_groups;
        }

        public PROPERTY getFilterProp() {
            return filterProp;
        }
    }


}
