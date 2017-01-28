package main.libgdx.anims;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.entity.Entity;
import main.entity.Ref;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.libgdx.GameScreen;
import main.libgdx.anims.ANIM_MODS.ANIM_MOD;
import main.libgdx.anims.ANIM_MODS.CONTINUOUS_ANIM_MODS;
import main.libgdx.anims.ANIM_MODS.OBJ_ANIMS;
import main.libgdx.anims.AnimData.ANIM_VALUES;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.particles.EmitterActor;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.libgdx.texture.TextureManager;
import main.system.GuiEventType;
import main.system.auxiliary.LogMaster;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/9/2017.
 */
public class Anim extends Group {
    protected Entity active;
    protected Vector2 origin;
    protected Vector2 destination;
    protected Vector2 defaultPosition;
    protected List<EmitterActor> emitterList;
    protected List<SpriteAnimation> sprites;
    protected int lightEmission; // its own lightmap?
    protected Color color;
    protected Supplier<Texture> textureSupplier;
    protected float time = 0;
    protected float duration = 0;
    protected float offsetX = 0;
    protected float offsetY = 0;
    protected AnimData data;
    protected ANIM_MOD[] mods;
    protected ANIM_PART part;
    protected boolean flipX;
    protected boolean flipY;
    protected int initialAngle;
    protected Float speedX;
    protected Float speedY;
    protected int loops;
    protected int pixelsPerSecond = 500;
    protected int cycles;
    protected float lifecycle; //0 to 1f
    protected float lifecycleDuration;
    protected Float frameDuration;
    protected float alpha;


    public Anim(Entity active, AnimData params) {
        data = params;
        this.active = active;
        textureSupplier = () -> getTexture();
        reset();
        if (data.getIntValue(ANIM_VALUES.FRAME_DURATION) > 0)
            frameDuration = data.getIntValue(ANIM_VALUES.FRAME_DURATION) / 100f;
//        duration= params.getIntValue(ANIM_VALUES.DURATION);
    }


    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);

    }

    public void reset() {
        time = 0;
        offsetX = 0;
        offsetY = 0;
        initDuration();
        initSpeed();

    }

    protected void initDuration() {

        duration = 2;
        if (part != null)
            duration = part.getDefaultDuration();
//        duration*= AnimMaster.getOptions().getAnimationSpeed()
    }

    protected void initFlip() {
        flipX = false;
        flipY = false;
        if (getOriginCoordinates().x < getDestinationCoordinates().x) flipX = true;
        if (getOriginCoordinates().y > getDestinationCoordinates().y) flipY = true;
    }

    protected void initSpeed() {
        if (!isSpeedSupported()) return;
        if (destination == null) return;
        if (origin == null) return;
        if (origin.equals(destination)) return;
        if (data.getIntValue(ANIM_VALUES.MISSILE_SPEED) != 0)
            pixelsPerSecond = data.getIntValue(ANIM_VALUES.MISSILE_SPEED);
        if (pixelsPerSecond == 0) return;
        float x = destination.x - origin.x;
        float y = destination.y - origin.y;

        double distance = Math.sqrt(x * x + y * y);
        if (distance == 0) return;
        duration = (float) distance / pixelsPerSecond;

        speedX = x / duration;
        speedY = y / duration;

    }

    protected boolean isSpeedSupported() {
        if (part == ANIM_PART.MAIN) return true;
        return false;
    }

    protected Texture getTexture() {
        return TextureManager.getOrCreate(active.getImagePath());
        //TODO scale, colorize, apply alpha, rotate, warp, ... based on time
        //for attack/turn anims?
    }

    public void finished() {
        //TODO
    }

    public boolean draw(Batch batch) {
//switch(template){
//}
        float delta = Gdx.graphics.getDeltaTime();
        time += delta;
        Texture currentFrame = textureSupplier.get();
        if (lifecycleDuration != 0) {
            cycles = (int) (time / lifecycleDuration);
            lifecycle = time % lifecycleDuration / lifecycleDuration;
        }
        if (duration > 0) //|| finished //  lifecycle duration for continuous?
            if (time >= duration) {
                main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG, this + " finished; duration = " + duration);
                dispose();
                return false;
            }
        if (currentFrame != null) {
            setWidth(currentFrame.getWidth());
            setHeight(currentFrame.getHeight());
        }
        updatePosition(delta);
        emitterList.forEach(e -> {
            e.setFlipX(flipX);
            e.setFlipX(flipY);
            e.act(delta);
        });
        sprites.forEach(s -> {
//            s.setFlipX(flipX);
//            s.setFlipX(flipY);
        });
        applyAnimMods();
        if (isDrawTexture())
            if (currentFrame != null) {
                clear();
                addActor(new Image(currentFrame));

                draw(batch, alpha);
//                batch.draw(currentFrame, this.getX(), getY(), this.getOriginX(),
//                 this.getOriginY(), this.getWidth(),
//                        this.getHeight(), this.getScaleX(), this.getScaleY(),
//                        initialAngle + this.getRotation(), 0, 0,
//                        currentFrame.getWidth(), currentFrame.getHeight(), flipX, flipY);
            }

        sprites.forEach(s -> {
            s.draw(batch);
        });
        emitterList.forEach(e -> {
            e.draw(batch, 1f);
        });
        return true;
    }

    protected void applyAnimMods() {
        if (mods != null)
            Arrays.stream(mods).forEach((ANIM_MOD mod) -> {
                if (mod instanceof CONTINUOUS_ANIM_MODS) {
                    applyContinuousAnimMod((CONTINUOUS_ANIM_MODS) mod);
                }
                if (mod instanceof OBJ_ANIMS) {
                    applyObjAnimMod((OBJ_ANIMS) mod);
                }
            });


    }

    private void applyObjAnimMod(OBJ_ANIMS mod) {
        switch (mod) {
            case FADE_IN:
                alpha = 0.1f + time / duration;
        }
    }

    private void applyContinuousAnimMod(CONTINUOUS_ANIM_MODS mod) {
        switch (mod) {
            case PENDULUM_ALPHA:
                sprites.forEach(s -> {
                    if (cycles % 2 == 0)
                        s.setAlpha(1f - lifecycle);
                    else
                        s.setAlpha(lifecycle);

//                           time%lifecycle/lifecycle
                });
                break;
        }
    }

    protected boolean isDrawTexture() {
        return true;
    }

    public void start() {
        initPosition();
        initDuration();
        initSpeed();
        if (emitterList != null)
            emitterList.removeIf(e -> e.isGenerated());
        emitterList.forEach(e -> {
            if (!e.isGenerated())
                if (e.isAttached())
                    e.setTarget(getDestinationCoordinates());
        });
        sprites.forEach(s -> s.setX(getX()));
        sprites.forEach(s -> s.setY(getY()));
        sprites.forEach(s -> s.setOffsetX(0));
        sprites.forEach(s -> s.setOffsetY(0));
        sprites.forEach(s -> s.setLoops(loops));
        sprites.forEach(s -> s.reset());
        if (frameDuration != null)
            sprites.forEach(s -> s.setFrameDuration(frameDuration));
        addLight();
        addEmitters();

    }

    public Float getSpeedX() {
        return speedX;
    }

    public void setSpeedX(Float speedX) {
        this.speedX = speedX;
    }

    public Float getSpeedY() {
        return speedY;
    }

    public void setSpeedY(Float speedY) {
        this.speedY = speedY;
    }

    public int getLoops() {
        return loops;
    }

    public void setLoops(int loops) {
        this.loops = loops;
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
        setX(defaultPosition.x);
        setY(defaultPosition.y);
        main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG,
         this + " defaultPosition: " + defaultPosition);
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

    public void updatePosition(float delta) {
        if (part != null)
            switch (part) {
                case MAIN:
                    if (speedX != null)
                        offsetX += speedX * delta;
                    else
                        offsetX = (destination.x - origin.x) * time / duration;
                    if (speedY != null)
                        offsetY += speedY * delta;
                    else
                        offsetY = (destination.y - origin.y) * time / duration;
                    break;
            }

        if (getActions().size == 0) {
            setX(defaultPosition.x + offsetX);
            setY(defaultPosition.y + offsetY);
        }
        sprites.forEach(s -> {
            s.setOffsetX(offsetX);
            s.setOffsetY(offsetY);
        });

        emitterList.forEach(e -> {
            if (e.isAttached())
                e.updatePosition(getX(), getY());

        });

        if (getActions().size == 0) {
            setX(origin.x + getWidth() / 2);
            setY(origin.y - getHeight() / 2);
        }
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
        return getClass().getSimpleName() + " " + part;
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

    public List<EmitterActor> getEmitterList() {
        return emitterList;
    }

    public void setEmitterList(List<EmitterActor> emitterList) {
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

    public float getTime() {
        return time;
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

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
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
        if (active == null)
            return (DC_Game.game.getManager().getActiveObj().getRef());

        return active.getRef();
    }


}
