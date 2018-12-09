package eidolons.libgdx.shaders.post.cinematic;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by JustMe on 12/6/2018.
 */
public class CinematicFx {

    //scaled value
    List<Pair<Float, Float>> strengthMap;

    float targetStrength;
    float strength;
    float dx;
    float time;
    float timeInStretch;
    float ddx;

    boolean lerp;
    boolean acceleration;
    private int i=0;

    public CinematicFx(List<Pair<Float, Float>> strengthMap, boolean lerp, boolean acceleration) {
        this.strengthMap = strengthMap;
        this.lerp = lerp;
        this.acceleration = acceleration;
    }

    public void act(float delta){

        time += delta;
        timeInStretch -= delta;

        if (!acceleration) {
            dx = (targetStrength - strength) / timeInStretch;
        } else {

        }
        timeInStretch=strengthMap.get(i++).getKey();
        targetStrength=strengthMap.get(i++).getValue();

        dx+=ddx;
        strength+=dx;
    }
}
