package eidolons.system.libgdx.datasource;

import eidolons.entity.active.ActiveObj;
import main.ability.effects.Effect;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.logic.action.context.Context;
import main.game.logic.event.Event;

/**
 * Created by JustMe on 11/9/2017.
 */
public class AnimContext extends Context {
    private boolean attachToNext;

    public AnimContext(Obj source, Obj target, Event event) {
        super(source, target);
    }

    public AnimContext(Obj source, Obj target, Effect effect) {
        super(source, target);
    }

    public AnimContext(Ref ref) {
        super(ref);
    }

    public AnimContext(Obj source, Obj target) {
        super(source, target);
        Coordinates sourceCoordinate;
        if (source != null)
            sourceCoordinate = source.getCoordinates();
        Coordinates targetCoordinate;
        if (target != null)
            targetCoordinate = target.getCoordinates();

    }

    public AnimContext(ActiveObj action) {
        super(action.getRef());
        attachToNext = action.isCounterMode();
    }

    public boolean isAttachToNext() {
        return attachToNext;
    }

    public void setAttachToNext(boolean attachToNext) {
        this.attachToNext = attachToNext;
    }
}
