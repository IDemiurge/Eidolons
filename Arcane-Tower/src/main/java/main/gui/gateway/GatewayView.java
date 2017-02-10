package main.gui.gateway;

import main.logic.AT_OBJ_TYPE;
import main.logic.ArcaneEntity;
import main.logic.Direction;
import main.swing.generic.components.G_Panel;

public class GatewayView extends G_Panel {

    //	NavigablePanel panel;
    private static GatewayView instance;
    EditPanel editPanel;
    EntityControlPanel controlPanel;
    TopGatewayContainer<? extends ArcaneEntity> mainContainer;
    private boolean direction;

    public GatewayView() {
        this(AT_OBJ_TYPE.DIRECTION);
    }

    public GatewayView(AT_OBJ_TYPE topLevelType) {
        this(3, 2, topLevelType);
    }

    public GatewayView(int levelsOfDepth, int defaultDetailLevel, AT_OBJ_TYPE topLevelType) {
        // if (direction)
        mainContainer = new TopGatewayContainer<Direction>(AT_OBJ_TYPE.DIRECTION, levelsOfDepth,
                defaultDetailLevel);
        mainContainer.refresh();
        add(mainContainer, "id main, pos @center_x 0");
        editPanel = new EditPanel();
        // add(editPanel.getPanel(), "pos main.x2 0");
        add(editPanel.getPanel(), "pos @max_x 0");
        // add(controlPanel, "pos main.y2 0");
        // new gatewaycont

        instance = this;
    }

    public static GatewayView getInstance() {
        return instance;
    }

    @Override
    public void refresh() {
        mainContainer.refresh();
    }

    public EditPanel getEditPanel() {
        return editPanel;
    }

    public EntityControlPanel getControlPanel() {
        return controlPanel;
    }

    public TopGatewayContainer<? extends ArcaneEntity> getMainContainer() {
        return mainContainer;
    }

    public boolean isDirection() {
        return direction;
    }

//	public NavigablePanel getPanel() {
//		return panel;
//	}

    public enum GATEWAY_VIEW_TEMPLATE {
        DIRECTIONS_FULL,
    }

}
