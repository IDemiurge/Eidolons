package main.ability;

import main.ability.effects.Effects;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Attachment;
import main.entity.obj.Obj;
import main.game.core.game.Game;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.battle.player.Player;

public class PassiveAbilityObj extends AbilityObj implements Attachment {

    private Obj ownerObj;
    private boolean retainAfterDeath;
    private Conditions retainConditions;
    private int dur;
    private boolean applied;

    public PassiveAbilityObj(AbilityType type, Ref ref, Player player, Game game) {
        super(type, ref, player, game);
        this.ownerObj = ref.getSourceObj();
    }

    public Effects getEffects() {
        return abilities.getEffects();
    }

    // @Override
    // public boolean activate() {
    // boolean result = true;
    //
    //
    // return result;
    // }

    @Override
    public boolean kill() {
        Ref REF = Ref.getCopy(ref);
        if (isDead()) {
            return false;
        }

        if (!game.fireEvent(new Event(
                STANDARD_EVENT_TYPE.PASSIVE_BEING_REMOVED, REF))) {
            return false;
        }

        setDead(true);
        game.getManager().attachmentRemoved(this, ownerObj);

        game.fireEvent(new Event(STANDARD_EVENT_TYPE.PASSIVE_REMOVED, REF));

        applied = false;

        return super.kill();
    }

    @Override
    public void remove() {
        kill();

    }

    @Override
    public int getDuration() {
        return dur;
    }

    @Override
    public int tick() {
        return 0;
    }

    @Override
    public boolean isRetainAfterDeath() {
        return retainAfterDeath;
    }

    @Override
    public void setRetainAfterDeath(boolean retainAfterDeath) {
        this.retainAfterDeath = retainAfterDeath;
    }

    @Override
    public void setRef(Ref ref) {
        super.setRef(ref);
        this.ref.setID(KEYS.ACTIVE, null);
    }

    @Override
    public boolean checkRetainCondition() {
        if (retainConditions != null) {
            if (retainConditions.check(ref)) {
                kill();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isTransient() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setTransient(boolean b) {
        // TODO Auto-generated method stub

    }

    @Override
    public Obj getBasis() {
        return getRef().getObj(KEYS.BASIS);
    }

}
