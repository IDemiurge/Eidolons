package main.gui.gateway.node;

import main.ArcaneTower;
import main.logic.ArcaneEntity;
import main.swing.components.TextComp;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.swing.generic.services.listener.MouseClickListener;

import java.awt.*;
import java.awt.event.MouseEvent;

public class GatewayNode<E extends ArcaneEntity> extends G_Panel {
    E entity;
    int zoom;
    TextComp header;
    G_Panel paramPanel;
    G_Panel propPanel;
    GraphicComponent iconLabel;

    public GatewayNode() {
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
    public void paint(Graphics g) {
        // TODO Auto-generated method stub
        super.paint(g);
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
//		if (e.getSource() == exp_collapse) {
//			toggleExpanded();
//		} else if (e.getSource() == exp_collapse) {
//			toggleExpanded();
//		} else if (e.getSource() == exp_collapse) {
//			toggleExpanded();
//		} else if (e.getClickCount() > 1) {
//			// fast-edit
//			return;
//		}
//		GatewayView.selected(entity);

    }

}
