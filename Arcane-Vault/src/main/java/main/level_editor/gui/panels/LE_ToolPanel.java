package main.level_editor.gui.panels;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.kotcrab.vis.ui.layout.DragPane;
import eidolons.libgdx.gui.NinePatchFactory;

public class LE_ToolPanel extends DragPane {
/*
would be best if this draggable could also be tabbed between ctrl groups
 */
    public LE_ToolPanel(WidgetGroup panelX) {
        super(panelX);
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());

    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
