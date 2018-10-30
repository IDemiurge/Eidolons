package eidolons.game.module.dungeoncrawl.quest;

import eidolons.ability.conditions.DynamicCondition;
import eidolons.game.core.Eidolons;
import main.elements.conditions.Condition;
import main.elements.conditions.ObjComparison;
import main.elements.triggers.Trigger;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.logic.event.Event.EVENT_TYPE;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 10/5/2018.
 *
 * if you have a global quest, its progress must be tracked in dungeons
 *
 * quest data
 *
 */
public class QuestResolver  extends QuestHandler{


    public QuestResolver(QuestMaster questMaster) {
        super(questMaster);
    }

    public   void questTaken(DungeonQuest quest){

        Runnable completionRunnable = getCompletionRunnable(quest);
        EVENT_TYPE event = getEvent(quest);
        Condition completionConditions = getCompletionConditions(quest);
        Condition  condition = getUpdateConditions(quest);

        Trigger updateTrigger= new QuestTrigger(event, condition,
         completionConditions, completionRunnable, quest);
        Eidolons.getGame().getState().addTrigger(updateTrigger);

    }

    private Condition getUpdateConditions(DungeonQuest quest) {
        switch (quest.getType()) {
            case BOSS:
                return new ObjComparison(()->((Obj) quest.getArg()).getId(), KEYS.TARGET.name());
            case HUNT:
            case COMMON_ITEMS:
                return new DynamicCondition<Obj>(obj ->
             (obj.getType()).equalsAsBaseType(quest.getArg()));
            case SPECIAL_ITEM:
                return new DynamicCondition<Obj>(obj ->
                 (obj).equals(quest.getArg()));

        }

        return null;
    }
    private Condition getCompletionConditions(DungeonQuest quest) {
       return new DynamicCondition<>(q -> {
            if (q.getNumberRequired() <=
             q.getNumberAchieved())
                return true;

            return false;
        }, quest);

    }

    private EVENT_TYPE getEvent(DungeonQuest quest) {
        switch (quest.getType()) {
            case BOSS:
            case HUNT:
                return STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED;
            case COMMON_ITEMS:
            case SPECIAL_ITEM:
                return STANDARD_EVENT_TYPE.ITEM_ACQUIRED;
            case ESCAPE:
//                return STANDARD_EVENT_TYPE.;
        }
        return null;
    }

    private Runnable getCompletionRunnable(DungeonQuest quest) {
        QuestReward reward = quest.getReward();
        return () -> {
            GuiEventManager.trigger(GuiEventType.QUEST_ENDED, quest);
            reward.award(Eidolons.getMainHero());
            quest.setComplete(true);
        };
    }
}
