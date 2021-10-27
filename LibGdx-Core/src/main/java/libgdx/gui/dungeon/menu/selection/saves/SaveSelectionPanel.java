package libgdx.gui.dungeon.menu.selection.saves;

import libgdx.gui.dungeon.menu.selection.ItemListPanel;
import libgdx.gui.dungeon.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.dungeon.menu.selection.SelectableItemDisplayer;
import libgdx.gui.dungeon.menu.selection.hero.HeroSelectionPanel;
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
        if (listPanel.getCurrentItem() == null || listPanel.isBlocked(listPanel.getCurrentItem())) {
            return;
        }
        super.done();
        // AdventureInitializer.load(listPanel.getCurrentItem().getName());

    }

    protected String getDoneText() {
        return "Load";
    }
    protected String getTitle() {
        return "Select a Save";
    }
    @Override
    protected SelectableItemDisplayer createInfoPanel() {
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
