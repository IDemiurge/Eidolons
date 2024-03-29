package eidolons.game.exploration.story.quest;

import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Core;
import eidolons.game.exploration.story.quest.advanced.Quest;
import eidolons.game.exploration.handlers.ExplorationMaster;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicEnums;
import eidolons.system.audio.MusicMaster;
import eidolons.system.text.tips.TipMessageMaster;
import eidolons.system.text.tips.TipMessageSource;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums;
import main.content.enums.meta.QuestEnums;
import main.content.enums.meta.QuestEnums.QUEST_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Strings;
import main.system.sound.AudioEnums;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static main.content.enums.DungeonEnums.DUNGEON_STYLE.*;

/**
 * Created by JustMe on 10/6/2018.
 */
public class QuestMaster extends MetaGameHandler {

    public static final boolean TEST_MODE = false;//CoreEngine.isLiteLaunch();// CoreEngine.isFastMode() && !CoreEngine.isFullFastMode();
    public static final boolean ON = true;

    //    static {
    //        if (TEST_MODE)
    //            CoreEngine.setFastMode(true);
    //    }
    protected QuestResolver resolver;
    protected QuestCreator creator;
    protected QuestSelector selector;
    protected List<Quest> quests = new ArrayList<>();
    protected boolean started;
    protected Set<Quest> questsPool;

    public QuestMaster(MetaGameMaster master) {
        super(master);
        resolver = new QuestResolver(this);
        creator = new QuestCreator(this);
        selector = new QuestSelector(this);

//        if (!CoreEngine.isMacro()) {
//            XML_Reader.readTypeFile(true, MACRO_OBJ_TYPES.QUEST);
//        }
        GuiEventManager.bind(GuiEventType.QUEST_TAKEN,
                p -> {
                    if (p.get() instanceof Quest) {
                        questTaken(((Quest) p.get()), null);
                    } else {
                        questTaken(p.get().toString());
                    }
                    DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__QUEST_TAKEN);
                });
        GuiEventManager.bind(GuiEventType.QUEST_CANCELLED,
                p -> {
                    if (p.get() == null) {
                        questsCancelled();
                    } else
                        questCancelled(p.get().toString());

                    DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__QUEST_CANCELLED);
                });
        GuiEventManager.bind(GuiEventType.QUESTS_UPDATE_REQUIRED,
                p -> {
                    if (!started) {
                        startedQuests();
                    } else updateQuests();
                });
    }

    public static boolean isPrecreatedQuests() {
        return true;
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
        quests.forEach(q -> questCancelled(q.getTitle()));
    }

    public void questCancelled(String name) {
        for (Quest quest : new ArrayList<>(quests)) {
            if (quest.getTitle().equalsIgnoreCase(name)) {
                quest.setStarted(false);
                quests.remove(quest);
            }
        }
    }

    public void questTaken(String name) {
        questTaken(name, false);
    }

    public void questTaken(String name, boolean notify) {
        ObjType type = DataManager.getType(name, DC_TYPE.QUEST);
        Quest quest = null;
        if (isPrecreatedQuests())
            for (Quest q : getQuestsPool()) {
                if (q.getTitle().equals(type.getName())) {
                    quest = q;
                    break;
                }
            }
        if (quest == null) {
            quest = getCreator().create(type);
        }
        questTaken(quest, type);
    }

    public void questTaken(Quest quest, ObjType type) {
        quest.setStarted(true);
        quests.add(quest);
        startQuests();
        if (type != null) {
            String txt = type.getName() + Strings.NEW_LINE +
                    Strings.NEW_LINE + quest.getProgressText() + Strings.NEW_LINE +
                    quest.getDescription();
            if (ExplorationMaster.isExplorationOn())
                MusicMaster.playMoment(MusicEnums.MUSIC_MOMENT.TOWN);

            TipMessageMaster.tip(new TipMessageSource(txt, type.getImagePath(), "Onward!", false, () -> {
            }));

        }
    }

    public void updateQuests() {
        quests.forEach(quest ->
                GuiEventManager.trigger(GuiEventType.QUEST_UPDATE, quest));
    }

    public void startQuests() {
        quests.removeIf(Quest::isComplete);
        quests.forEach(quest -> {
            if (quest instanceof DungeonQuest) {
                getResolver().questTaken((DungeonQuest) quest);
            }
        });
        startedQuests();
    }

    public void startedQuests() {
        quests.forEach(quest ->
                GuiEventManager.trigger(GuiEventType.QUEST_STARTED, quest));
        GuiEventManager.trigger(GuiEventType.QUESTS_UPDATE_REQUIRED);
        started = true;
    }

    public boolean initQuests() {
        started = false;
        quests = getSelector().chooseQuests(2);
        return !quests.isEmpty();
    }

    public List<Quest> getRunningQuests() {
        return quests;
    }

    public Set<ObjType> getQuestTypePool() {
        String filter = getQuestGroupFilter();
        Set<ObjType> pool = new LinkedHashSet<>(DataManager.getFilteredTypes(DC_TYPE.QUEST,
                filter, G_PROPS.QUEST_GROUP));

        pool.removeIf(q -> !checkQuestForLocation(q));

        return pool;
    }

    protected String getQuestGroupFilter() {
        return QuestEnums.QUEST_GROUP.SCENARIO + Strings.OR + QuestEnums.QUEST_GROUP.RNG;
    }

    protected boolean checkQuestForLocation(ObjType q) {
//        if (master.getMetaDataManager().getMetaGame() instanceof ScenarioMeta) {
//            ((ScenarioMeta) master.getMetaDataManager().getMetaGame()).getScenario()
//        }
        QUEST_TYPE type = new EnumMaster<QUEST_TYPE>().retrieveEnumConst(
                QUEST_TYPE.class, q.getProperty(G_PROPS.QUEST_TYPE));
        switch (type) {

            case BOSS:
            case HUNT:
                break;
            case OBJECTS:
                if (master.getGame().getDungeonMaster().getFloorWrapper()  == null) {
                    return false;
                }
                switch (master.getGame().getDungeonMaster().getFloorWrapper().getStyle()) {
                    case Somber:
                    case Grimy:
                    case Pagan:
                    case Arcane:
                        return true;
                }
                return false;
            case COMMON_ITEMS:
            case SPECIAL_ITEM:
                if (master.getGame().getDungeonMaster().getFloorWrapper()  == null) {
                    return false;
                }
                switch (master.getGame().getDungeonMaster().getFloorWrapper().getStyle()) {
                    case Somber:
                        return false;
                }
                break;
        }
        return true;
    }

    public Set<Quest> getQuestsPool() {
        if (questsPool != null) {
            return questsPool;
        }
        questsPool = new LinkedHashSet<>();
        Set<ObjType> pool = getQuestTypePool();
        for (ObjType type : pool) {
            Quest quest = getCreator().create(type);
            questsPool.add(quest);
        }
        return questsPool;
    }

    public void questComplete(String questN) {
        questComplete(getQuest(questN));
    }
        public void questComplete(Quest quest) {
        questCancelled(quest.getTitle()); // just in case?..
        GuiEventManager.trigger(GuiEventType.QUEST_CANCELLED, quest.getTitle());
        GuiEventManager.trigger(GuiEventType.QUEST_COMPLETED, quest.getTitle());
        GuiEventManager.trigger(GuiEventType.QUESTS_UPDATE_REQUIRED);
        questsPool.remove(quest); //can't retake it
        updateQuests();
        quest.getReward().award(Core.getMainHero(), true);
        quest.setRewardTaken(true);
        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__QUEST_COMPLETED);
    }

    public Quest getQuest(String string) {
        for (Quest quest : getRunningQuests()) {
            if (quest.getTitle().equalsIgnoreCase(string))
                return quest;
        }
        return null;
    }
}
