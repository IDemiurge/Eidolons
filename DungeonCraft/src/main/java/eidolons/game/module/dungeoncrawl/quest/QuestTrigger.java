package eidolons.game.module.dungeoncrawl.quest;

import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import main.elements.conditions.Condition;
import main.elements.triggers.Trigger;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.logic.event.Event;
import main.game.logic.event.Event.EVENT_TYPE;

/**
 * Created by JustMe on 10/9/2018.
 */
public class QuestTrigger extends Trigger {
    Condition completionConditions;
    Runnable completionRunnable;
    DungeonQuest quest;

    public QuestTrigger(EVENT_TYPE event, Condition condition) {
        super(event, condition);
    }

    public QuestTrigger(EVENT_TYPE eventType, Condition conditions, Condition completionConditions, Runnable completionRunnable, DungeonQuest quest) {
        super(eventType, conditions);
        this.completionConditions = completionConditions;
        this.completionRunnable = completionRunnable;
        this.quest = quest;
        setRemoveAfterTriggers(false);

        setCallback(() -> {
            quest.increment();
            try {
                specialAction(quest, event);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

            if (completionConditions.check(event.getRef())) {
                completionRunnable.run();
                remove();
            }
        });
    }

    private void specialAction(DungeonQuest quest, Event event) {
        switch (quest.getType()) {
            case SPECIAL_ITEM:
            case COMMON_ITEMS:
                {
                Obj item = event.getRef().getObj(KEYS.ITEM);
                EUtils.showInfoText("Quest item found: " + item.getName());
                Unit hero = (Unit) event.getRef().getSourceObj();
                hero.removeFromInventory((DC_HeroItemObj) item);
                break;
            }
        }
    }


    @Override
    public boolean isRemoveOnReset() {
        return false;
    }

    @Override
    public boolean trigger() {
        return super.trigger();


    }

    @Override
    public boolean check(Event event) {
        return super.check(event);


    }
}
