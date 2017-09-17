package main.game.logic.action.context;

import main.entity.Ref;
import main.entity.obj.Obj;
import main.system.launch.CoreEngine;

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
        if (target != null) {
            setTarget(target.getId());
        }
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
        setDebug(ref.isDebug()); if (CoreEngine.isPhaseAnimsOn())
        setAnimationActive(ref.getAnimationActive());

        target = getTargetObj();
        source = getSourceObj();
    }

    @Override
    public Obj getTargetObj() {
        if (target == null) {
            return super.getTargetObj();
        }
        return target;
    }


    public enum IdKey{

        TARGET,
        SOURCE,
        BASIS,
        ACTIVE,
        SPELL,
        WEAPON,
        ARMOR,
        OFFHAND,
        BUFF,
        SUMMONER,
        SUMMONED,
        PAYEE,
        ITEM,
        SKILL,
        PARTY,
        INFO,
        AMMO,
        RANGED,
    }
}
