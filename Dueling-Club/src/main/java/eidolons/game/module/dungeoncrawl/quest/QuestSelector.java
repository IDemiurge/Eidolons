package eidolons.game.module.dungeoncrawl.quest;

import eidolons.game.module.dungeoncrawl.quest.advanced.Quest;
import eidolons.libgdx.gui.menu.selection.town.quest.QuestSelectionPanel;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by JustMe on 10/5/2018.
 * <p>
 * Quest should be chosen before RNG/Scenario is launched, so we can adjust spawning/...
 */
public class QuestSelector extends QuestHandler {


    public QuestSelector(QuestMaster questMaster) {
        super(questMaster);
    }

    public void displayQuests() {
        //for macro
    }

    public void selectQuests() {

        Collection<ObjType> filtered =master.getQuestTypePool();

        GuiEventManager.trigger(GuiEventType.SHOW_QUEST_SELECTION, filtered);
    }

    public List<Quest> chooseQuests(int n) {
        List<Quest> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            selectQuests();

            String result =
             (CoreEngine.isFastMode()) ? DataManager.getRandomType(MACRO_OBJ_TYPES.QUEST).getName()
              : (String) WaitMaster.waitForInput(QuestSelectionPanel.WAIT_OPERATION);
            if (result == null) {
                return list;
            }
            ObjType type = DataManager.getType(result.toString(), MACRO_OBJ_TYPES.QUEST);
            Quest quest = master.getCreator().create(type);
            list.add(quest);
        }
        if (CoreEngine.isFastMode() )
            GuiEventManager.trigger(GuiEventType.SHOW_SELECTION_PANEL, null);
        return list;
    }
}
