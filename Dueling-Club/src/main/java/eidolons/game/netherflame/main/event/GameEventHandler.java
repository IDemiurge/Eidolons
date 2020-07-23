package eidolons.game.netherflame.main.event;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.rules.counter.UndyingCounterRule;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.system.text.tips.TipMessageMaster;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class GameEventHandler extends MetaGameHandler {

    public GameEventHandler(MetaGameMaster master) {
        super(master);
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
                case UNIT_ACTION_COMPLETE:
                    if (event.getRef().getSourceObj()== Eidolons.getMainHero()) {
                        GuiEventManager.trigger(GuiEventType.CLEAR_COMMENTS);
                    }

                    break;
                case INTRO_FINISHED:

                    TipMessageMaster.welcome();
                    break;
            }
        }
        return true;
    }

    protected void waitForAnims() {
        AnimMaster.waitForAnimations(null);
    }

    protected void handleUnconscious(Event event) {
        if (event.getRef().getSourceObj() instanceof Unit) {
            if (((Unit) event.getRef().getSourceObj()).isPlayerCharacter()) {
                getMaster().getDefeatHandler().fallsUnconscious(event);

            }
        }
    }
}
