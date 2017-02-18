package main.libgdx.anims.controls;

/**
 * Created by JustMe on 2/10/2017.
 */
public interface Controller {

    boolean charTyped(char c);

    public enum CONTROLLER {
        ACTION, ANIM, EMITTER,
        DEBUG,
        RULES,

    }
}
