package libgdx.controls;

/**
 * Created by JustMe on 2/10/2017.
 */
public interface Controller {

    boolean charTyped(char c);

    default boolean keyDown(int keyCode) {

        return false;
    }

    enum CONTROLLER {
        ACTION, ANIM, EMITTER,
        DEBUG,
        RULES,

    }
}
