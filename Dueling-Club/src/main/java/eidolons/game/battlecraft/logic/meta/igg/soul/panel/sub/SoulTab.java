package eidolons.game.battlecraft.logic.meta.igg.soul.panel.sub;

import eidolons.game.battlecraft.logic.meta.igg.soul.panel.LordPanel;
import eidolons.libgdx.gui.panels.TablePanelX;

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
