package main.libgdx.anims;

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
    private  Coordinates sourceCoordinate;
    private  Coordinates targetCoordinate;
    private  Effect effect;
    private  Event event;

    public AnimContext(Obj source, Obj target, Event event) {
        super(source, target);
        this.event = event;
    }

    public AnimContext(Obj source, Obj target, Effect effect) {
        super(source, target);
        this.effect = effect;
    }

    public AnimContext(Ref ref) {
        super(ref);
    }

    public AnimContext(Obj source, Obj target) {
        super(source, target);
        if (source != null)
            this.sourceCoordinate = source.getCoordinates();
        if (target != null)
        this.targetCoordinate = target.getCoordinates();
    }
}
