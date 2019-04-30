package eidolons.game.battlecraft.logic.meta.igg.story.brief;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenWithLoader;
import eidolons.libgdx.stage.StageX;
import eidolons.libgdx.stage.UiStage;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * get data
 */
public class IggBriefScreen extends UiStage {

    BriefBackground background;
    BriefText text;
    BriefWindow window;
    private FadeImageContainer plainBg;

    public IggBriefScreen() {
        GuiEventManager.bind(GuiEventType.BRIEFING_START, p -> initBriefing((BriefingData) p.get()));
    }

    public void initBriefing(BriefingData briefingData) {

        int w = GdxMaster.getWidth() / 3;
        int h = GdxMaster.getHeight() / 2;

        addActor(plainBg = new FadeImageContainer());
        addActor(background = new BriefBackground());
        addActor(window = new BriefWindow(w, h, briefingData.images));
        addActor(text = new BriefText(w, h, briefingData.msgs));
        window.setPosition(GdxMaster.centerWidth(window), GdxMaster.centerHeight(window));
        text.setPosition(GdxMaster.centerWidth(text), GdxMaster.centerHeight(text));
        if (briefingData.backgroundSprite != null)
            background.setUserObject(briefingData.backgroundSprite);
        background.setAlpha(0.7f);
        plainBg.setImage(briefingData.background);

    }

    @Override
    public void draw() {
        if (background != null) {
            background.draw(getBatch(), 1);
        }
        super.draw();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
