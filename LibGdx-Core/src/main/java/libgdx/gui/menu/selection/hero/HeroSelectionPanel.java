package libgdx.gui.menu.selection.hero;

import eidolons.game.battlecraft.logic.meta.universal.SpawnManager;
import libgdx.gui.menu.selection.ItemListPanel;
import libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.menu.selection.SelectableItemDisplayer;
import libgdx.gui.menu.selection.SelectionPanel;
import main.entity.Entity;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 11/29/2017.
 */
public class HeroSelectionPanel extends SelectionPanel {


    protected Supplier<List<? extends Entity>> dataSupplier;

    public HeroSelectionPanel(Supplier<List<? extends Entity>> dataSupplier) {
        super();
        this.dataSupplier = dataSupplier;
        init();
    }

    @Override
    protected boolean isRandom() {
        return false;
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
    public WAIT_OPERATIONS getWaitOperation() {
        return SpawnManager.WAIT_OPERATION;
    }
    @Override
    public void closed(Object selection) {
        super.closed(selection);
    }

    @Override
    protected SelectableItemDisplayer createInfoPanel() {
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
