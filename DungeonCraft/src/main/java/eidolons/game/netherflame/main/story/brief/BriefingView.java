package eidolons.game.netherflame.main.story.brief;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.Scene;
import libgdx.GdxMaster;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.panels.TablePanelX;

public class BriefingView extends TablePanelX implements Scene {

    BriefBackground background;
    BriefText text;
    BriefWindow window;
    private FadeImageContainer plainBg;
    BriefingData briefingData;
    BriefScene scene;

    public BriefingView(BriefScene scene) {
        this();
        this.scene = scene;
        setUserObject(scene.getData());
    }

    public BriefingView() {
        setSize(1920, 1080);
        int w = GdxMaster.getWidth() / 3;
        int h = GdxMaster.getHeight() / 2;

        addActor(plainBg = new FadeImageContainer());
//        addActor
        background = new BriefBackground();
        addActor(window = new BriefWindow(w, h));
        addActor(text = new BriefText(w, h));

//        GuiEventManager.bind(GuiEventType.BRIEFING_FINISHED, p -> scene.setDone(true));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        background.act(delta);
    }

    @Override
    public void setUserObject(Object userObject) {
        briefingData = (BriefingData) userObject;

        updateAct(0);
//        super.setUserObject(userObject);
    }

    @Override
    public void updateAct(float delta) {
        window.setImages(briefingData.images);
        text.setMessages(briefingData.msgs);
        text.nextMsg();
//        text.setUserObject(briefingData.msgs[0]);
//        window.setUserObject(briefingData.images[0]);
        if (briefingData.backgroundSprite != null)
            background.setUserObject(briefingData.backgroundSprite);
        background.setAlpha(0.7f);
        plainBg.setImage(briefingData.background);
    }

    public void hide() {
        text.fadeOut();
        window.fadeOut();
    }

    @Override
    public boolean isDone() {
        return text.isDone();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        background.draw(batch, parentAlpha);
        window.setPosition(GdxMaster.centerWidth(window), GdxMaster.centerHeight(window)+120);
        text.setPosition(GdxMaster.centerWidth(text), window.getY() - text.getHeight());
        super.draw(batch, parentAlpha);
    }
}
