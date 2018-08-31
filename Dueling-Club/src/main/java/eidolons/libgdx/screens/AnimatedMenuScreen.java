package eidolons.libgdx.screens;


import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.menu.selection.SelectionPanel;
import eidolons.libgdx.gui.menu.selection.rng.RngSelectionPanel;
import eidolons.libgdx.gui.menu.selection.saves.SaveSelectionPanel;
import eidolons.libgdx.gui.menu.selection.scenario.ScenarioSelectionPanel;
import eidolons.libgdx.screens.menu.MainMenu;
import main.content.DC_TYPE;
import main.entity.Entity;
import main.system.EventCallbackParam;

import java.util.List;

/**
 * Created by JustMe on 11/28/2017.
 */
public class AnimatedMenuScreen extends ScreenWithVideoLoader {

    MainMenu mainMenu;

    public AnimatedMenuScreen() {
        initMenu();
    }

    protected void initMenu() {
        getOverlayStage().clear();
        mainMenu = MainMenu.getInstance();
        mainMenu.setVisible(true);
        getOverlayStage().addActor(mainMenu);
        mainMenu.setPosition(
         GdxMaster.centerWidth(mainMenu)
         , GdxMaster.centerHeight(mainMenu));
    }

    @Override
    public void backToLoader() {
        initMenu();
        super.backToLoader();
        getOverlayStage().setActive(true);
    }

    protected boolean isWaitForInput() {
        return false;
    }

    @Override
    protected void afterLoad() {
        getOverlayStage().setActive(true);
    }
    protected void back() {
        if (mainMenu != null)
            mainMenu.setVisible(true);
    }
    @Override
    protected SelectionPanel createSelectionPanel(EventCallbackParam p) {
        List<? extends Entity> list = (List<? extends Entity>) p.get();
            if (isLoadGame(list)){
            return new SaveSelectionPanel(() -> list) {
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
        if (isRngScenario(list)){
            return new RngSelectionPanel(() -> (List<? extends Entity>) p.get()) {
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

    private boolean isLoadGame(List<? extends Entity> p) {
        return p.get(0).getOBJ_TYPE_ENUM()== DC_TYPE.CHARS;
    }

    private boolean isRngScenario(List<? extends Entity> p) {
        return p.get(0).getGroupingKey().equalsIgnoreCase("Random");
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
    protected boolean isLoadingWithVideo() {
        return false;
    }


}
