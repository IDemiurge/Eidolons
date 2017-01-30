package main.libgdx.anims.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.Particle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import main.libgdx.anims.particles.Emitter.EMITTER_VALS_SCALED;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.secondary.ReflectionMaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by JustMe on 1/27/2017.
 */
public class ParticleEffect extends com.badlogic.gdx.graphics.g2d.ParticleEffect {

    public void offset(String offset, String value) {
        offset(Float.valueOf(offset), new EnumMaster<EMITTER_VALS_SCALED>().retrieveEnumConst(EMITTER_VALS_SCALED.class, value));
    }

    public void offset(float offset, EMITTER_VALS_SCALED value) {
        for (ParticleEmitter e : getEmitters()) {
            Emitter emitter = (Emitter) e;
            emitter.offset(offset, value.name().toLowerCase());
        }
    }

    public void offsetAngle(float offset) {
        for (ParticleEmitter e : getEmitters()) {
            Emitter emitter = (Emitter) e;
            emitter.offsetAngle(offset);
        }
    }



    public void modifyParticles() {
        Arrays.stream(getParticles()).forEach(p->{
            float x = Gdx.input.getX()-p.getX();
            float y = Gdx.input.getY()-p.getY();
            Float distance = (float) (Math.sqrt( x *  x + y * y));
            if (distance>100)return ;
            p.setAlpha(1f- distance/100);

            
        });
    }
        public Particle[] getParticles() {
        return
         (Particle[]) new ReflectionMaster<>().getFieldValue("particles", this, ParticleEmitter.class);
    }

    public void set(String choice, String value) {
        for (ParticleEmitter e : getEmitters()) {
            Emitter emitter = (Emitter) e;
            emitter.set(choice, value.toLowerCase());
        }
    }

    private boolean checkSprite(FileHandle effectFile) {
        if (EmitterActor.spriteEmitterTest)
            return true;
        return
         EmitterPresetMaster.getInstance().getImagePath(effectFile.path()).contains("sprites");
    }

    public void loadEmitters(FileHandle effectFile) {
        InputStream input = effectFile.read();
        getEmitters().clear();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(input), 512);
            while (true) {
                ParticleEmitter emitter = (checkSprite(effectFile)) ? new SpriteEmitter(reader
                ) : new Emitter(reader);
                getEmitters().add(emitter);
                if (reader.readLine() == null) break;
                if (reader.readLine() == null) break;
            }
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error loading effect: " + effectFile, ex);
        } finally {
            StreamUtils.closeQuietly(reader);
        }
    }

    public void setImagePath(String imagePath) {
        for (int i = 0, n = getEmitters().size; i < n; i++)
            getEmitters().get(i).setImagePath(imagePath);
    }

}
