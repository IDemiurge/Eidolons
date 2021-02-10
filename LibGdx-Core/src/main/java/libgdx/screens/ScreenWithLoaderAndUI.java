package libgdx.screens;

import eidolons.game.netherflame.main.story.brief.IggBriefScreen;
import libgdx.gui.menu.selection.SelectionPanel;
import libgdx.gui.menu.selection.manual.ManualPanel;
import libgdx.stage.UiStage;

/**
 * Created by JustMe on 11/28/2017.
 */
public abstract class ScreenWithLoaderAndUI extends ScreenWithLoader {
    protected UiStage overlayStage;

    protected SelectionPanel selectionPanel;
    protected ManualPanel manualPanel;

    public ScreenWithLoaderAndUI() {
        super();
        overlayStage = new IggBriefScreen();
    }

    public SelectionPanel getSelectionPanel() {
        return selectionPanel;
    }

    public UiStage getOverlayStage() {
        return overlayStage;
    }

    protected void renderLoaderAndOverlays(float delta) {
        loadingStage.act(delta);
        loadingStage.draw();
        overlayStage.act(delta);
        overlayStage.draw();
        loadWaited(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        overlayStage.getRoot().setSize(width, height);
        overlayStage.getViewport().update(width, height);
        loadingStage.getRoot().setSize(width, height);
        loadingStage.getViewport().update(width, height);
    }

}
