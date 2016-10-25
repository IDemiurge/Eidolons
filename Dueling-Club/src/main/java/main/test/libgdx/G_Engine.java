package main.test.libgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import main.data.filesys.PathFinder;

import java.util.Random;

/**
 * Created by PC on 21.10.2016.
 */
public class G_Engine extends Game {

    Pixmap pixmap;
//    PathFinder path;
    @Override
    public void create() {
        System.out.println("create");
        Gdx.graphics.setTitle("lol");
        setScreen(new Menu(this));
        System.out.println("set new MENU");

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
