package eidolons.libgdx.utils;

/**
 * Created by JustMe on 5/28/2018.
 */
public class ActTimer {
    Runnable runnable;
    float period;
    float timer;

    public ActTimer( float period,Runnable runnable) {
        this.runnable = runnable;
        this.period = period;
    }

    public void act(float delta){
        timer+=delta;
        if (timer >= period) {
            timer=0f;
            runnable.run();
        }
    }

    public void setPeriod(float period) {
        this.period = period;
    }

    public void reset() {
        timer = 0;
    }
}
