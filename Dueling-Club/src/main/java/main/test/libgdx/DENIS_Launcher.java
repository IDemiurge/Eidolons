package main.test.libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import main.client.dc.Launcher;
import main.libgdx.DungeonScreen;

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

    private static LwjglApplicationConfiguration getConf() {
        LwjglApplicationConfiguration conf = new LwjglApplicationConfiguration();
        conf.title = "Eidolons: Battlecraft v" + Launcher.VERSION;
        conf.useGL30 = true;

        conf.width = 1600;
        conf.height = 900;
        conf.fullscreen = false;

//        conf.width = GuiManager.getScreenWidthInt();
//        conf.height = GuiManager.getScreenHeightInt();
//        conf.fullscreen = true;
        return conf;
    }

    @Override
    public void create() {
        screen = new DungeonScreen().PostConstruct();
    }

    @Override
    public void resize(int width, int height) {
        screen.resize(width, height);
    }

    @Override
    public void render() {
        screen.render(Gdx.graphics.getDeltaTime());
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
