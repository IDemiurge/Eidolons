package main.ability.effects.oneshot.common;

import main.ability.effects.ResistibleEffect;
import main.ability.effects.oneshot.MicroEffect;
import main.entity.obj.Obj;
import main.game.MicroGame;
import main.game.event.Event;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.game.player.Player;
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
                    ((MicroGame) ref.getGame()).getPlayer(!obj.getOwner().isMe());
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
