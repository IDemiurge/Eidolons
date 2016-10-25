package main.test.libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:48
 * To change this template use File | Settings | File Templates.
 */
public class DENIS_Launcher implements ApplicationListener {

    Screen screen;

    public static void main(String[] args) {
        LwjglApplicationConfiguration lwjglApplicationConfiguration = new LwjglApplicationConfiguration();
        lwjglApplicationConfiguration.title = "demo";
        lwjglApplicationConfiguration.useGL30 = true;

        lwjglApplicationConfiguration.width = 1680;
        lwjglApplicationConfiguration.height = 1050;
        lwjglApplicationConfiguration.fullscreen = false;

        new LwjglApplication(new DENIS_Launcher(), lwjglApplicationConfiguration);
    }

    @Override
    public void create() {
         screen = new GameScreen().PostConstruct();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
         screen.render(0);
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
