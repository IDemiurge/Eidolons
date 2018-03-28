package main.libgdx.launch.prestart;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.libgdx.GdxMaster;

/**
 * Created by JustMe on 11/28/2017.
 */
public class PrestartScreen extends ScreenAdapter {
    PrestartMenu menu;
    Stage stage;
    private boolean initialized;

    public PrestartScreen() {
    }

    private void init() {
        stage = new Stage();
        menu = new PrestartMenu();
        stage.addActor(menu);
        menu.setPosition(GdxMaster.centerWidth(menu), GdxMaster.centerHeight(menu));
        Gdx.input.setInputProcessor(stage);
        initialized = true;
    }


    @Override
    public void show() {
        init();
    }

    @Override
    public void render(float delta) {
        if (!initialized)
            init();
        stage.act(delta);
        stage.draw();
    }


    @Override
    public void dispose() {

    }
}
