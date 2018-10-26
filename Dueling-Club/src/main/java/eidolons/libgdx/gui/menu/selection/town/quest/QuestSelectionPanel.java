package eidolons.libgdx.gui.menu.selection.town.quest;

import eidolons.game.module.dungeoncrawl.quest.DungeonQuest;
import eidolons.libgdx.gui.menu.selection.ItemInfoPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.menu.selection.town.PlaceSelectionPanel;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 10/5/2018.
 */
public class QuestSelectionPanel extends PlaceSelectionPanel {
    public static final WAIT_OPERATIONS WAIT_OPERATION = WAIT_OPERATIONS.CUSTOM_SELECT;
    Supplier<List<? extends Entity>> dataSupplier;

    public QuestSelectionPanel() {
        super();
        bindEvents();
    }

    public QuestSelectionPanel(Supplier<List<? extends Entity>> dataSupplier) {
        super();
        this.dataSupplier = dataSupplier;
        init();
        bindEvents();
    }

    protected void bindEvents() {
        GuiEventManager.bind(GuiEventType.QUEST_TAKEN, p -> {
            ((QuestListPanel) listPanel).setDisabled(true);
            ((QuestInfoPanel) infoPanel).setDisabled(true);
        });
        GuiEventManager.bind(GuiEventType.QUEST_CANCELLED, p -> {
            ((QuestListPanel) listPanel).setDisabled(false);
            ((QuestInfoPanel) infoPanel).setDisabled(false);
        });
    }

    protected String getDoneText() {
        return "Done";
    }

    protected String getTitle() {
        return "Available Quests";
    }

    protected ItemListPanel createListPanel() {
        return new QuestListPanel();
    }

    @Override
    protected ItemInfoPanel createInfoPanel() {
        return new QuestInfoPanel(null);
    }

    protected boolean isReadyToBeInitialized() {
        return false;
    }


    protected boolean isDoneSupported() {
        return dataSupplier != null;
    }

    @Override
    protected List<SelectableItemData> createListData() {
        if (isPrecreatedQuests()) {
            List<DungeonQuest> quests = (List<DungeonQuest>) getUserObject();
            return toDataList(quests);
        }
        Collection<? extends Entity> list = null;
        if (dataSupplier != null) {
            list = dataSupplier.get();
        } else if (getUserObject() instanceof
         Collection) {
            list = (Collection<? extends Entity>) getUserObject();
        }
        return listPanel.toDataList(list);
    }

    private List<SelectableItemData> toDataList(List<DungeonQuest> quests) {
        List<SelectableItemData> items = new ArrayList<>();
//        for (DungeonQuest quest : quests) {
//            //parse here?
//            item = new SelectableItemData(name, descr, big, img);
//            items.add(item);
//        }
        return items;
    }

    private boolean isPrecreatedQuests() {
        return false;
    }

}
