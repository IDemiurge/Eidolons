package main.test.debug;

import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagePanel;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.GuiManager;
import main.test.debug.DebugMaster.DEBUG_FUNCTIONS;

import java.util.List;

public class PagedDebugPanel extends G_PagePanel<DEBUG_FUNCTIONS> {
    private static final int PAGE_SIZE = 12;

    public PagedDebugPanel() {
        super(PAGE_SIZE, false, 5);
    }

    @Override
    protected void addControls() {
        forwardButton = getButton(true);
        backButton = getButton(false);
        int x = (getPanelWidth() - arrowWidth * 2) / 2; // center!
        int y = VISUALS.MENU_BUTTON.getHeight();
        addControl(backButton, true, x, y);
        x += arrowWidth;
        addControl(forwardButton, false, x, y);

        setComponentZOrder(backButton, 0);
        setComponentZOrder(forwardButton, 1);
        if (getCurrentComponent() != null) {
            setComponentZOrder(getCurrentComponent(), 2);
        }
    }

    @Override
    protected boolean isComponentAfterControls() {
        return false;
    }

    @Override
    public int getPanelHeight() {
        return GuiManager.getCellWidth();
    }

    @Override
    public int getPanelWidth() {
        return 732;
    }

    @Override
    protected G_Component createPageComponent(List<DEBUG_FUNCTIONS> list) {
        return new DebugGuiPage(list);
    }

    @Override
    protected List<List<DEBUG_FUNCTIONS>> getPageData() {
        return splitList(new EnumMaster<DEBUG_FUNCTIONS>().getEnumList(DEBUG_FUNCTIONS.class));
    }

}
