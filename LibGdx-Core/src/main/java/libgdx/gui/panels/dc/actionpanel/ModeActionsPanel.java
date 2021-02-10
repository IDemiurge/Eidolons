package libgdx.gui.panels.dc.actionpanel;

import libgdx.gui.generic.ValueContainer;
import libgdx.gui.panels.dc.actionpanel.datasource.UnitActionsDataSource;

import java.util.List;

public class ModeActionsPanel extends BaseSlotPanel {

    public ModeActionsPanel() {
        super(0);
    }

    public ModeActionsPanel(int imageSize) {
        super(imageSize);
    }

    protected int getPageSize() {
        return 5;
    }
    @Override
    public void updateAct(float delta) {

        final UnitActionsDataSource source = (UnitActionsDataSource) getUserObject();
        if (source == null) {
            return;
        }
        clear();
        final List<ValueContainer> sources = source.getDisplayedActions();
        initContainer(sources, "ui/empty_list_item.jpg");
    }


}
