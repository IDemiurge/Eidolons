package main.libgdx.anims.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import main.libgdx.GameScreen;
import main.system.auxiliary.secondary.ReflectionMaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by JustMe on 1/27/2017.
 */
public class Emitter extends ParticleEmitter {

    private boolean particleLogOn;

    public Emitter(BufferedReader reader) throws IOException {
        super(reader);
    }

    public void offsetColor(float offset) {
//        float[] colors = getColorValue().getColors();
//        getColorValue().setColors(modifiedColors);
    }

    public void set(String choice, String s) {
        Object val = getValue(s);
        if (val instanceof ScaledNumericValue) {
            ScaledNumericValue value = getScaledNumericValue(choice);
            Float f = Float.valueOf(s);
            value.setHigh(f, f);
            value.setLow(f, f);
        } else {
            Object v = s;
//     if (val instanceof )
            new ReflectionMaster<>().setValue(choice, v, this);
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
//                GridMaster.getMouseCoordinates

            float x = Gdx.input.getX() - (getX()+p.getX())
//             - GameScreen.getInstance().getController().getX_cam_pos()
             ;
            float y =(Gdx.graphics.getHeight()- Gdx.input.getY()) //fuck that shit 
             -(getY()+p.getY())
//             - GameScreen.getInstance().getController().getY_cam_pos()
             ;
            Float distance = (float) (Math.sqrt(x * x + y * y));
            if (particleLogOn)
            main.system.auxiliary.LogMaster.log(1,
             " Mouse x: "+ Gdx.input.getX()
             + " Mouse y: "+ (Gdx.graphics.getHeight()- Gdx.input.getY()) //fuck that shit
              + " Particle x: " + (getY()+p.getX())
              + " Particle y: " + (getY()+p.getY())
              + " cam x: " + (GameScreen.getInstance().getController().getX_cam_pos())
              + " cam y: " + (GameScreen.getInstance().getController().getY_cam_pos())
              + " distance: " + (distance)
            );
                if (distance > 500) return;
            p.setAlpha(1f - distance / 500);
            p.setScale(3f-distance/250,3f-distance/250);
            }


        });
    }

    public Particle[] getParticles() {
        try {
            return (Particle[]) new ReflectionMaster<>().getFieldValue("particles",
             this, ParticleEmitter.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Particle[0];
    }


    public enum EMITTER_VALS_SCALED {
        ANGLE,
        LIFE,
        EMISSION,
        SCALE,
        ROTATION,
        VELOCITY,
        WIND, GRAVITY, TRANSPARENCY,
    }

    public enum EMITTER_VALS_RANGED {
        DELAY,
        DURATION,
    }
}
