package main.content;

import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_MATERIAL_GROUP;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.values.parameters.PARAMETER;
import main.data.ability.construct.VarHolder;
import main.data.ability.construct.VariableManager;
import main.entity.Ref.KEYS;
import main.game.battlefield.Coordinates.UNIT_DIRECTION;

public class CONTENT_CONSTS {

    public enum COLOR_THEME {
        BLUE, GREEN, RED, DARK, LIGHT, YELLOW, PURPLE {
            public String getSuffix() {
                return " v";
            }
        };

        COLOR_THEME() {
            // path suffixes
        }

        public String getSuffix() {
            return (" " + name().charAt(0)).toLowerCase();
        }
    }

    public enum DEITY {
        NIGHT_LORD, UNDERKING, ARACHNIA, KEEPER_OF_TWILIGHT, DARK_GODS,

        FAITHLESS, FIDE_ARCANUM, LORD_OF_MAGIC, WITCH_KING, IRON_GOD, VOID_LORD,

        ANNIHILATOR, CONSUMER_OF_WORLDS, WARMASTER, QUEEN_OF_LUST,

        SKY_QUEEN, REDEEMER, ALLFATHER, THREE_DIVINES,

        CRIMSON_QUEEN, WRAITH_GOD, DEATH_GORGER, LORD_OF_DECAY,

        FEY_QUEEN, WORLD_TREE, WILD_GOD, WORLD_SERPENT, OLD_WAY
    }

    public enum DIMENSION {
        T1_2, T1_3, T1_4, T1_5, T1_7, T1_10, T2_3, T3_4,

        W2_1, W3_1, W4_1, W5_1, W7_1, W10_1, W3_2, W4_3,
    }

    public enum DYNAMIC_BOOLS {

        BACKWARDS_ROUTE_TRAVEL, AI_CONTROLLED, PLAYER_CONTROLLED, FLIPPED,
    }

    public enum FLIP {
        HORIZONTAL, VERTICAL, BOTH, CCW90, CW90, CCW180, CW180
    }

    public enum OBJECT_ARMOR_TYPE {
        STONE, WOOD, METAL, FLESH, BONE, CRYSTAL, AETHER, ETHEREAL,;

        public ITEM_MATERIAL_GROUP getGroup() {
            switch (this) {
                case STONE:
                    return ItemEnums.ITEM_MATERIAL_GROUP.STONE;
                case BONE:
                    return ItemEnums.ITEM_MATERIAL_GROUP.BONE;
                case FLESH:
                    return ItemEnums.ITEM_MATERIAL_GROUP.NATURAL;
                case METAL:
                    return ItemEnums.ITEM_MATERIAL_GROUP.METAL;
                case WOOD:
                    return ItemEnums.ITEM_MATERIAL_GROUP.WOOD;
                case CRYSTAL:
                    return ItemEnums.ITEM_MATERIAL_GROUP.CRYSTAL;
                case AETHER:
                    return ItemEnums.ITEM_MATERIAL_GROUP.CRYSTAL;
                case ETHEREAL:
                    break;
            }
            return null;
        }

    }

    public enum RANK {
        NEW_HERO(1), NEOPHYTE(2), DISCIPLE(10), ADEPT(20), CHAMPION(30), AVATAR(40);

        RANK(int hero_level) {

        }
    }

    public enum RETAIN_CONDITIONS {
        CASTER_ALIVE, CASTER_FOCUS_REQ, CASTER_CONSCIOUS, TARGET_MATCHES_TARGETING_FILTER, // DISTANCE/FACING/VISION?

    }

    public enum SOUNDSET {
        HUMAN, ROGUE, BLACKGUARD, FEMALE, WIZARD,

        ANGEL, DAEMON, HORROR, BEAST, ABOMINATION, WOLF, BAT, WRAITH, GHOST, ZOMBIE, CREATURE,

        // SPELL
        ARCANE,
        ARCANE1, // ELDRITCH SORCERY
        ARCANE2, // ILLUSION ENCHANTMENT

        DARK,

        DEATH,

        CHAOS,

        LIGHT,;

        public String getName() {
            return name();
        }
    }

    public enum SPECIAL_REQUIREMENTS implements VarHolder {
        // boolean
        CUSTOM("condition string", "[AV(condition)]", String.class),
        PARAM("param;amount", VariableManager.getVarIndex(0) + " required: "
         + VariableManager.getVarIndex(1), PARAMETER.class, String.class),
        // PARAM_NOT("param;amount", VariableManager.getVarIndex(0)
        // + " required: " + VariableManager.getVarIndex(1), PARAMETER.class,
        // String.class),
        COUNTER("COUNTER;amount", VariableManager.getVarIndex(0) + " needed: "
         + VariableManager.getVarIndex(1), String.class, String.class),
        // COUNTER_NOT("COUNTER;amount", VariableManager.getVarIndex(0)
        // + " needed: " + VariableManager.getVarIndex(1), String.class,
        // String.class),

        HAS_ITEM("Item slot", "Must have an item in " + VariableManager.getVarIndex(0), ITEM_SLOT.class),
        ITEM("Item slot;property;value", "Must have an item in " + VariableManager.getVarIndex(0)
         + " with " + VariableManager.getVarIndex(1) + " matching "
         + VariableManager.getVarIndex(2), ITEM_SLOT.class, VariableManager.PROP_VAR_CLASS, VariableManager.PROP_VALUE_VAR_CLASS),
        NOT_ITEM("Item slot;property;value", "Must not have an item in "
         + VariableManager.getVarIndex(0), ITEM_SLOT.class, VariableManager.PROP_VAR_CLASS, VariableManager.PROP_VALUE_VAR_CLASS),
        FREE_CELL("Relative position of the cell", "The cell " + VariableManager.getVarIndex(0)
         + " must be free ", UNIT_DIRECTION.class),

        FREE_CELL_RANGE("Relative position of the cell", "The cell "
         + VariableManager.getVarIndex(1) + " spaces " + VariableManager.getVarIndex(0)
         + " must be free ", UNIT_DIRECTION.class, VariableManager.NUMBER_VAR_CLASS),

        NOT_FREE_CELL("Relative position of the cell", "The cell " + VariableManager.getVarIndex(0)
         + " must be occupied", UNIT_DIRECTION.class),

        REF_NOT_EMPTY("obj to check;ref to check", VariableManager.getVarIndex(1) + " is missing!", String.class, KEYS.class),;

        private String text;
        private Object[] vars;
        private String variableNames;

        SPECIAL_REQUIREMENTS(String variableNames, String text, Object... vars) {
            this.vars = (vars);
            this.text = text;
            this.setVariableNames(variableNames);
        }

        public String getText(Object... variables) {
            if (variables.length < 1) {
                return text;
            }
            return VariableManager.getVarText(text, variables);

        }

        @Override
        public Object[] getVarClasses() {
            return vars;
        }

        @Override
        public String getVariableNames() {
            return variableNames;
        }

        public void setVariableNames(String variableNames) {
            this.variableNames = variableNames;
        }

    }

    // public enum CV_

    // why do i need templates? for AV mostly; otherwise CV can be made up of
    // just a bunch of variables...
    // just define the order in which they are given; and some *types* of CV's -
    // MOD vs BONUS at least
    // then also application targets... actives, spells, in the future maybe
    // summoned units etc

    // use cases: 1) reduce cost 2) increase Spellpower/mastery: apply when?

    public enum TARGET_KEYWORD {
        SOURCE, TARGET, SUMMONER, BASIS, WEAPON, ARMOR, SPELL, ACTIVE
    }

}
