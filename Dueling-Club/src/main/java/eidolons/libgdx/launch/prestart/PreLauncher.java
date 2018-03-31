package eidolons.libgdx.launch.prestart;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.launch.Showcase;
import eidolons.client.dc.Launcher;
import main.data.filesys.PathFinder;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.libgdx.launch.JavaProcess;

import java.io.IOException;

/**
 * Created by JustMe on 11/28/2017.
 */
public class PreLauncher extends Game {

    private static boolean fullscreen;

    public PreLauncher() {
    }

    public static void gameLaunched() {
//        Gdx.app.getApplicationListener().dispose();
        LwjglApplicationConfiguration.disableAudio = false;
        Gdx.app.exit();
//        new Thread(new Runnable() {             public void run() {
//        DemoLauncher.main(null );
//        }       }, " thread").start();
        try {
            int res = JavaProcess.exec(Showcase.class);
        } catch (IOException e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } catch (InterruptedException e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
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

        LwjglApplicationConfiguration.disableAudio = true;
        conf.title = "Eidolons: Battlecraft v" + Launcher.VERSION;
        conf.forceExit = false;
        conf.resizable = false;
        conf.width = 600;
        conf.height = 800;
        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");

        conf.useGL30 = true;
        conf.initialBackgroundColor = (new Color(0, 0, 0, 0.1f));
        conf.resizable = false;
        conf.resizable = false;
        try {
            conf.addIcon(PathFinder.getImagePath() + "mini/new/logo32.png", FileType.Absolute);
            conf.addIcon(PathFinder.getImagePath() + "mini/new/logo64.png", FileType.Absolute);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return conf;
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


}
