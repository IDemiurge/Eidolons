package eidolons.libgdx.gui.menu.selection.quest;

import eidolons.libgdx.gui.menu.selection.ItemInfoPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.menu.selection.SelectionPanel;
import main.entity.Entity;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 10/5/2018.
 */
public class QuestSelectionPanel extends SelectionPanel {
    public static final WAIT_OPERATIONS WAIT_OPERATION = WAIT_OPERATIONS.CUSTOM_SELECT;
    Supplier<List<? extends Entity>> dataSupplier;

    public QuestSelectionPanel(Supplier<List<? extends Entity>> dataSupplier) {
        super();
        this.dataSupplier = dataSupplier;
        init();
    }
    protected String getDoneText() {
        return "Done";
    }

    protected String getTitle() {
        return "Select a Quest";
    }

    @Override
    public void closed(Object selection) {
        super.closed(selection);
    }

    @Override
    public WAIT_OPERATIONS getWaitOperation() {
        return WAIT_OPERATION;
    }

    @Override
    protected ItemInfoPanel createInfoPanel() {
        return new QuestInfoPanel(null);
    }

    protected boolean isReadyToBeInitialized() {
        return false;
    }

    @Override
    protected List<SelectableItemData> createListData() {

        return listPanel.toDataList(dataSupplier.get());
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new QuestListPanel();
    }
}
