package main.ability.effects.common;

import main.ability.effects.MicroEffect;
import main.ability.effects.ResistibleEffect;
import main.entity.obj.Obj;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.log.LogMaster;

public class OwnershipChangeEffect extends MicroEffect implements
 ResistibleEffect {

    private boolean neutral = false;
    private boolean permanent = false;
    private boolean berserker = false;

    public OwnershipChangeEffect() {
        this.permanent = false;
    }

    public OwnershipChangeEffect(Boolean permanent) {
        this.permanent = permanent;
    }

    public OwnershipChangeEffect(Boolean neutral, Boolean berserker) {

        this.neutral = neutral;
        this.berserker = berserker;
    }

    @Override
    public boolean applyThis() {
        if (!new Event(STANDARD_EVENT_TYPE.UNIT_OWNERSHIP_CHANGED, ref).fire()) {
            return false;
        }
        // + trigger rule flip image when ownership changes ;)
        Obj obj = ref.getTargetObj();
        Player newOwner = obj.getOriginalOwner();
        if (!neutral) {
            newOwner =
             // ref.getSourceObj().getOwner();
             ((GenericGame) ref.getGame()).getPlayer(!obj.getOwner().isMe());
        }
        obj.setOwner(newOwner);

        if (permanent) {
            obj.setOriginalOwner(newOwner);
            // obj.addProperty(G_PROPS.STATUS, STATUS.CHARMED.name());
        }
        if (berserker) {

        }
        LogMaster.log(LogMaster.CORE_DEBUG_1, ref
         .getTargetObj().getName() + "'s new owner: " + obj.getOwner());
        return true;
    }
}
