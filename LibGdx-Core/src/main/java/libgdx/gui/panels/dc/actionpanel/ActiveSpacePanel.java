package libgdx.gui.panels.dc.actionpanel;

import libgdx.gui.generic.ValueContainer;
import libgdx.gui.panels.dc.actionpanel.datasource.ActiveSpaceDataSource;
import eidolons.content.consts.Images;

import java.util.List;

public class ActiveSpacePanel extends BaseSlotPanel {
    public ActiveSpacePanel() {
        super(0);
    }

    protected int getPageSize() {
        return 6;
    }

    public ActiveSpacePanel(int imageSize) {
        super(imageSize);
    }

    @Override
    public void updateAct(float delta) {
        clear();

        final ActiveSpaceDataSource source = (ActiveSpaceDataSource) getUserObject();
        if (source == null) {
            return;
        }
        final List<ValueContainer> sources = source.getActives();
        initContainer(sources, Images.EMPTY_SPELL);
    }
}
