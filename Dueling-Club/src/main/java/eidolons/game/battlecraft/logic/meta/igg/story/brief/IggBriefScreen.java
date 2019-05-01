package eidolons.game.battlecraft.logic.meta.igg.story.brief;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.stage.UiStage;
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
    BriefingData briefingData;
    private boolean dirty;

    public IggBriefScreen() {
        super();
        GuiEventManager.bind(GuiEventType.BRIEFING_START, p -> setBriefingData((BriefingData) p.get()));

        GuiEventManager.bind(GuiEventType.BRIEFING_FINISHED, p -> hide());
    }

    private void hide() {
        text.fadeOut();
        window.fadeOut();
    }

    public void setBriefingData(BriefingData briefingData) {
        this. briefingData=briefingData;
        int w = GdxMaster.getWidth() / 3;
        int h = GdxMaster.getHeight() / 2;
        addActor(plainBg = new FadeImageContainer());
        addActor(background = new BriefBackground());
        addActor(window = new BriefWindow(w, h, briefingData.images));
        addActor(text = new BriefText(w, h, briefingData.msgs));
        dirty = true;
    }
    @Override
    public void act(float delta) {
        super.act(delta);
        if (dirty) {
            initBriefing();
        }
    }
        public void initBriefing() {
        window.setPosition(GdxMaster.centerWidth(window), GdxMaster.centerHeight(window));
        text.setPosition(GdxMaster.centerWidth(text), window.getY()- text.getHeight());
        if (briefingData.backgroundSprite != null)
            background.setUserObject(briefingData.backgroundSprite);
        background.setAlpha(0.7f);
        plainBg.setImage(briefingData.background);
            dirty = false;
    }

    @Override
    public void draw() {
        if (background != null) {
            background.draw(getBatch(), 1);
        }
        super.draw();
    }

}
