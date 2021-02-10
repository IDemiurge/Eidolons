package libgdx.gui.panels.dc.actionpanel;

import libgdx.gui.generic.ValueContainer;
import libgdx.gui.panels.TablePanel;
import libgdx.gui.panels.dc.actionpanel.datasource.ActiveQuickSlotsDataSource;
import libgdx.texture.Images;
import libgdx.texture.TextureCache;

import java.util.List;

import static libgdx.texture.TextureCache.getOrCreateR;

public class QuickSlotPanel extends BaseSlotPanel {

    public QuickSlotPanel(int imageSize) {
        super(imageSize);
    }

    protected int getPageSize() {
        return 6;
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
            addValueContainer(page, valueContainer, TextureCache.getOrCreateR(emptyImagePath));
        }

        return page;
    }
}
