package eidolons.libgdx.gui.panels.headquarters.creation;

import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.menu.selection.SelectableItemDisplayer;
import eidolons.libgdx.gui.menu.selection.SelectionPanel;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationSequence.HERO_CREATION_ITEM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 6/5/2018.
 */
public class HeroCreationPanel extends SelectionPanel {
    public HeroCreationPanel(Unit unit) {
        super();
        setUserObject(unit);
        init();
    }

    @Override
    protected boolean isReadyToBeInitialized() {
        return false;
    }

    @Override
    protected SelectableItemDisplayer createInfoPanel() {
        return new HeroCreationWorkspace();
    }

    @Override
    protected boolean isDoneSupported() {
        return false;
    }

    @Override
    protected List<SelectableItemData> createListData() {
        List<SelectableItemData> list = new ArrayList<>();
        for (HERO_CREATION_ITEM sub : HERO_CREATION_ITEM.values()) {
            SelectableItemData item = new SelectableItemData(sub.name(), getUserObject());
            item.setSubItems(sub.getSubItems());
            list.add(item);
        }

        return list;
    }

    protected boolean isAutoDoneEnabled() {
        return false;
    }
    @Override
    public Unit getUserObject() {
        return (Unit) super.getUserObject();
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new HeroCreationSequence();
    }
}
