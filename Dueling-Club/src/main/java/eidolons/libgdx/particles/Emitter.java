package eidolons.libgdx.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.screens.dungeon.DungeonScreen;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.ReflectionMaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by JustMe on 1/27/2017.
 */
public class Emitter extends ParticleEmitter {

    public Emitter(BufferedReader reader) throws IOException {
        super(reader);
    }

    public void toggle(String fieldName) {
        boolean value =
         new ReflectionMaster<Boolean>().getFieldValue(fieldName,
          this, ParticleEmitter.class);
        new ReflectionMaster().setValue(fieldName, !value, this, ParticleEmitter.class);
    }

    public void set(String fieldName, String fieldValue) {
        Object val = getValue(fieldName);
        if (val instanceof ScaledNumericValue) {
            ScaledNumericValue value = getScaledNumericValue(fieldName);
            Float f = Float.valueOf(fieldValue);
            value.setHigh(f, f);
            value.setLow(f, f);
        } else {
            Object v = fieldValue;
            new ReflectionMaster<>().setValue(fieldName, v, this);
        }
    }

    private Object getValue(String s) {

        return new ReflectionMaster<>().
         getFieldValue(s, this, ParticleEmitter.class);

    }

    public void offsetAngle(float offset) {
        offset(offset, "angle");
    }

    public void offset(float offset, String name) {


        ScaledNumericValue val = getScaledNumericValue(name + "Value");
        float min = val.getHighMin();
        float max = val.getHighMax();
        val.setHigh(min + offset, max + offset);
        min = val.getLowMin();
        max = val.getLowMax();
        val.setLow(min + offset, max + offset);
    }

    public ScaledNumericValue getScaledNumericValue(String fieldName) {
        return new ReflectionMaster<ScaledNumericValue>().getFieldValue(fieldName, this, ParticleEmitter.class);

    }

    public GradientColorValue getColorValue() {
        return new ReflectionMaster<GradientColorValue>().getFieldValue("tintValue", this, ParticleEmitter.class);

    }

    public void modifyParticles() {
        Arrays.stream(getParticles()).forEach(p -> {
            if (p != null) {
//                GridMaster.getMouseCoordinates;
                Vector2 v = new Vector2(Gdx.input.getX(), (GdxMaster.getHeight() - Gdx.input.getY()));
                Vector2 pos = DungeonScreen.getInstance().getGridStage().screenToStageCoordinates(v);
                float xDiff = pos.x
                 - DungeonScreen.getInstance().controller.getXCamPos()
                 - (getX() + p.getX());
                float yDiff = pos.y //fuck that shit
                 - (getY() + p.getY())
                 - DungeonScreen.getInstance().controller.getYCamPos();
                Float distance = (float) (Math.sqrt(xDiff * xDiff + yDiff * yDiff));
                // if (particleLogOn)
                {
                    LogMaster.log(1,
                     " Mouse x: " + pos.x
                      + " Mouse y: " + pos.y
                      + " Particle x: " + (getY() + p.getX())
                      + " Particle y: " + (getY() + p.getY())
                      + " cam x: " + (DungeonScreen.getInstance().controller.getXCamPos())
                      + " cam y: " + (DungeonScreen.getInstance().controller.getYCamPos())
                      + " distance: " + (distance)
                    );
                }
                if (distance > 500) {
                    return;
                }
                p.setAlpha(1f - distance / 500);
                p.setScale(3f - distance / 250, 3f - distance / 250);
            }


        });
    }

    public Particle[] getParticles() {
        try {
            return (Particle[]) new ReflectionMaster<>().getFieldValue("particles",
             this, ParticleEmitter.class);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return new Particle[0];
    }


    public enum EMITTER_VALS_RANGED {
        DELAY,
        DURATION,
    }

    //    @Override
//    public float getPercentComplete () {
//        if (delayTimer <  delay) return 0;
//        return MathMaster.minMax( durationTimer / (float)duration, 0 ,1);
//    }
    public enum EMITTER_VALS_SCALED {
        ANGLE,
        LIFE,
        EMISSION,
        SCALE,
        ROTATION,
        VELOCITY,
        WIND, GRAVITY, TRANSPARENCY,
    }
}
