package eidolons.libgdx.screens;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import eidolons.macro.entity.town.Town;
import main.system.EventCallbackParam;

/**
 * Created by JustMe on 10/15/2018.
 */
public abstract class GameScreenWithTown extends GameScreen {

    protected TownPanel townPanel;

    protected void showTownPanel(EventCallbackParam p) {

        if (p.get() == null) {
            townPanel.fadeOut();
            overlayStage.setActive(false);
            GdxMaster.setLoadingCursor();
            TownPanel.setActiveInstance(null);
            updateInputController();
            guiStage.setTown(false);
            if (!isTownInLoaderOnly())
                loading = false;

        } else {
            loading = true;
            Town town = (Town) p.get();
            if (townPanel == null || TownPanel.TEST_MODE) {
                overlayStage.addActor(townPanel = new TownPanel());
            } else {
                townPanel.setVisible(true);
            }
            townPanel.setUserObject(town);

            overlayStage.setActive(true);
            GdxMaster.setDefaultCursor();
            TownPanel.setActiveInstance(townPanel);
            updateInputController();
            guiStage.setTown(true);
        }
    }

    protected abstract boolean isTownInLoaderOnly();

    @Override
    protected void renderLoaderAndOverlays(float delta) {
        super.renderLoaderAndOverlays(delta);
        if (townPanel != null&&townPanel.isVisible()){
            guiStage.act(delta);
            guiStage.draw();
        }
    }
}
