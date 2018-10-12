package eidolons.game.module.dungeoncrawl.quest;

import main.elements.conditions.Condition;
import main.elements.triggers.Trigger;
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
}
