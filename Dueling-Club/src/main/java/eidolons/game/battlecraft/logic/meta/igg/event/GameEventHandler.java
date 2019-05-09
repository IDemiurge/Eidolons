package eidolons.game.battlecraft.logic.meta.igg.event;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.igg.IGG_MetaMaster;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.system.text.DC_LogManager;
import main.game.logic.event.Event;
import main.system.threading.WaitMaster;

public class GameEventHandler extends MetaGameHandler {

    public GameEventHandler(MetaGameMaster master) {
        super(master);
    }

    @Override
    public IGG_MetaMaster getMaster() {
        return (IGG_MetaMaster) super.getMaster();
    }

    public void handle(Event event) {

        if (event.getType() instanceof Event.STANDARD_EVENT_TYPE) {
            switch (((Event.STANDARD_EVENT_TYPE) event.getType())) {
                case UNIT_HAS_BEEN_KILLED:
                    if (event.getRef().getTargetObj() == Eidolons.getMainHero()) {
                        waitForAnims();
                        getMaster().getShadowMaster().annihilated(event);
                    }

                    break;
                case TIME_ELAPSED:
                    getMaster().getShadowMaster().timeElapsed(event);
                    break;
                case UNIT_TURN_STARTED:
                    break;
                case UNIT_FALLS_UNCONSCIOUS:
                    handleUnconscious(event);
                    break;
                case COMBAT_ENDS:
                    getMaster().getShadowMaster().victory(event);
//                    getMaster().getDefeatHandler()
                    break;
                case GAME_STARTED:

                    TipMessageMaster.welcome();
                    break;
            }
        }
    }

    private void waitForAnims() {
        AnimMaster.waitForAnimations(null);
    }

    private void handleUnconscious(Event event) {
        if (event.getRef().getSourceObj() instanceof Unit) {
            if (((Unit) event.getRef().getSourceObj()).isMainHero()) {
                getMaster().getDefeatHandler().fallsUnconscious(event);

            }
        }
    }
}
