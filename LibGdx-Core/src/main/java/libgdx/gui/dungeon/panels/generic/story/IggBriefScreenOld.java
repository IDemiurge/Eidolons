package libgdx.gui.dungeon.panels.generic.story;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import eidolons.system.libgdx.datasource.ScreenData;
import libgdx.anims.fullscreen.BriefBackground;
import eidolons.game.exploration.story.cinematic.brief.BriefingData;
import libgdx.GdxMaster;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.screens.generic.ScreenWithLoader;
import libgdx.stage.StageX;
import libgdx.stage.UiStage;
import main.system.EventCallbackParam;

/**
 * getVar data
 */
public class IggBriefScreenOld extends ScreenWithLoader {

    UiStage overlay;
    StageX backgroundStage;
    BriefBackground background;
    BriefText text;
    BriefWindow window;
    private final FadeImageContainer plainBg;

    public IggBriefScreenOld() {
        overlay = new UiStage();
        backgroundStage = new StageX();
        background = new BriefBackground();
        backgroundStage.addActor(plainBg = new FadeImageContainer());

        backgroundStage.addActor(background);
    }
    @Override
    protected String getLoadScreenPath() {
        return null;
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
        if (briefingData.backgroundSprite!=null )
            background.setUserObject(briefingData.backgroundSprite);
            background.setAlpha(0.7f);
        plainBg.setImage(briefingData.background);

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
