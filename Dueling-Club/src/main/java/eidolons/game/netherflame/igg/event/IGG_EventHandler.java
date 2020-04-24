package eidolons.game.netherflame.igg.event;

import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Eidolons;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

public class IGG_EventHandler extends GameEventHandler {

    public IGG_EventHandler(MetaGameMaster master) {
        super(master);
    }

    @Override
    public boolean handle(Event event) {

        if (event.getType() instanceof STANDARD_EVENT_TYPE) {
            switch (((STANDARD_EVENT_TYPE) event.getType())) {
                case UNIT_HAS_FALLEN_UNCONSCIOUS:
                    handleUnconscious(event);
                    break;
                case UNIT_HAS_BEEN_KILLED:
                    if (event.getRef().getTargetObj() == Eidolons.getMainHero()) {
                        waitForAnims();
                        getMaster().getShadowMaster().annihilated(event);
                    }
                    break;
                case TIME_ELAPSED:
                    getMaster().getShadowMaster().timeElapsed(event);
                    break;
                case COMBAT_ENDS:
                    getMaster().getShadowMaster().victory(event);
//                    getMaster().getDefeatHandler()
                    break;
            }
        }
        return super.handle(event);
    }


}
