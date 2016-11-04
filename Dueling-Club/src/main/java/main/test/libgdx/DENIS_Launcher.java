package main.test.libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
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
       new LwjglApplication(new DENIS_Launcher(), getConf());
    }

    public LwjglAWTCanvas getCanvas(){

//        new LwjglApplication(new DENIS_Launcher(), lwjglApplicationConfiguration);
        return new LwjglAWTCanvas(new DENIS_Launcher(), getConf());
    }

    private static LwjglApplicationConfiguration getConf(){
        LwjglApplicationConfiguration lwjglApplicationConfiguration = new LwjglApplicationConfiguration();
        lwjglApplicationConfiguration.title = "demo";
        lwjglApplicationConfiguration.useGL30 = true;

/*        lwjglApplicationConfiguration.width = 1920;
        lwjglApplicationConfiguration.height = 1080;*/

        lwjglApplicationConfiguration.width = 1600;
        lwjglApplicationConfiguration.height = 900;
        lwjglApplicationConfiguration.fullscreen = false;

        return lwjglApplicationConfiguration;
    }

    @Override
    public void create() {
/*        World world;
        world = new World(new Vector2(0,-10),true);*/
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
