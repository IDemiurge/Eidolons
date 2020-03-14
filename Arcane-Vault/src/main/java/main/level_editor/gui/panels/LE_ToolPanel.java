package main.level_editor.gui.panels;

import com.kotcrab.vis.ui.layout.DragPane;
import eidolons.libgdx.gui.panels.TablePanelX;

public class LE_ToolPanel extends DragPane {
/*
would be best if this draggable could also be tabbed between ctrl groups
 */
    public LE_ToolPanel(TablePanelX     panelX) {
        super(panelX);
    }
}
