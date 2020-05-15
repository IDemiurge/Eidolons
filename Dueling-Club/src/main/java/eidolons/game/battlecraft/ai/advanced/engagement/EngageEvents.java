package eidolons.game.battlecraft.ai.advanced.engagement;

import com.google.inject.internal.util.ImmutableList;
import eidolons.game.battlecraft.ai.explore.AggroMaster;
import main.content.enums.rules.VisionEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class EngageEvents {

    public void fireEvent(boolean b) {
        //TODO dummy
        GuiEventManager.trigger(GuiEventType.PLAYER_STATUS_CHANGED,
                new PlayerStatus(
                        b?
                                VisionEnums.PLAYER_STATUS.COMBAT:
                                VisionEnums.PLAYER_STATUS.EXPLORATION_UNDETECTED, AggroMaster.getAggroGroup().size()));
    }
    public void newEncounter() {
        GuiEventManager.trigger(GuiEventType.SHOW_LARGE_TEXT,
                ImmutableList.of("Encounter", "The Dummies" , 3f));


    }

}
