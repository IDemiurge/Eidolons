package main.libgdx.gui.panels.dc.actionpanel;

import main.libgdx.gui.panels.dc.actionpanel.datasource.UnitActionsDataSource;

import java.util.List;

public class ModeActionsPanel extends BaseSlotPanel {

    public ModeActionsPanel() {
        super(0);
    }

    public ModeActionsPanel(int imageSize) {
        super(imageSize);
    }

    @Override
    public void updateAct(float delta) {
        clear();

        final UnitActionsDataSource source = (UnitActionsDataSource) getUserObject();

        final List<ActionValueContainer> sources = source.getDisplayedActions();
        initContainer(sources, "UI/EMPTY_LIST_ITEM.jpg");
    }


}
