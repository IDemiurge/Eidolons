package eidolons.game.module.dungeoncrawl.quest;

import eidolons.ability.conditions.DynamicCondition;
import eidolons.game.core.Eidolons;
import main.elements.conditions.Condition;
import main.elements.conditions.ObjComparison;
import main.elements.triggers.Trigger;
import main.entity.Ref.KEYS;
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

        Runnable runnable = getRunnable(quest);
        EVENT_TYPE event = getEvent(quest);
        Condition condition = getConditions(quest);
        Trigger trigger = new Trigger(event, condition);
        trigger.setCallback(runnable);
        trigger.setRemoveAfterTriggers(true);
        Eidolons.getGame().getState().addTrigger(trigger);

        condition = getUpdateConditions(quest);
        Trigger updateTrigger= new Trigger(event, condition);
        updateTrigger.setRemoveAfterTriggers(false);
        updateTrigger.setCallback(()->{
            quest.numberAchieved++;
            quest.update();
         }
        );

        Eidolons.getGame().getState().addTrigger(updateTrigger);

        GuiEventManager.trigger(GuiEventType.QUEST_STARTED, quest);
    }

    private Condition getUpdateConditions(DungeonQuest quest) {
        switch (quest.getType()) {
            case BOSS:
                return new ObjComparison((Integer) quest.getArg(), KEYS.TARGET.name());
        }

        return null;
    }
    private Condition getConditions(DungeonQuest quest) {
        new DynamicCondition<>(q -> {
            if (q.getNumberRequired() <=
             q.getNumberAchieved())
                return true;

            return false;
        }, quest);

        return null;
    }

    private EVENT_TYPE getEvent(DungeonQuest quest) {
        switch (quest.getType()) {
            case BOSS:
            case HUNT:
                return STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED;
            case FIND:
                return STANDARD_EVENT_TYPE.ITEM_ACQUIRED;
            case ESCAPE:
//                return STANDARD_EVENT_TYPE.;
        }
        return null;
    }

    private Runnable getRunnable(DungeonQuest quest) {
        QuestReward reward = quest.getReward();
        return () -> {
            GuiEventManager.trigger(GuiEventType.QUEST_ENDED, quest);
            reward.award(Eidolons.getMainHero());
            quest.setComplete(true);
        };
    }
}
