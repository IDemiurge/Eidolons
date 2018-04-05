package eidolons.libgdx.screens;


import com.kotcrab.vis.ui.VisUI;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.menu.selection.SelectionPanel;
import eidolons.libgdx.gui.menu.selection.scenario.ScenarioSelectionPanel;
import eidolons.libgdx.screens.menu.MainMenu;
import main.entity.Entity;
import main.system.EventCallbackParam;

import java.util.List;

/**
 * Created by JustMe on 11/28/2017.
 */
public class AnimatedMenuScreen extends ScreenWithVideoLoader {

    MainMenu mainMenu;

    public AnimatedMenuScreen() {
        VisUI.load();
        mainMenu = MainMenu.getInstance();
        getOverlayStage().addActor(mainMenu);
        mainMenu.setPosition(
         GdxMaster.centerWidth(mainMenu)
         , GdxMaster.centerHeight(mainMenu));
        bindEvents();
    }

    protected void bindEvents() {

    }

    protected boolean isWaitForInput() {
        return false;
    }

    @Override
    protected void afterLoad() {
//music
        getOverlayStage().setActive(true);
    }

    @Override
    protected SelectionPanel createSelectionPanel(EventCallbackParam p) {
        return new ScenarioSelectionPanel(() -> (List<? extends Entity>) p.get()) {
            @Override
            public void closed(Object selection) {
                if (selection == null) {
                    if (mainMenu != null)
                        mainMenu.setVisible(true);
                }
                super.closed(selection);
            }
        };
    }

    @Override
    protected void preLoad() {
        super.preLoad();
//        mainMenu.setData(data);
    }

    @Override
    public void dispose() {
        super.dispose();
        getOverlayStage().setActive(false);
    }


    @Override
    protected void renderLoader(float delta) {

//        background.draw(getBatch(), 1);

        super.renderLoader(delta);
    }

    @Override
    protected boolean isVideoLoader() {
        return false;
    }


}
