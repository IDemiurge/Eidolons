package eidolons.game.battlecraft.logic.meta.igg.story.brief;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenWithLoader;
import eidolons.libgdx.stage.StageX;
import eidolons.libgdx.stage.UiStage;
import main.system.EventCallbackParam;

/**
 * get data
 */
public class IggBriefScreen extends ScreenWithLoader {

    UiStage overlay;
    StageX backgroundStage;
    BriefBackground background;
    BriefText text;
    BriefWindow window;

    public IggBriefScreen() {
        overlay = new UiStage();
        backgroundStage = new StageX();
        background = new BriefBackground();
        backgroundStage.addActor(background);
    }

    @Override
    public void setData(ScreenData data) {
        super.setData(data);
        BriefingData briefingData = (BriefingData) data.getParams().get();
//        background.setUserObject(briefingData);

        int w = GdxMaster.getWidth() / 3;
        int h = GdxMaster.getHeight() / 2;

        text = new BriefText(w, h, briefingData.msgs);
        window = new BriefWindow(w, h, briefingData.images);
        window.setPosition(GdxMaster.centerWidth(window), GdxMaster.centerHeight(window));
        text.setPosition(GdxMaster.centerWidth(text), GdxMaster.centerHeight(text));
        background.setUserObject(briefingData.background);

        overlay.addActor(window);
        overlay.addActor(text);
    }


    @Override
    protected void renderMain(float delta) {
        super.renderMain(delta);
        backgroundStage.act(delta);
        overlay.act(delta);
        backgroundStage.draw();
        overlay.draw();

    }

    @Override
    protected InputProcessor createInputController() {
        return new InputMultiplexer(loadingStage, overlay);
    }

    @Override
    protected void preLoad() {
       
//        super.preLoad();
        //start ambi, stop other music
    }

    @Override
    protected void done(EventCallbackParam param) {
        super.done(param);
       
        //start music
    }

    @Override
    protected void triggerInitialEvents() {
        super.triggerInitialEvents();
    }


    @Override
    public void initLoadingStage(ScreenData meta) {
        super.initLoadingStage(meta);
    }


    @Override
    protected void afterLoad() {

       
    }
}
