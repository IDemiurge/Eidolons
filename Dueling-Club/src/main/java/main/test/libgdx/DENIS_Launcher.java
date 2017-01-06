package main.test.libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import main.libgdx.GameScreen;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:48
 * To change this template use File | Settings | File Templates.
 */
public class DENIS_Launcher implements ApplicationListener {

    Screen screen;

    public static void main(String[] args) {

        new LwjglApplication(new DENIS_Launcher(), getConf());
    }

    public LwjglAWTCanvas getCanvas(){

//        new LwjglApplication(new DENIS_Launcher(), lwjglApplicationConfiguration);
        return new LwjglAWTCanvas(new DENIS_Launcher(), getConf());
    }

    private static LwjglApplicationConfiguration getConf(){
        LwjglApplicationConfiguration conf = new LwjglApplicationConfiguration();
        conf.title = "demo";
        conf.useGL30 = false;

        conf.width = 1600;
        conf.height = 900;
        conf.fullscreen = false;
        return conf;
    }

    @Override
    public void create() {
         screen = new GameScreen().PostConstruct();
    }

    @Override
    public void resize(int width, int height) {
         screen.resize(width, height);
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
