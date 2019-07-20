package eidolons.game.battlecraft.logic.meta.igg.event;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.igg.IGG_MetaMaster;
import eidolons.game.battlecraft.logic.meta.igg.death.ShadowMaster;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.rules.counter.UndyingCounterRule;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.system.audio.DC_SoundMaster;
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

    public boolean handle(Event event) {
        SoundEvents.checkEventSound(event);
        getMaster().getDungeonMaster().getPuzzleMaster().processEvent(event);

        if (event.getType() instanceof Event.STANDARD_EVENT_TYPE) {
            switch (((Event.STANDARD_EVENT_TYPE) event.getType())) {

                case HERO_LEVEL_UP:
                    TipMessageMaster.onEvent(event.getType());
                    break;
                case UNIT_IS_FALLING_UNCONSCIOUS:
                case UNIT_IS_BEING_KILLED:
//                    if (ShadowMaster.checkCheatDeath(event)) {
//                        return false;
//                    }
                    if (UndyingCounterRule.check(event)){
                        return false;
                    }
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
                case UNIT_TURN_STARTED:
                    break;
                case UNIT_HAS_FALLEN_UNCONSCIOUS:
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
        return true;
    }

    private void waitForAnims() {
        AnimMaster.waitForAnimations(null);
    }

    private void handleUnconscious(Event event) {
        if (event.getRef().getSourceObj() instanceof Unit) {
            if (((Unit) event.getRef().getSourceObj()).isPlayerCharacter()) {
                getMaster().getDefeatHandler().fallsUnconscious(event);

            }
        }
    }
}
