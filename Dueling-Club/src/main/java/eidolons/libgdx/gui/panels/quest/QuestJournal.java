package eidolons.libgdx.gui.panels.quest;

import eidolons.game.module.dungeoncrawl.quest.DungeonQuest;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.menu.selection.SelectableItemDisplayer;
import eidolons.libgdx.gui.menu.selection.SelectionPanel;
import eidolons.libgdx.gui.menu.selection.town.quest.QuestInfoPanel;
import eidolons.libgdx.gui.menu.selection.town.quest.QuestListPanel;
import eidolons.libgdx.stage.Blocking;
import eidolons.libgdx.stage.StageWithClosable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 10/10/2018.
 */
public class QuestJournal extends SelectionPanel implements Blocking{

    protected String getDoneText() {
        return "Close";
    }

    protected String getTitle() {
        return "Current Quests";
    }

    @Override
    public void closed(Object selection) {
        fadeOut();
    }

    @Override
    protected boolean isAutoDoneEnabled() {
        return false;
    }



    @Override
    protected SelectableItemDisplayer createInfoPanel() {
        return new QuestInfoPanel(null);
    }

    protected boolean isReadyToBeInitialized() {
        return false;
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
    }

    @Override
    protected List<SelectableItemData> createListData() {
        List<DungeonQuest> quests = (List<DungeonQuest>) getUserObject();

        List<SelectableItemData> list = quests.stream().map(
         quest -> new SelectableItemData(quest.getTitle(), quest.getDescription(),
         "" ,quest.getImage())).collect(Collectors.toList());

        return list;
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new QuestListPanel();
    }

    @Override
    public boolean isPausing() {
        return true;
    }

    @Override
    public StageWithClosable getStageWithClosable() {
        return (StageWithClosable) getStage();
    }

}
