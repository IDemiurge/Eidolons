package main.test.libgdx.TestGameCreation;

import com.badlogic.gdx.Game;

/**
 * Created by PC on 01.11.2016.
 */
public class GameLoop extends Game {
    @Override
    public void create() {
        setScreen(new MainField());
    }
}
