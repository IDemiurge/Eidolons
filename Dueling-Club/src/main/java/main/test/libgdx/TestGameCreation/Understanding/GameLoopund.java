package main.test.libgdx.TestGameCreation.Understanding;

import com.badlogic.gdx.Game;

/**
 * Created by PC on 01.11.2016.
 */
public class GameLoopund extends Game {
    @Override
    public void create() {
        setScreen(new myUnderstandingField());
    }
}
