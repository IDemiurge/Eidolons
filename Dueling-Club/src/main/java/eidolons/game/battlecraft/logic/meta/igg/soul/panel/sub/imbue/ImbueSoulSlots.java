package eidolons.game.battlecraft.logic.meta.igg.soul.panel.sub.imbue;

import eidolons.game.battlecraft.logic.meta.igg.soul.eidola.Soul;
import eidolons.game.battlecraft.logic.meta.igg.soul.panel.LordPanel;
import eidolons.libgdx.gui.panels.TablePanelX;

public class ImbueSoulSlots extends TablePanelX {
    ImbuePanel imbuePanel;
    private Soul[] souls= new Soul[4];

    public ImbueSoulSlots(ImbuePanel imbuePanel) {
        this.imbuePanel = imbuePanel;
    }

    public Soul[] getSouls() {
        return souls;
    }
}
