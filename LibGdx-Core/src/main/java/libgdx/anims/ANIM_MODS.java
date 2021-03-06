package libgdx.anims;

/**
 * Created by JustMe on 1/9/2017.
 */
public class ANIM_MODS {

    public enum ANIM_SHAPE implements ANIM_MOD {

        THRUST,
        SWING, //flash arc, icon, impact, text
        ZIGZAG,


    }

    public enum COLOR_ANIMS implements ANIM_MOD {
        DARKEN, COLORIZE,
    }

    public enum CONTINUOUS_ANIM_MODS implements ANIM_MOD {
        PENDULUM_ALPHA,
        PENDULUM_OFFSET_X,

    }

    public enum GLOBAL_ANIMS implements ANIM_MOD {
        QUAKE,
    }

    public enum ICON_ANIMS implements ANIM_MOD {
        FADE,
    }

    public enum OBJ_ANIMS implements ANIM_MOD {
        SHAKE, FADE_IN,
    }

    public enum SPELL_ANIMS implements ANIM_MOD {
        MISSILE,
        SNAKE,
        IMPACT,

    }

    //    public enum  ANIM_MOD {
//        FADE,QUAKE,DARKEN, COLORIZE,
//    }
    public enum TEXT_ANIMS implements ANIM_MOD {
        FADE,
    }


    public interface ANIM_MOD {

    }

}
