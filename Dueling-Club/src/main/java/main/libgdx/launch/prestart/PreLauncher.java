package main.libgdx.launch.prestart;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import main.client.dc.Launcher;
import main.data.filesys.PathFinder;
import main.game.battlecraft.DC_Engine;
import main.game.core.Eidolons;
import main.libgdx.launch.JavaProcess;
import main.libgdx.launch.Showcase;

import java.io.IOException;

/**
 * Created by JustMe on 11/28/2017.
 */
public class PreLauncher extends Game{

    private static boolean fullscreen;

    public static void gameLaunched( ) {
//        Gdx.app.getApplicationListener().dispose();
        LwjglApplicationConfiguration.disableAudio=false;
        Gdx.app.exit();
//        new Thread(new Runnable() {             public void run() {
//        DemoLauncher.main(null );
//        }       }, " thread").start();
        try {
            int res = JavaProcess.exec(Showcase.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public PreLauncher() {
    }

    @Override
    public void create() {
        setScreen(new PrestartScreen());

    }

    @Override
    public void dispose() {
        getScreen().dispose();
        super.dispose();
    }

    @Override
    public void render() {
super.render();
    }
    public static void main(String[] args) {
        prestart();

    }

    public static void prestart() {
        DC_Engine.systemInit();
        Eidolons.setApplication(new LwjglApplication(new PreLauncher(),
         getConf()));
    }

    public static LwjglApplicationConfiguration getConf() {
        LwjglApplicationConfiguration conf = new LwjglApplicationConfiguration();

        LwjglApplicationConfiguration.disableAudio=true;
        conf.title = "Eidolons: Battlecraft v" + Launcher.VERSION;
        conf.forceExit = false;
        conf.resizable=false;
        conf.width=600;
        conf.height=800;
        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");

        conf.useGL30 = true;
        conf.initialBackgroundColor=(new Color(0, 0, 0, 0.1f));
        conf.resizable=false;
        conf.resizable=false; try {
            conf.addIcon(PathFinder.getImagePath() + "mini/new/logo32.png", FileType.Absolute);
            conf.addIcon(PathFinder.getImagePath() + "mini/new/logo64.png", FileType.Absolute);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conf;
    }


}