package main.test.libgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;

/**
 * Created by PC on 21.10.2016.
 */
public class G_Engine extends Game {

    Pixmap pixmap;
//    PathFinder path;
    @Override
    public void create() {

        Gdx.graphics.setTitle("G_Engine");
        setScreen(new BattleFieldScreen(this));


    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public G_Engine() {
    }

    @Override
    public Screen getScreen() {
        return super.getScreen();
    }

    @Override
    public void pause() {
        super.pause();
    }

//    @Override
//    public void render() {
////        System.out.println("render");
//
//    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void setScreen(Screen screen) {
        super.setScreen(screen);
    }
}
