package main.libgdx.anims;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import main.entity.Entity;
import main.entity.Ref;
import main.libgdx.GameScreen;
import main.libgdx.anims.ANIM_MODS.ANIM_MOD;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.particles.ParticleEmitter;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.libgdx.texture.TextureManager;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/9/2017.
 */
public class Anim extends Group {
    Vector2 origin;
    Vector2 destination;
    Vector2 position;
    List<ParticleEmitter> emitterList;
    int lightEmission; // its own lightmap?
    Color color;
    List<SpriteAnimation> sprites;
    Supplier<Texture> textureSupplier;
    float stateTime = 0;
    float duration;
    float offsetX;
    float offsetY;
    private AnimData data;
    private ANIM_MOD[] mods;
    private Ref ref;
    private ANIM_PART part;


    public Anim(Entity active, AnimData params) {
        ref = active.getRef();
        data = params;
        textureSupplier = () -> TextureManager.getOrCreate(active.getImagePath());
        //TODO colorize, apply alpha, rotate, warp, ... based on stateTime

        initPosition();

        duration = 3;
//        duration= params.getIntValue(ANIM_VALUES.DURATION);
    }

    public boolean draw(Batch batch) {
//switch(template){
//} applyTemplate()
        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;
        Texture currentFrame = textureSupplier.get();
        if (currentFrame == null || stateTime >= duration) {
            dispose();
            return false;
        }
        updatePosition();
//        batch.begin();
        batch.draw(currentFrame, position.x, position.y);

        sprites.forEach(s -> s.draw(batch));
        emitterList.forEach(e -> {
            e.act(delta);
            e.draw(batch, 1f);
        });
//        AnimMaster.getDrawer().draw(this, batch);
//        batch.end();
        return true;
    }

    public void start() {
        sprites.forEach(s -> s.setX(position.x));
        sprites.forEach(s -> s.setY(position.y));
        sprites.forEach(s -> s.setOffsetX(0));
        sprites.forEach(s -> s.setOffsetY(0));
        addLight();
        addEmitters();
    }

    private void addLight() {
    }

    private void addEmitters() {
        emitterList.forEach(e -> {
            e.start();
        });
    }

    private void dispose() {
    }

    public void initPosition() {
        origin = GameScreen.getInstance().getGridPanel()
         .getVectorForCoordinateWithOffset(ref.getSourceObj().getCoordinates());
        if (ref.getTargetObj()==null )
            destination = new Vector2(origin);
      else   destination = GameScreen.getInstance().getGridPanel()
         .getVectorForCoordinateWithOffset(ref.getTargetObj().getCoordinates());

        position = getDefaultPosition();
    }

    private Vector2 getDefaultPosition() {
        if (part!=null )
        switch (part) {
            case IMPACT:
            case AFTEREFFECT:
                return new Vector2(destination);
        }
        return new Vector2(origin);
    }

    public void updatePosition() {
        if (part!=null )
        switch (part) {
            case MAIN:
                offsetX = (destination.x - origin.x) * stateTime / duration;
                offsetY = (destination.y - origin.y) * stateTime / duration;
                break;
        }
        position.set(position.x + offsetX, position.y + offsetY);
        sprites.forEach(s -> {
            s.setOffsetX(offsetX);
            s.setOffsetY(offsetY);
        });

        emitterList.forEach(e ->
         e.setPosition(position.x, position.y));
    }

    public void setEmitterList(List<ParticleEmitter> emitterList) {
        this.emitterList = emitterList;
    }

    public void setLightEmission(int lightEmission) {
        this.lightEmission = lightEmission;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    public void setTextureSupplier(Supplier<Texture> textureSupplier) {
        this.textureSupplier = textureSupplier;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void setMods(ANIM_MOD[] mods) {
        this.mods = mods;
    }

    public void setSprites(List<SpriteAnimation> sprites) {
        this.sprites = sprites;
    }

    public ANIM_PART getPart() {
        return part;
    }

    public void setPart(ANIM_PART part) {
        this.part = part;
    }
}
