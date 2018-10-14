package eidolons.libgdx.gui.menu.selection.quest;

import eidolons.libgdx.gui.menu.selection.ItemInfoPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.menu.selection.SelectionPanel;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 10/5/2018.
 */
public class QuestSelectionPanel extends SelectionPanel {
    public static final WAIT_OPERATIONS WAIT_OPERATION = WAIT_OPERATIONS.CUSTOM_SELECT;
    Supplier<List<? extends Entity>> dataSupplier;

    public QuestSelectionPanel() {
        super();
    }

    public QuestSelectionPanel(Supplier<List<? extends Entity>> dataSupplier) {
        super();
        this.dataSupplier = dataSupplier;
        init();
        GuiEventManager.bind(GuiEventType.QUEST_TAKEN , p-> {
            ((QuestListPanel) listPanel).setDisabled(true);
            ((QuestInfoPanel) infoPanel).setDisabled(true);
        });
        GuiEventManager.bind(GuiEventType.QUEST_CANCELLED , p-> {
            ((QuestListPanel) listPanel).setDisabled(false);
            ((QuestInfoPanel) infoPanel).setDisabled(false);
        });
    }

    @Override
    public void cancel(boolean manual) {
        if (manual)
        {
            WaitMaster.receiveInput(TownPanel.DONE_OPERATION, false);
            GuiEventManager.trigger(GuiEventType.SHOW_TOWN_PANEL, null );
        }
        else
            super.cancel(false);
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
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        init();
        if (TownPanel.TEST_MODE) {
            debugAll();
        }
    }
    protected boolean isDoneSupported() {
        return dataSupplier!=null;
    }
    @Override
    protected List<SelectableItemData> createListData() {
        List<? extends Entity> list = null;
        if (dataSupplier != null) {
            list = dataSupplier.get();
        } else
        if (getUserObject() instanceof
         List) {
            list = (List<? extends Entity>) getUserObject();
        }
        return listPanel.toDataList(list);
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
    }

    @Override
    public float getWidth() {
        return super.getWidth();
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
    }
    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
    }
    @Override
    protected ItemListPanel createListPanel() {
        return new QuestListPanel();
    }
}
