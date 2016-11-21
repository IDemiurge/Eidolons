package main.test.libgdx.prototype;

import com.badlogic.gdx.Game;

/**
 * Created by PC on 07.11.2016.
 */
public class GameLoop extends Game {
    @Override
    public void create() {
        Res_holder.load();
        setScreen(new PrototypeScreen());
//        setScreen(new MeshScreenTest());
    }
}
