package main.libgdx;

import java.util.function.Consumer;

/**
 * All methods with callbacks is blocking
 * and his implementation must be fastest as possible
 * move logic for this methods to game loop\another thread
 * and call callback then its done
 */
public interface Engine {
    /**
     * called then game started
     *
     * @param onDone must call this callback then init is done
     */
    void init(Runnable onDone);

    /**
     * get meta data for save\campaing\scenario by name
     * <b>ATTENTION</b> blocking method! must work as fucking lighting!
     *
     * @param name name of save\campaing\scenario
     * @return
     */
    ScreenData getMeta(String name);

    /**
     * calls then user select save\new game
     *
     * @param meta
     * @param onDone must call this callback then load is done
     * @return
     */
    void load(ScreenData meta, Runnable onDone);

    /**
     * calls then user want to close game
     */
    void exit();

    /**
     * empty description for now
     *
     * @param onFail
     */
    void onFail(Consumer<OnEngineFail> onFail);
}
