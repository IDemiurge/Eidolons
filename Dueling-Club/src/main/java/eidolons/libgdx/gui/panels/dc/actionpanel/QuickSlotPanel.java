package eidolons.libgdx.gui.panels.dc.actionpanel;

import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.ActiveQuickSlotsDataSource;
import eidolons.libgdx.texture.Images;

import java.util.List;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class QuickSlotPanel extends BaseSlotPanel {

    public QuickSlotPanel(int imageSize) {
        super(imageSize);
    }

    protected int getPageSize() {
        return 5;
    }

    @Override
    public void updateAct(float delta) {
        final ActiveQuickSlotsDataSource source = (ActiveQuickSlotsDataSource) getUserObject();
        if (source == null) {
            return;
        }
        clear();
        final List<ValueContainer> sources = source.getQuickSlotActions();
        initContainer(sources, Images.EMPTY_QUICK_ITEM);
    }

    @Override
    protected TablePanel initPage(List<ValueContainer> sources, String emptyImagePath) {
        TablePanel page = new TablePanel();
        for (int i = 0; i < getPageSize(); i++) {
            ValueContainer valueContainer = null;
            if (sources.size() > i) {
                valueContainer = sources.get(i);
//                if (page == null)
//                    emptyImagePath = ("ui/empty_pack.jpg");
            }
            addValueContainer(page, valueContainer, getOrCreateR(emptyImagePath));
        }

        return page;
    }
}
