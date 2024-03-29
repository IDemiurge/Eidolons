package eidolons.game.exploration.story.quest;

import eidolons.ability.conditions.DynamicCondition;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import main.elements.conditions.Condition;
import main.elements.conditions.ObjComparison;
import main.elements.triggers.Trigger;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.logic.event.Event.EVENT_TYPE;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;

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
        Core.getGame().getState().addTrigger(updateTrigger);

    }

    private Condition getUpdateConditions(DungeonQuest quest) {
        switch (quest.getType()) {
            case BOSS:
                return new ObjComparison(()->((Obj) quest.getArg()).getId(), KEYS.TARGET.name());
            case HUNT:
            case COMMON_ITEMS:
            case OBJECTS:
                return new DynamicCondition<ObjType>(obj ->
             checkType(obj, quest));
            case SPECIAL_ITEM:
                return new DynamicCondition<Obj>(obj ->
                 (obj).equals(quest.getArg()));

        }

        return null;
    }

    private boolean checkType(ObjType obj, DungeonQuest quest) {
        if ((obj.getType()).equalsAsBaseType(quest.getArg())) {
            return true;
        }
        if (quest.getArg() instanceof String) {
            for(String substring: ContainerUtils.openContainer( ((String) quest.getArg()))){
                if ((obj.getType().getName()).toLowerCase().contains(substring.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Condition getCompletionConditions(DungeonQuest quest) {
       return new DynamicCondition<>(q -> {
           return q.getNumberRequired() <=
                   q.getNumberAchieved();
       }, quest);

    }

    private EVENT_TYPE getEvent(DungeonQuest quest) {
        switch (quest.getType()) {
            case BOSS:
            case HUNT:
                return STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED;
            case OBJECTS:
                return STANDARD_EVENT_TYPE.INTERACTIVE_OBJ_USED;
            case SECRETS:
                return STANDARD_EVENT_TYPE.SECRET_FOUND;
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
            EUtils.onConfirm("Congratulations! You have completed the quest " +
             quest.getTitle() +
             "! " +
             reward.getGoldFormula() +
             " gold pieces promised to you await you in town" +
//      TODO        quest.getTown().getName() +
             ". Probably...", false, ()->{
                GuiEventManager.trigger(GuiEventType.QUEST_ENDED, quest);
                GuiEventManager.trigger(GuiEventType.QUEST_UPDATE, quest);
                GuiEventManager.trigger(GuiEventType.QUESTS_UPDATE_REQUIRED);
                reward.award(Core.getMainHero(), false);
                quest.setComplete(true);
            } );
        };
    }
}
