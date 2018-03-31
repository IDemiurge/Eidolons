package main.system.text;

/**
 * Created by JustMe on 8/30/2017.
 */
public class Tips {

    public enum ADVANCED_COMBAT_TIPS implements TIP {
        SMART_ACTION_SEQUENCES, USING_TIME_RULE, USE_INVENTORY, ALERT_MODE, WAIT_ACTION,;


        String text;

        ADVANCED_COMBAT_TIPS(String... text) {
            if (text.length > 0)
                this.text = text[0];
        }

        @Override
        public String getText() {
            if (text == null) text = TipMaster.getText(this);
            return text;

        }
    }

    public enum ADVANCED_HERO_TIPS implements TIP {

        GRADUAL_SPELL_LEARNING, SPELL_UPGRADES, MULTICLASSING, ITEM_ENCHANTMENT,;

        String text;

        ADVANCED_HERO_TIPS(String... text) {
            if (text.length > 0)
                this.text = text[0];
        }

        @Override
        public String getText() {
            if (text == null) text = TipMaster.getText(this);
            return text;

        }
    }

    public enum BASIC_ARCADE_TIPS implements TIP {
        DUNGEONS, ENCOUNTERS, REGIONS, LOOT, HERO_DEATH, LOAD_GAME, GLORY_RATING,;

        String text;

        BASIC_ARCADE_TIPS(String... text) {
            if (text.length > 0)
                this.text = text[0];
        }

        @Override
        public String getText() {
            if (text == null) text = TipMaster.getText(this);
            return text;

        }
    }


    public enum BASIC_COMBAT_TIPS implements TIP {
        RESISTANCE, ARMOR, ATTACK_AND_DEFENSE, VISION, DYNAMIC_ROUNDS,
        PAUSE, MISSION(),

        OPTIONS,
        MODES, GAME_START(), DEFAULT_ACTIONS(), DEBUG();

        String text;

        BASIC_COMBAT_TIPS(String... text) {
            if (text.length > 0)
                this.text = text[0];
        }

        @Override
        public String getText() {
            if (text == null) text = TipMaster.getText(this);
            return text;

        }
    }

    public enum BASIC_HERO_TIPS implements TIP {
        PRINCIPLES, SPELL_LEARNING,

        WEAPON_ITEMS, ARMOR_ITEMS,

        HERO_BACKGROUNDS,;

        String text;

        BASIC_HERO_TIPS(String... text) {
            if (text.length > 0)
                this.text = text[0];
        }

        @Override
        public String getText() {
            if (text == null) text = TipMaster.getText(this);
            return text;

        }

    }

    public enum BASIC_USABILITY_TIPS implements TIP {
        INFO_PAGES,

        ALT_CLICK,;

        String text;

        BASIC_USABILITY_TIPS(String... text) {
            if (text.length > 0)
                this.text = text[0];
        }

        @Override
        public String getText() {
            if (text == null) text = TipMaster.getText(this);
            return text;

        }

    }

    public enum COMBAT_TIPS implements TIP {
        TIME_RULE, PARAMETER_RULES, WOUNDS_AND_BLEEDING,

        SUMMONING_SICKNESS, UPKEEP, STEALTH,

        BF_OBJECTS,;

        String text;

        COMBAT_TIPS(String... text) {
            if (text.length > 0)
                this.text = text[0];
        }

        @Override
        public String getText() {
            if (text == null) text = TipMaster.getText(this);
            return text;

        }
    }

    public enum COMBAT_TIP_CATEGORY implements TIP {
        GENERAL, SPELLCASTING, MELEE, RANGED, STEALTH,;

        String text;

        COMBAT_TIP_CATEGORY(String... text) {
            if (text.length > 0)
                this.text = text[0];
        }

        @Override
        public String getText() {
            if (text == null) text = TipMaster.getText(this);
            return text;

        }
    }

    public enum HERO_TIPS implements TIP {
        DIVINATION, SKILL_POINTS, JEWELRY, QUICK_ITEMS, WEIGHT,;

        String text;

        HERO_TIPS(String... text) {
            if (text.length > 0)
                this.text = text[0];
        }

        @Override
        public String getText() {
            if (text == null) text = TipMaster.getText(this);
            return text;

        }
    }

    public enum HERO_TIP_CATEGORY implements TIP {
        GENERAL, ITEMS, CLASSES, SKILLS,;

        String text;

        HERO_TIP_CATEGORY(String... text) {
            if (text.length > 0)
                this.text = text[0];
        }

        @Override
        public String getText() {
            if (text == null) text = TipMaster.getText(this);
            return text;

        }
    }

    public enum SHOWCASE_TOOLTIPS implements TIP {
        SHOWCASE,

        MISSION("There are 6 missions in this version, plus custom-skirmish setup mode!"),

        PAUSE,
        OPTIONS,;

        String text;

        SHOWCASE_TOOLTIPS(String... text) {
            if (text.length > 0)
                this.text = text[0];
        }

        @Override
        public String getText() {
            if (text == null) text = TipMaster.getText(this);
            return text;

        }
    }

    public interface TIP {

        String getText();
    }
}
