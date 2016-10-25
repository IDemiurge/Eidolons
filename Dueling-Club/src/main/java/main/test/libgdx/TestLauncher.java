package main.test.libgdx;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * Created by PC on 21.10.2016.
 */
public class TestLauncher {
    static LwjglApplication app;
    public static void main(String[] args) {
        LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
        configuration.samples = 3;
        configuration.resizable = false;
        configuration.width = 800;
        configuration.height = 600;
       app =  new LwjglApplication (new G_Engine(),configuration);
    }
}
