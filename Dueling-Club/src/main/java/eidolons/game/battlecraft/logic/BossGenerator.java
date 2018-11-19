package eidolons.game.battlecraft.logic;

/**
 * Created by JustMe on 11/17/2018.
 */
public class BossGenerator {

    public enum BOSS_SUPPORT{
        WAVES, CONTINUOUS, ONETIME, AID_ON_FALL,
    }
    public enum BOSS_ENVIRONMENT{
        BARRAGE,
        SHRINKING_SPACE,
        DEADSPACE, // AWAY FROM A FEW BEACONS OF LIGHT, THERE IS ONLY DEATH AND DARKNESS
        HAZE, //VISIBILITY IS HEAVILY IMPAIRED
        FUGUE, //MOVEMENT IS SKEWED, THE GROUND IS SHAKING!

    }
    public enum BOSS_WEAKNESS{
        LIFE_BOUND_TO_OBJECT,

    }
    public enum BOSS_LIFE_MECHANIC{
        FAIR,
        REVIVE_DISPLACE,
        REVIVE_PUSH_OUT,
        REVIVE_ALTER_STAGE,
        IMMORTAL,
        IMMORTAL_BUT_DELAYED,

    }

        public enum BOSS_QUALITY{
        INVULNERABLE_WHILE_OBJ_REMAINS,

        SPAWN_ILLUSIONS,
        MIRROR_HERO,

    }
}
