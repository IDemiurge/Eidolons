package main.game.logic.action.context;

import main.entity.Ref;
import main.entity.obj.Obj;

import java.util.Map;

/**
 * Created by JustMe on 3/23/2017.
 */
public class Context extends Ref {
    Obj source;
    Obj target;
    Map<IdKey, Integer> idMap;

    public Context(Obj source, Obj target) {
        super(source);
        this. source = source;
        this. target = target;
        setTarget(target.getId());
    }

    @Override
    public Obj getTargetObj() {
        return target;
    }

    public Context(Ref ref){
        
        cloneMaps(ref );
        setPlayer(ref.getPlayer());
        setEvent(ref.event);
        setGroup(ref.getGroup());
        setBase(ref.base);
        setGame(ref.game);
        setEffect(ref.getEffect());
        setTriggered(ref.isTriggered());
        setDebug(ref.isDebug());
        setAnimationActive(ref.getAnimationActive());
    }


    public enum IdKey{

    }
}
