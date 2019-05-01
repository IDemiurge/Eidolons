package eidolons.game.battlecraft.logic.meta.igg.event;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Images;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Meta;
import eidolons.game.battlecraft.logic.meta.igg.death.IGG_DefeatHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.system.text.DC_LogManager;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class GameEventHandler extends MetaGameHandler {

    public GameEventHandler(MetaGameMaster master) {
        super(master);
    }

    public void handle(Event event) {

        if (event.getType() instanceof Event.STANDARD_EVENT_TYPE) {
            switch (((Event.STANDARD_EVENT_TYPE) event.getType())) {
                case UNIT_TURN_STARTED:
                    getGame().getLogManager().log(DC_LogManager.UNIT_TURN_PREFIX
                            + event.getRef().getSourceObj().getNameIfKnown());
                    break;
                    case DOOR_CLOSES:
                    TipMessageMaster.testChained();
                    break;
                case DOOR_OPENS:
                    TipMessageMaster.test();
                    break;
                case UNIT_FALLS_UNCONSCIOUS:
                    handleUnconscious(event);
                    break;
                case COMBAT_ENDS:
                    break;
                case GAME_STARTED:

                    TipMessageMaster.welcome();
                    break;
            }
        }
    }

    private void handleUnconscious(Event event) {
        if (event.getRef().getTargetObj() instanceof Unit) {
            if (((Unit) event.getRef().getTargetObj()).isMainHero()) {
                getGame().getLoop().setPaused(true);
                GuiEventManager.trigger(GuiEventType. TIP_MESSAGE, new TipMessageSource(
                        TipMessageMaster.MESSAGE_TIPS.UNCONSCIOUS.message,
                        IGG_Images.SHADOW,"Summon Shade",false, ()->
                        getMaster().getDefeatHandler().fallsUnconscious(event)));
            }
        }
    }
}
