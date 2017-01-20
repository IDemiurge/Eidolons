package main.libgdx.anims;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
public class Anim  extends Actor {
    protected Entity active;
    protected Vector2 origin;
    protected Vector2 destination;
    protected Vector2 defaultPosition;
    protected List<ParticleEmitter> emitterList;
    protected int lightEmission; // its own lightmap?
    protected Color color;
    protected List<SpriteAnimation> sprites;
    protected Supplier<Texture> textureSupplier;
    protected float stateTime = 0;
    protected float duration;
    protected float offsetX = 0;
    protected float offsetY = 0;
    protected AnimData data;
    protected ANIM_MOD[] mods;
    protected ANIM_PART part;
    private Float speedX;
    private Float speedY;
    private int loops;


    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        
    }

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

    protected void initDuration() {

        duration = 2;
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
            main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG,this+" finished; duration = " +duration);
            dispose();
            return false;
        }
        if (currentFrame != null){
            setWidth(currentFrame.getWidth());
        setHeight(currentFrame.getHeight());
        }
        updatePosition();
//        batch.begin();
        if (isDrawTexture())
            if (currentFrame != null)
            batch.draw(currentFrame, getX(), getY());

        sprites.forEach(s -> s.draw(batch));
        emitterList.forEach(e -> {
            e.act(delta);
            e.draw(batch, 1f);
        });
//        AnimMaster.getDrawer().draw(this, batch);
//        batch.end();
        return true;
    }

    protected boolean isDrawTexture() {
        return true;
    }

    public void start() {
        initPosition();
        sprites.forEach(s -> s.setX(getX()));
        sprites.forEach(s -> s.setY(getY()));
        sprites.forEach(s -> s.setOffsetX(0));
        sprites.forEach(s -> s.setOffsetY(0));
        sprites.forEach(s -> s.setLoops(loops));
        addLight();
        addEmitters();

    }

    public Float getSpeedX() {
        return speedX;
    }

    public Float getSpeedY() {
        return speedY;
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
        setY(defaultPosition.y); main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG,
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

if (getActions().size==0){
          setX(defaultPosition.x + offsetX);
         setY(defaultPosition.y + offsetY);
}
        sprites.forEach(s -> {
            s.setOffsetX(offsetX);
            s.setOffsetY(offsetY);
        });

        emitterList.forEach(e ->
         e.updatePosition(getX(), getY()));

        if (getActions().size==0){
       setX(origin.x + getWidth() / 2);
       setY(origin.y - getHeight() / 2);}
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

}
