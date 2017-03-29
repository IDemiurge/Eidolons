package main.libgdx.gui.panels.dc.actionpanel;

import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.actionpanel.datasource.ActionModDataSource;

import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class ActionModPanel extends TablePanel {

    public ActionModPanel() {
        left().bottom();
    }

    @Override
    public void updateAct(float delta) {
        clear();

        final ActionModDataSource source = (ActionModDataSource) getUserObject();

        final List<ActionValueContainer> sources = source.getActionMods();
        final int tempLimit = Math.min(sources.size(), 6);
        for (int i = 0; i < tempLimit; i++) {
            final ActionValueContainer valueContainer = sources.get(i);
            if (valueContainer != null) {
                add(valueContainer).left().bottom();
            } else {
                add(new ValueContainer(getOrCreateR("UI/EMPTY_LIST_ITEM.jpg"))).left().bottom();
            }
        }

        for (int i = tempLimit; i < 6; i++) {
            add(new ValueContainer(getOrCreateR("UI/EMPTY_LIST_ITEM.jpg"))).left().bottom();
        }
    }
}
