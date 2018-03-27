package main.system.controls;

/**
 * Created by JustMe on 2/10/2017.
 */
public interface Controller {

    boolean charTyped(char c);

    default void keyDown(int keyCode){

    }

    enum CONTROLLER {
        ACTION, ANIM, EMITTER,
        DEBUG,
        RULES,

    }
}
