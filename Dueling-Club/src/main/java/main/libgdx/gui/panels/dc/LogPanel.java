package main.libgdx.gui.panels.dc;

import main.libgdx.gui.panels.generic.PagedListPanel;
import main.swing.generic.components.G_Panel.VISUALS;

/**
 * Created by JustMe on 1/5/2017.
 */
public class LogPanel extends PagedListPanel {
    public LogPanel(  int col, int row) {

        super(VISUALS.INFO_PANEL.getImgPath(), col, row);
    }
}
