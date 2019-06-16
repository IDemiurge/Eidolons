package eidolons.libgdx.gui.panels.dc.actionpanel;

import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.UnitActionsDataSource;

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

        final List<ValueContainer> sources = source.getDisplayedActions();
        initContainer(sources, "ui/empty_list_item.jpg");
    }


}
