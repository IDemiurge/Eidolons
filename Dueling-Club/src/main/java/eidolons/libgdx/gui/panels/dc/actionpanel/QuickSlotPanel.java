package eidolons.libgdx.gui.panels.dc.actionpanel;

import eidolons.libgdx.gui.panels.dc.TablePanel;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.ActiveQuickSlotsDataSource;

import java.util.List;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class QuickSlotPanel extends BaseSlotPanel {

    public QuickSlotPanel(int imageSize) {
        super(imageSize);
    }


    @Override
    public void updateAct(float delta) {
        clear();
        final ActiveQuickSlotsDataSource source = (ActiveQuickSlotsDataSource) getUserObject();
        final List<ActionValueContainer> sources = source.getQuickSlotActions();
        initContainer(sources, "UI/EMPTY_LIST_ITEM.jpg");
    }

    @Override
    protected TablePanel initPage(List<ActionValueContainer> sources, String emptyImagePath) {
        TablePanel page = new TablePanel();
        for (int i = 0; i < getPageSize(); i++) {
            ActionValueContainer valueContainer = null;
            if (sources.size() > i) {
                valueContainer = sources.get(i);
                if (page == null)
                    emptyImagePath = ("UI/empty_pack.jpg");
            }
            addValueContainer(page, valueContainer, getOrCreateR(emptyImagePath));
        }

        return page;
    }
}
