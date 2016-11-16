package main.test.libgdx.prototype;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * Created by PC on 07.11.2016.
 */
public class Launcher {
    static LwjglApplication app;
    public static int wid = 1200;
    public static int hei = 675;
    public static void main(String[] args) {
        LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
        configuration.samples = 3;
//        configuration.resizable = false;
        configuration.width = wid;
        configuration.height = hei;
        app =  new LwjglApplication (new GameLoop(),configuration);
    }
}
