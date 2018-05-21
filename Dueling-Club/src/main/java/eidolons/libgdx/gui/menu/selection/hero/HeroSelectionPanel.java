package eidolons.libgdx.gui.menu.selection.hero;

import eidolons.libgdx.gui.menu.selection.ItemInfoPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.menu.selection.SelectionPanel;
import main.entity.Entity;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 11/29/2017.
 */
public class HeroSelectionPanel extends SelectionPanel {

    Supplier<List<? extends Entity>> dataSupplier;

    public HeroSelectionPanel(Supplier<List<? extends Entity>> dataSupplier) {
        super();
        this.dataSupplier = dataSupplier;
        init();
    }
    protected String getDoneText() {
        return "Ready";
    }
    protected String getTitle() {
        return "Select a Hero";
    }
    protected boolean isReadyToBeInitialized() {
        return false;
    }

    @Override
    protected ItemInfoPanel createInfoPanel() {
        return new HeroInfoPanel(null);
    }

    @Override
    protected List<SelectableItemData> createListData() {

        return listPanel.toDataList(dataSupplier.get());
    }

    @Override
    protected boolean isBackSupported() {
        return false;
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new HeroListPanel();
    }
}
