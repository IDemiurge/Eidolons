package main.libgdx.anims;

/**
 * Created by JustMe on 1/9/2017.
 */
public class STD_ANIMS {

    public interface  ANIM{

    }
    public enum ATK_ANIMS implements ANIM {

    THRUST,
    SWING,
    ZIGZAG,

}
    public enum TEXT_ANIMS implements ANIM {
        FADE,
    }
    public enum ICON_ANIMS implements ANIM {
        FADE,
    }
    public enum COLOR_ANIMS implements ANIM {
DARKEN, COLORIZE,
    }
    public enum GLOBAL_ANIMS implements ANIM {
QUAKE,
    }
    public enum OBJ_ANIMS implements ANIM {
SHAKE,
    }

        public enum SPELL_ANIMS implements ANIM {
        MISSILE,
        SNAKE,
        IMPACT,

    }
    }
