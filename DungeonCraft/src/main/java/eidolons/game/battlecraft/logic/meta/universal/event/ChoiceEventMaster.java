package eidolons.game.battlecraft.logic.meta.universal.event;

import eidolons.game.battlecraft.rules.combat.attack.accuracy.Deadeye;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

public class ChoiceEventMaster<T extends ChoiceEventOption> {
    public   T promptAndWait(T[] options) {
        GuiEventManager.trigger(GuiEventType.VISUAL_CHOICE, options);
        T result = (T) WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.VISUAL_CHOICE);
        return result;
    }
}
