package main.libgdx.anims;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import main.entity.Entity;
import main.entity.Ref;
import main.game.battlefield.Coordinates;
import main.libgdx.GameScreen;
import main.libgdx.anims.ANIM_MODS.ANIM_MOD;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.particles.ParticleEmitter;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.libgdx.texture.TextureManager;
import main.system.GuiEventType;
import main.system.auxiliary.LogMaster;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/9/2017.
 */
public class Anim   {
    protected Entity active;
    protected Vector2 origin;
    protected Vector2 destination;
    protected Vector2 defaultPosition;
    protected Vector2 position;
    protected List<ParticleEmitter> emitterList;
    protected int lightEmission; // its own lightmap?
    protected Color color;
    protected List<SpriteAnimation> sprites;
    protected Supplier<Texture> textureSupplier;
    protected float stateTime = 0;
    protected float duration;
    protected float offsetX = 0;
    protected float offsetY = 0;
    protected float height = 0;
    protected float width = 0;
    protected AnimData data;
    protected ANIM_MOD[] mods;
    protected ANIM_PART part;
    private Float speedX;
    private Float speedY;

    public Anim(Entity active, AnimData params) {
        data = params;
        this.active = active;
        textureSupplier = () -> getTexture();
        reset();

//        duration= params.getIntValue(ANIM_VALUES.DURATION);
    }

    public void reset() {
        stateTime = 0;
        offsetX = 0;
        offsetY = 0;
        initDuration();
    }

    private void initDuration() {
        duration = 3;
        if (part != null)
            duration = part.getDefaultDuration();
//        duration*= AnimMaster.getOptions().getAnimationSpeed()
    }

    protected Texture getTexture() {
        return TextureManager.getOrCreate(active.getImagePath());
        //TODO scale, colorize, apply alpha, rotate, warp, ... based on stateTime
        //for attack/turn anims?
    }

    public boolean draw(Batch batch) {
//switch(template){
//} applyTemplate()
        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;
        Texture currentFrame = textureSupplier.get();

        if (  stateTime >= duration) {
            dispose();
            return false;
        }
        if (currentFrame != null){
            setWidth(currentFrame.getWidth());
        setHeight(currentFrame.getHeight());
        }
        updatePosition();
//        batch.begin();
        if (currentFrame != null)
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
        initPosition();
        sprites.forEach(s -> s.setX(position.x));
        sprites.forEach(s -> s.setY(position.y));
        sprites.forEach(s -> s.setOffsetX(0));
        sprites.forEach(s -> s.setOffsetY(0));
        addLight();
        addEmitters();
    }

    protected void addLight() {
    }

    protected void addEmitters() {
        emitterList.forEach(e -> {
            e.start();
        });
    }

    protected void dispose() {
    }

    public void initPosition() {
        origin = GameScreen.getInstance().getGridPanel()
         .getVectorForCoordinateWithOffset(getOriginCoordinates());

        main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG,
         this + " origin: " + origin);

        destination = GameScreen.getInstance().getGridPanel()
         .getVectorForCoordinateWithOffset(getDestinationCoordinates());

        main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG,
         this + " destination: " + destination);


        defaultPosition = getDefaultPosition();
        position = new Vector2(defaultPosition);
    }

    protected Coordinates getOriginCoordinates() {

        return getRef().getSourceObj().getCoordinates();

    }

    protected Coordinates getDestinationCoordinates() {
        if (getRef().getTargetObj() == null)
            return getRef().getSourceObj().getCoordinates();
        return getRef().getTargetObj().getCoordinates();
    }

    protected Vector2 getDefaultPosition() {
        if (part != null)
            switch (part) {
                case IMPACT:
                case AFTEREFFECT:
                    return new Vector2(destination);
            }
        return new Vector2(origin);
    }

    public void updatePosition() {
        if (part != null)
            switch (part) {
                case MAIN:
                    if (speedX != null)
                        offsetX += speedX;
                    else
                        offsetX = (destination.x - origin.x) * stateTime / duration;
                    if (speedY != null)
                        offsetY += speedY;
                    else
                        offsetY = (destination.y - origin.y) * stateTime / duration;
                    break;
            }


        position.set(defaultPosition.x + offsetX, defaultPosition.y + offsetY);
        sprites.forEach(s -> {
            s.setOffsetX(offsetX);
            s.setOffsetY(offsetY);
        });

        emitterList.forEach(e ->
         e.updatePosition(position.x, position.y));

        position.set(origin.x + getWidth() / 2, origin.y - getHeight() / 2);
    }

    public ANIM_PART getPart() {
        return part;
    }

    public void setPart(ANIM_PART part) {
        this.part = part;
        initDuration();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+" "+part;
    }

    public Entity getActive() {
        return active;
    }

    public Vector2 getOrigin() {
        return origin;
    }

    public Vector2 getDestination() {
        return destination;
    }

    public Vector2 getPosition() {
        return position;
    }

    public List<ParticleEmitter> getEmitterList() {
        return emitterList;
    }

    public void setEmitterList(List<ParticleEmitter> emitterList) {
        this.emitterList = emitterList;
    }

    public int getLightEmission() {
        return lightEmission;
    }

    public void setLightEmission(int lightEmission) {
        this.lightEmission = lightEmission;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<GuiEventType> getEventsOnFinish() {
        return null;
    }

    public List<GuiEventType> getEventsOnStart() {
        return null;
    }

    public List<SpriteAnimation> getSprites() {
        return sprites;
    }

    public void setSprites(List<SpriteAnimation> sprites) {
        this.sprites = sprites;
    }

    public Supplier<Texture> getTextureSupplier() {
        return textureSupplier;
    }

    public void setTextureSupplier(Supplier<Texture> textureSupplier) {
        this.textureSupplier = textureSupplier;
    }

    public float getStateTime() {
        return stateTime;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public AnimData getData() {
        return data;
    }

    public ANIM_MOD[] getMods() {
        return mods;
    }

    public void setMods(ANIM_MOD[] mods) {
        this.mods = mods;
    }

    public Ref getRef() {
        return active.getRef();
    }

    public void setSpeedX(Float speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(Float speedY) {
        this.speedY = speedY;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
