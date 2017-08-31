package main.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by JustMe on 8/30/2017.
 */
public class GdxMaster {
    public static float centerWidth(Actor actor) {
        return Gdx.graphics.getWidth() / 2 - actor.getWidth()/2;
    }
    public static float centerHeight(Actor actor) {
        return Gdx.graphics.getHeight() / 2 - actor.getHeight()/2;
    }
    public static float top(Actor actor) {
        return Gdx.graphics.getHeight()   - actor.getHeight() ;
    }
    public static float right(Actor actor) {
        return Gdx.graphics.getWidth()   - actor.getWidth() ;
    }

}
