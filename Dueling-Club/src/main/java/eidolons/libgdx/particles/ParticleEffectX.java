package eidolons.libgdx.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;
import eidolons.libgdx.particles.Emitter.EMITTER_VALS_SCALED;
import eidolons.libgdx.particles.util.EmitterMaster;
import eidolons.libgdx.particles.util.EmitterPresetMaster;
import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 1/27/2017.
 */
public class ParticleEffectX extends ParticleEffect {

    public static final boolean TEST_MODE = false;
    private static float globalAlpha;
    public String path;
    private static final List<String> broken = new ArrayList<>();
    private Float alpha = 1f;
    private boolean spell;
    private VfxPool pool;

    public ParticleEffectX(ParticleEffect effect) {
        super(effect);
    }

    public ParticleEffectX() {
    }

    public ParticleEffectX(String path) {
        this.path = path;
        if (broken.contains(path))
            return;
        if (isEmitterAtlasesOn()) {
            FileHandle presetFile = new FileHandle(path);
            if (!presetFile.exists())
                presetFile = Gdx.files.internal(PathFinder.getVfxAtlasPath() + path);
            if (!presetFile.exists())
                presetFile = Gdx.files.internal(PathFinder.getVfxPath() + path);
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

        load(Gdx.files.internal(PathUtils.addMissingPathSegments(path, PathFinder.getParticlePresetPath())),
                Gdx.files.internal(imagePath));


    }

    public static void setGlobalAlpha(float globalAlpha) {
        ParticleEffectX.globalAlpha = globalAlpha;
    }


    public void setAlpha(Float a) {
        this.alpha = a;
        if (a != 1f) {
            for (ParticleEmitter e : getEmitters()) {
                //                e.getTransparency().set
                //                float a = e.getTransparency().getScale(e.getPercentComplete());

                float min = e.getTransparency().getHighMin();
                float max = e.getTransparency().getHighMax();
                float lowMax = e.getTransparency().getLowMax();
                float lowMin = e.getTransparency().getLowMin();

                e.getTransparency().setHigh(min * a, max * a);
                e.getTransparency().setLow(lowMin * a, lowMax * a);

            }
        }
    }

    @Override
    public void draw(Batch spriteBatch) {
        if (globalAlpha != alpha) {
            setAlpha(globalAlpha);
        }
        super.draw(spriteBatch);
    }

    @Override
    public void draw(Batch spriteBatch, float delta) {
        if (!spell)
            if (globalAlpha != alpha) {
                setAlpha(globalAlpha);
            }
        super.draw(spriteBatch, delta);
    }

    @Override
    public Array<ParticleEmitter> getEmitters() {
        return super.getEmitters();
    }

    protected Texture loadTexture(FileHandle file) {
        return new Texture(file, false);
    }


    public static boolean isEmitterAtlasesOn() {
        return true;//!CoreEngine.isFastMode();
    }

    private TextureAtlas getEmitterAtlas() {
        EmitterMaster.VFX_ATLAS type = EmitterMaster.getAtlasType(path);
        if (type == EmitterMaster.VFX_ATLAS.SPELL) {
            spell = true;
        }
        return EmitterMaster.getAtlas(type);
    }

    public void offset(String value, String offset) {
        offset(Float.parseFloat(offset),
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
            if (e instanceof Emitter) {
                Emitter emitter = (Emitter) e;
                emitter.offsetAngle(offset);
            }

        }
    }


    public void set(String valName, String value) {
        for (ParticleEmitter e : getEmitters()) {
            Emitter emitter = (Emitter) e;
            emitter.set(valName, value.toLowerCase());
        }
    }

    private boolean checkSprite(FileHandle effectFile) {
        //        if (EmitterActor.spriteEmitterTest) {
        //            return true;
        //        }
        String imgPath = EmitterPresetMaster.getInstance().getImagePath(effectFile.path());
        //        if (imgPath.contains("sprites")){
        //            main.system.auxiliary.log.LogMaster.log(1,effectFile.path()+" is a SPRITES!.. " );
        //            return true;
        //        }
        if (TEST_MODE)
            LogMaster.log(1, effectFile.path() + " created with imgPath " + imgPath);
        return false;
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
        if (!effectFile.exists()) {
            broken.add(effectFile.path());
            LogMaster.log(0, "no such emitter preset: " + effectFile.path());
            return;
        }
        InputStream input = effectFile.read();
        getEmitters().clear();
        BufferedReader reader;

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

    public void loadEmitterImages(TextureAtlas atlas, String atlasPrefix) {
        for (int i = 0, n = getEmitters().size; i < n; i++) {
            ParticleEmitter emitter = getEmitters().get(i);
            if (emitter.getImagePaths().size == 0) continue;
            Array<Sprite> sprites = new Array<>();
            for (String imagePath : emitter.getImagePaths()) {
                String imageName = new File(imagePath.replace('\\', '/')).getName();
                int lastDotIndex = imageName.lastIndexOf('.');
                if (lastDotIndex != -1) imageName = imageName.substring(0, lastDotIndex);
                if (atlasPrefix != null) imageName = atlasPrefix + imageName;
                Sprite sprite = atlas.createSprite(imageName);
                if (sprite == null)
                    sprite = atlas.createSprite(StringMaster.cropFormat(imagePath));

                if (sprite == null)
                    throw new IllegalArgumentException("SpriteSheet missing image: " + imageName);
                sprites.add(sprite);
            }
            emitter.setSprites(sprites);
        }
    }

    public void setPool(VfxPool pool) {
        this.pool = pool;
    }

    public void free() {
        if (pool == null) {
            // main.system.auxiliary.log.LogMaster.log(1,"No pool for vfx: " +this.path);
        } else
            pool.free(this);
    }
}
