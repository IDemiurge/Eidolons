package main.level_editor.gui.top;

import eidolons.libgdx.GDX;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.level_editor.gui.panels.control.MenuPanel;
import main.level_editor.gui.screen.LE_Screen;

public class TopPanel extends TablePanelX {
    private final MenuPanel menuPanel;
    private boolean fullControlPanelInitialized;

    public TopPanel() {

        menuPanel = new MenuPanel();
        add(menuPanel.getTable()).fillX().expandX().top();

        GDX.top(menuPanel.getTable());
    }

    @Override
    public void act(float delta) {
        if (!fullControlPanelInitialized) {
            menuPanel.initControlMenus(LE_Screen.getInstance().getGuiStage().getControlTabs().getPanels());
            fullControlPanelInitialized = true;
        }
        super.act(delta);
    }
}
