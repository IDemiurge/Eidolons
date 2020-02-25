package eidolons.game.battlecraft.logic.meta.igg.story.brief;

import eidolons.libgdx.stage.UiStage;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * getVar data
 */
public class IggBriefScreen extends UiStage {

    BriefingData briefingData;
    BriefingView briefingView;

    public IggBriefScreen() {
        super();
        addActor(briefingView = new BriefingView());
        GuiEventManager.bind(GuiEventType.BRIEFING_START, p -> setBriefingData((BriefingData) p.get()));

        GuiEventManager.bind(GuiEventType.BRIEFING_FINISHED, p -> hide());
    }

    private void hide() {
        briefingView.hide();
    }

    public void setBriefingData(BriefingData briefingData) {
        this. briefingData=briefingData;
        briefingView.setUserObject(briefingData);
    }

    @Override
    public void draw() {
        super.draw();
    }

}
