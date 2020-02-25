package eidolons.game.battlecraft.logic.meta.igg.story;

import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.module.dungeoncrawl.quest.DungeonQuest;
import eidolons.game.module.dungeoncrawl.quest.QuestMaster;
import eidolons.game.module.dungeoncrawl.quest.advanced.Quest;
import main.content.enums.meta.QuestEnums;
import main.entity.type.ObjType;

import java.util.ArrayList;
import java.util.Set;

public class IGG_QuestMaster extends QuestMaster {
    public IGG_QuestMaster(MetaGameMaster master) {
        super(master);
    }

    protected String getQuestGroupFilter() {
        return QuestEnums.QUEST_GROUP.DEMO.toString();
    }

    protected boolean checkQuestForLocation(ObjType q) {
        return true;
    }

    @Override
    public boolean initQuests() {
        quests = new ArrayList<>();
        if (TEST_MODE)
        for (ObjType type : getQuestTypePool()) {
            DungeonQuest quest = getCreator().create(type);
            quests.add(quest);
        }
        return true;
    }

    @Override
    public Set<Quest> getQuestsPool() {
        return super.getQuestsPool();
    }

    @Override
    public Set<ObjType> getQuestTypePool() {
        return super.getQuestTypePool();
    }
}
