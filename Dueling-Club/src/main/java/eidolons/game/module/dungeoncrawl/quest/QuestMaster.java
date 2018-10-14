package eidolons.game.module.dungeoncrawl.quest;

import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.enums.meta.QuestEnums;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.data.xml.XML_Reader;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 10/6/2018.
 */
public class QuestMaster {

    public static final boolean TEST_MODE = true;
    public static final boolean ON = true;

    static {
        if (TEST_MODE)
            CoreEngine.setFastMode(true);
    }

    QuestResolver resolver;
    QuestCreator creator;
    QuestSelector selector;

    List<DungeonQuest> quests = new ArrayList<>();
    private boolean started;

    public QuestMaster() {
        resolver = new QuestResolver(this);
        creator = new QuestCreator(this);
        selector = new QuestSelector(this);

        if (!CoreEngine.isMacro()) {
            XML_Reader.readTypeFile(true, MACRO_OBJ_TYPES.QUEST);
        }
        GuiEventManager.bind(GuiEventType.QUEST_TAKEN,
         p -> {
             questTaken(p.get().toString());
         });
        GuiEventManager.bind(GuiEventType.QUEST_CANCELLED,
         p -> {
             if (p.get() == null) {
                 questsCancelled();
             } else
             questCancelled(p.get().toString());
         });
        GuiEventManager.bind(GuiEventType.QUESTS_UPDATE_REQUIRED,
         p -> {
             if (!started) {
                 startedQuests();
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

    public void questsCancelled() {
        quests.clear();
    }

    public void questCancelled(String name) {
        for (DungeonQuest quest : new ArrayList<>(quests)) {
            if (quest.getTitle().equalsIgnoreCase(name)) {
                quests.remove(quest);
            }
        }
    }

    public void questTaken(String name) {
        ObjType type = DataManager.getType(name, MACRO_OBJ_TYPES.QUEST);
        DungeonQuest quest = getCreator().create(type);
        quests.add(quest);
    }

    public void updateQuests() {
        quests.forEach(quest ->
         GuiEventManager.trigger(GuiEventType.QUEST_UPDATE, quest));
    }

    public void startQuests() {
        quests.removeIf(quest -> quest.isComplete());
        quests.forEach(quest -> getResolver().questTaken(quest));
        if (TEST_MODE)
            CoreEngine.setFastMode(true);
        startedQuests();
    }

    public void startedQuests() {
        quests.forEach(quest ->
         GuiEventManager.trigger(GuiEventType.QUEST_STARTED, quest));
        started = true;
    }

    public boolean initQuests() {
        started = false;
        quests = getSelector().chooseQuests(2);
        return !quests.isEmpty();
    }

    public List<DungeonQuest> getQuests() {
        return quests;
    }

    public List<ObjType> getQuestTypePool() {
        String filter = QuestEnums.QUEST_GROUP.SCENARIO + StringMaster.OR + QuestEnums.QUEST_GROUP.RNG;

        return DataManager.getFilteredTypes(MACRO_OBJ_TYPES.QUEST,
         filter, MACRO_PROPS.QUEST_GROUP);

    }
}
