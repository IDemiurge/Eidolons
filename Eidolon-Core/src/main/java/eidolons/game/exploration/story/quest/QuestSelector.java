package eidolons.game.exploration.story.quest;

import eidolons.game.exploration.story.quest.advanced.Quest;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.Flags;
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

    public static final WaitMaster.WAIT_OPERATIONS WAIT_OPERATION = WaitMaster.WAIT_OPERATIONS.CUSTOM_SELECT;

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
             (Flags.isFastMode()) ? DataManager.getRandomType(DC_TYPE.QUEST).getName()
              : (String) WaitMaster.waitForInput( WAIT_OPERATION);
            if (result == null) {
                return list;
            }
            ObjType type = DataManager.getType(result, DC_TYPE.QUEST);
            Quest quest = master.getCreator().create(type);
            list.add(quest);
        }
        if (Flags.isFastMode() )
            GuiEventManager.trigger(GuiEventType.SHOW_SELECTION_PANEL, null);
        return list;
    }
}
