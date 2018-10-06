package eidolons.game.module.dungeoncrawl.quest;

import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.xml.XML_Reader;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 10/6/2018.
 */
public class QuestMaster {

    private boolean started;
    QuestResolver resolver;
    QuestCreator creator;
    QuestSelector selector;

    List<DungeonQuest> quests=    new ArrayList<>() ;

    public QuestMaster() {
        resolver= new QuestResolver(this);
        creator= new QuestCreator(this);
        selector= new QuestSelector(this);

        if (!CoreEngine.isMacro()) {
            XML_Reader.readTypeFile(true, MACRO_OBJ_TYPES.QUEST);
        }
        GuiEventManager.bind(GuiEventType.QUESTS_UPDATE_REQUIRED,
         p -> {
            if (started){
                startedQuests();
                started=true;
            } else updateQuests();
         });
    }

    public QuestResolver getResolver() {
        return resolver;
    }

    public QuestCreator getCreator() {
        return creator;
    }

    public QuestSelector getSelector() {
        return selector;
    }

    public void updateQuests(){
        quests.forEach(quest ->
         GuiEventManager.trigger(GuiEventType.QUEST_UPDATE, quest));
    }
    public void startedQuests(){
        quests.forEach(quest ->
         GuiEventManager.trigger(GuiEventType.QUEST_STARTED, quest));
    }
    public boolean initQuests() {
        quests= getSelector().chooseQuests(2);
        CoreEngine.setFastMode(true);
        return !quests.isEmpty();
    }

    public List<DungeonQuest> getQuests() {
        return quests;
    }
}
