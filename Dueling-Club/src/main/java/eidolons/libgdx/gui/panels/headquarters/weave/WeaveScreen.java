package eidolons.libgdx.gui.panels.headquarters.weave;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.panels.headquarters.weave.ui.WeaveUi;
import eidolons.libgdx.screens.GameScreen;
import eidolons.libgdx.texture.Images;

/**
 * Created by JustMe on 6/4/2018.
 */
public class WeaveScreen extends GameScreen {

    WeaveSpace space;
    WeaveUi ui;
    InputProcessor inputController;
    private static WeaveScreen instance;
    private FadeImageContainer background;

    public static WeaveScreen getInstance() {
        if (instance == null) {
            instance= new WeaveScreen();
        }
        return instance;
    }


    @Override
    protected void preLoad() {
        setCam(new OrthographicCamera());
         background = new FadeImageContainer(Images.WEAVE_BACKGROUND);
        space = new WeaveSpace(getCam());
//        new FillViewport(GdxMaster.getWidth(),
//         GdxMaster.getHeight(),  new OrthographicCamera())
        ui = new WeaveUi(new ScreenViewport(), getBatch());
    }

    @Override
    protected void afterLoad() {
    }

    @Override
    public InputProcessor createInputController() {
        if (inputController == null)
            inputController =
             new InputMultiplexer(
              new WeaveInputController(getCam()),
              ui, space,
              new WeaveKeyController());
        return inputController;
    }

    @Override
    protected void renderMain(float delta) {
//        super.renderMain(delta);
//        swirl.act(delta);
//        swirl.draw();
        space.act(delta);
        ui.act(delta);
        getBatch().begin();
        background.draw(getBatch(), 1f);
        getBatch().end();
        space.draw();
        ui.draw();
    }
    @Override
    public void reset() {
        super.reset();
    }


    @Override
    protected boolean isLoadingWithVideo() {
        return super.isLoadingWithVideo();
    }

    public WeaveSpace getSpace() {
        return space;
    }
}
