package elements.content.enums.types;

/**
 * Created by Alexander on 8/20/2023
 */
public class EntityTypes {
    public enum EntityType {
        ACTION,
        UNIT,
        HERO, //an entity on top, used for Progression?
        OMEN, //do we need an entity there?
        OBSTACLE,

        // ++ NON-COMBAT!!!

    }

    public enum ActionType {
        Extra_Power_Attack,
        Extra_Standard_Attack,
        Power_Attack,
        Standard_Attack,
        Defense, //DEF?
    }

    public enum PassiveType {
        Perk,
        Quality,
        Unique,
        Effect, //from something?

    }
}
