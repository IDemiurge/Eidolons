package libgdx.gui.panels.lord.sub;

import libgdx.gui.panels.lord.LordPanel;
import libgdx.gui.panels.TablePanelX;

public class SoulTab extends TablePanelX {

    private static final float WIDTH = 395;
    private static final float HEIGHT = 650;

    public SoulTab() {
        super(WIDTH, HEIGHT);
    }

    @Override
    public LordPanel.LordDataSource getUserObject() {
        return (LordPanel.LordDataSource) super.getUserObject();
    }
}
