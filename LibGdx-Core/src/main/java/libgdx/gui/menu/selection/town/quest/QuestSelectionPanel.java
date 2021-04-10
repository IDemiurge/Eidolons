package libgdx.gui.menu.selection.town.quest;

import eidolons.game.module.dungeoncrawl.quest.DungeonQuest;
import libgdx.gui.menu.selection.ItemListPanel;
import libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.menu.selection.SelectableItemDisplayer;
import libgdx.gui.menu.selection.town.PlaceSelectionPanel;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static eidolons.game.module.dungeoncrawl.quest.QuestMaster.isPrecreatedQuests;

/**
 * Created by JustMe on 10/5/2018.
 */
public class QuestSelectionPanel extends PlaceSelectionPanel {
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
        GuiEventManager.bind(GuiEventType.QUEST_COMPLETED, p -> {
            try {
                next();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        });
        //        GuiEventManager.bind(GuiEventType.QUEST_TAKEN, p -> {
        //            ((QuestListPanel) listPanel).setDisabled(true);
        //            ((QuestInfoPanel) infoPanel).setDisabled(true);
        //        });
        //        GuiEventManager.bind(GuiEventType.QUEST_CANCELLED, p -> {
        //            ((QuestListPanel) listPanel).setDisabled(false);
        //            ((QuestInfoPanel) infoPanel).setDisabled(false);
        //        });
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
    protected SelectableItemDisplayer createInfoPanel() {
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
            Collection<DungeonQuest> quests = (Collection<DungeonQuest>) getUserObject();
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

    private List<SelectableItemData> toDataList(Collection<DungeonQuest> quests) {
        List<SelectableItemData> items = new ArrayList<>();
        for (DungeonQuest quest : quests) {
            //parse here?
            SelectableItemData item = new SelectableItemData(quest.getTitle(), quest.getDescription(),
             null, quest.getImage());
            items.add(item);
        }
        return items;
    }


}
