package eidolons.libgdx.gui.menu.selection.saves;

import eidolons.libgdx.gui.menu.selection.ItemInfoPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.menu.selection.hero.HeroSelectionPanel;
import eidolons.macro.MacroInitializer;
import main.entity.Entity;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 6/27/2018.
 */
public class SaveSelectionPanel extends HeroSelectionPanel {
    public SaveSelectionPanel(Supplier<List<? extends Entity>> dataSupplier) {
        super(dataSupplier);
    }

    @Override
    public void done() {
        super.done();
        MacroInitializer.load(listPanel.getCurrentItem().getName());

    }

    protected String getDoneText() {
        return "Load";
    }
    protected String getTitle() {
        return "Select a Save";
    }
    @Override
    protected ItemInfoPanel createInfoPanel() {
        return new SaveInfoPanel(null);
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
        return new SaveListPanel();
    }
}
