package eidolons.game.module.dungeoncrawl.quest;

import main.elements.conditions.Condition;
import main.elements.triggers.Trigger;
import main.game.logic.event.Event;
import main.game.logic.event.Event.EVENT_TYPE;

/**
 * Created by JustMe on 10/9/2018.
 */
public class QuestTrigger extends Trigger {
    public QuestTrigger(EVENT_TYPE event, Condition condition) {
        super(event, condition);
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
