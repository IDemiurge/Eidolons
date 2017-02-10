package main.gui.gateway.tree;

import main.ArcaneTower;
import main.logic.ArcaneEntity;
import main.swing.components.TextComp;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.swing.generic.services.listener.MouseClickListener;

import java.awt.event.MouseEvent;

public class GatewayTreeNode extends G_Panel {
    ArcaneEntity entity;
    int zoom;
    TextComp header;
    G_Panel paramPanel;
    G_Panel propPanel;
    GraphicComponent iconLabel;

    public GatewayTreeNode() {
        MouseClickListener listener = new MouseClickListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e);
            }
        };
        header = new TextComp(entity.getName(), ArcaneTower.getTextColor());
        paramPanel = new G_Panel("flowy");
        propPanel = new G_Panel();
//		iconLabel = new PortraitComp(entity);

        header.addMouseListener(listener);
    }

    @Override
    public void refresh() {
        paramPanel.removeAll();

        super.refresh();
    }

    protected void zoom(int zoom) {
        // header.setFont(font);
    }

    protected void handleClick(MouseEvent e) {
        if (e.getClickCount() > 1) {
            // fast-edit
            return;
        }
//		GatewayView.selected(entity);

    }
}
