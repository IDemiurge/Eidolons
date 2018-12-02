package eidolons.libgdx.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import eidolons.libgdx.particles.Emitter.EMITTER_VALS_SCALED;
import eidolons.libgdx.particles.util.EmitterMaster;
import eidolons.libgdx.particles.util.EmitterPresetMaster;
import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 1/27/2017.
 */
public class ParticleEffectX extends com.badlogic.gdx.graphics.g2d.ParticleEffect {

    public static final boolean TEST_MODE = true;
    public String path;
    private static List<String> broken=    new ArrayList<>() ;

    public ParticleEffectX(String path) {
        this.path = path;

        if (broken.contains(path))
            return;
        if (isEmitterAtlasesOn()) {
            FileHandle presetFile = Gdx.files.internal(path);
            if (!presetFile.exists())
                presetFile=Gdx.files.internal(PathFinder.getVfxPath()+path);

            try {
                load(presetFile, getEmitterAtlas());
                return;
            } catch (Exception e) {
                broken.add(path);
                e.printStackTrace();
            }
        }
        String imagePath = EmitterPresetMaster.getInstance().getImagePath(path);
        if (StringMaster.isEmpty(imagePath)) {
            return;
        }
        if (FileManager.isImageFile(PathUtils.getLastPathSegment(imagePath))) {
            imagePath = PathUtils.cropLastPathSegment(imagePath);
        }
        load(Gdx.files.internal(
         PathUtils.addMissingPathSegments(
          path, PathFinder.getParticlePresetPath())),
         Gdx.files.internal(imagePath));

//TODO         batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    protected Texture loadTexture (FileHandle file) {
        return new Texture(file, false);
    }
    public ParticleEffectX() {
        super();
    }

    public static  boolean isEmitterAtlasesOn() {
        return true;//!CoreEngine.isFastMode();
    }

    private TextureAtlas getEmitterAtlas() {
        return EmitterMaster.getAtlas(path);
    }

    public void offset(String value, String offset) {
        offset(Float.valueOf(offset),
         new EnumMaster<EMITTER_VALS_SCALED>().retrieveEnumConst(EMITTER_VALS_SCALED.class, value));
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


    public void set(String valName, String value) {
        for (ParticleEmitter e : getEmitters()) {
            Emitter emitter = (Emitter) e;
            emitter.set(valName, value.toLowerCase());
        }
    }

    private boolean checkSprite(FileHandle effectFile) {
        if (EmitterActor.spriteEmitterTest) {
            return true;
        }
        String imgPath = EmitterPresetMaster.getInstance().getImagePath(effectFile.path());
        if (imgPath.contains("sprites")){
            main.system.auxiliary.log.LogMaster.log(1,effectFile.path()+" is a SPRITE!.. " );
            return true;
        }
        main.system.auxiliary.log.LogMaster.log(1, effectFile.path() + " created with imgPath " + imgPath);
        return false;
    }

    @Override
    public Array<ParticleEmitter> getEmitters() {
        return super.getEmitters();
    }

    public void loadEmitters(FileHandle effectFile) {
        try {
            loadEmitters_(effectFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getEmitters().forEach(emitter -> emitter.setCleansUpBlendFunction(false));
    }
    public void loadEmitters_(FileHandle effectFile) throws IOException {
//        if (CoreEngine.isMacro())
        if (!effectFile.exists()){
            broken.add(effectFile.path());
            main.system.auxiliary.log.LogMaster.log(1,"no such emitter preset: " +effectFile.path());
            return;
        }
        InputStream input = effectFile.read();
        getEmitters().clear();
        BufferedReader reader = null;

            reader = new BufferedReader(new InputStreamReader(input), 512);
            while (true) {
                ParticleEmitter emitter = (checkSprite(effectFile)) ? new SpriteEmitter(reader
                ) : new Emitter(reader);

                getEmitters().add(emitter);
                if (reader.readLine() == null) {
                    break;
                }
                if (reader.readLine() == null) {
                    break;
                }
            }

    }

    public void setImagePath(String imagePath) {
        Array<String> array = new Array<>();
        array.add(imagePath);
        for (int i = 0, n = getEmitters().size; i < n; i++) {
            getEmitters().get(i).setImagePaths(array);
        }
    }

    public void modifyParticles() {
        for (int i = 0, n = getEmitters().size; i < n; i++) {
            Emitter e = (Emitter) getEmitters().get(i);
            e.modifyParticles();
        }
    }

    public void toggle(String fieldName) {
        for (int i = 0, n = getEmitters().size; i < n; i++) {
            Emitter e = (Emitter) getEmitters().get(i);
            e.toggle(fieldName);
        }
    }

}
