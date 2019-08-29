package main.content.enums.entity;

/**
 * Created by JustMe on 2/14/2017.
 */
public class AbilityEnums {
    public enum ABILITY_GROUP {
        DAMAGE, BUFF, STD_ACTION, MOVE, ONESHOT,TEMPLATE_ACTIVE,

        VALUE_MOD, TRIGGER, PASSIVE, STD_PASSIVE, TEMPLATE_PASSIVE, CUSTOM_PASSIVE

        // EXTENDED: GROUPS INTO TYPES, THEN SPLIT FURTHER

    }

    public enum ABILITY_TYPE {
        ACTIVES, PASSIVES
        // , OBJ
    }

    // SYNC WITH TARGETING?
    public enum EFFECTS_WRAP {
        SINGLE_BUFF, CHAIN, ZONE, CUSTOM_ZONE_STAR, CUSTOM_ZONE_CROSS, ZONE_RING,
    }

    public enum TARGETING_MODE {
        FRONT,

        SELF, SINGLE, MULTI,

        DOUBLE, TRIPPLE, CELL,

        RAY,
        RAY_AUTO,
        THREE_RAY_AUTO,
        // TODO ARGS - N_OF_JUMPS ETC???
        WAVE,
        NOVA,
        BLAST,
        CORPSE,
        TRAP,
        BF_OBJ,
        ANY_UNIT,
        ANY_ENEMY,
        ANY_ALLY,
        ALL,
        ALL_UNITS,
        ALL_ENEMIES,
        ALL_ALLIES,
        BOLT_ANY,
        BOLT_UNITS,
        SPRAY,
        // GLOBAL_NOT_SELF,
        MY_WEAPON,
        MY_ARMOR,
        MY_ITEM,
        ENEMY_WEAPON,
        ENEMY_ARMOR,
        ENEMY_ITEM,
        ANY_ITEM,
        ANY_WEAPON,
        ANY_ARMOR,

    }

    public enum TARGETING_MODIFIERS {
        NOT_SELF,
        NO_FRIENDLY_FIRE,
        NO_UNDEAD,
        ONLY_UNDEAD,
        NO_LIVING,
        ONLY_LIVING,
        CLEAR_SHOT,
        FACING,
        RANGE,
        SPACE,
        VISION,
        NO_VISION,

        SNEAKY,
        ONLY_EVIL,
        NO_EVIL,
        NO_ENEMIES,
        NO_WALLS,
        NO_WATER,
        NO_STEALTH, PUSHABLE, NO_NEUTRALS

    }
}
