package eidolons.game.battlecraft.rules.counter.timed;

import main.entity.obj.Obj;

/**
 * Created by JustMe on 4/9/2018.
 */
public class EffectTimer<R extends Runnable, O extends Obj> {

    private R runnable;
    private O object;
    private float period;
    private float timer;

    public EffectTimer(R runnable, O object, float period) {
        this.runnable = runnable;
        this.object = object;
        this.period = period;
    }

//    public void timePassed(float time) {
//        timer+=time;
//        if (!runnable.check(object)){
//            //TODO remove
//            timer = 0;
//            return;
//        }
//        while (timer>=getTimePeriod()){
//            timer -= getTimePeriod();
//            runnable.apply(object);
//        }
//    }

    protected float getTimePeriod() {
        return period;
    }

}
