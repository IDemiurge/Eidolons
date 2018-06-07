package eidolons.system.utils;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

/**
 * Created by JustMe on 5/13/2018.
 */
public abstract class GdxUtil implements ApplicationListener {
    @Override
    public void create() {
        execute();
        if (isExitOnDone())
        Gdx.app.exit();
    }

    protected boolean isExitOnDone() {
        return true;
    }

    public void start(){
    new LwjglApplication(this, "", getWidth(), getHeight());
}

    protected int getHeight() {
        return 1;
    }

    protected int getWidth() {
        return 1;
    }

    protected abstract void execute();

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
