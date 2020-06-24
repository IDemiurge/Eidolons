package eidolons.libgdx.anims;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.libgdx.anims.ANIM_MODS.ANIM_MOD;
import eidolons.libgdx.anims.ANIM_MODS.CONTINUOUS_ANIM_MODS;
import eidolons.libgdx.anims.ANIM_MODS.OBJ_ANIMS;
import eidolons.libgdx.anims.AnimData.ANIM_VALUES;
import eidolons.libgdx.anims.AnimEnums.ANIM_PART;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.anims.text.FloatingText;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.particles.EmitterPools;
import eidolons.libgdx.particles.spell.SpellMultiplicator;
import eidolons.libgdx.particles.spell.SpellVfx;
import eidolons.libgdx.particles.spell.SpellVfxPool;
import eidolons.libgdx.texture.TextureCache;
import main.entity.Entity;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.EventCallback;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.images.ImageManager;
import main.system.launch.Flags;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static main.system.auxiliary.log.LogMaster.ANIM_DEBUG;
import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 1/9/2017.
 */
public class Anim extends Group implements Animation {
    public static final float DEFAULT_ANIM_DURATION = 2;
    public static final float DEFAULT_MAX_ANIM_DURATION = 6;
    protected Entity active;
    protected Vector2 origin;
    protected Vector2 destination;
    protected Vector2 defaultPosition;
    protected List<SpellVfx> emitterList;
    protected List<SpellVfx> emitterCache = new ArrayList<>(); //TODO not the best practice!
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
    protected float pixelsPerSecond;


    protected int cycles;
    protected float lifecycle; //0 to 1f
    protected float lifecycleDuration;
    protected Float frameDuration;
    protected float alpha = 1f;
    protected float delay;
    protected Coordinates forcedDestination;
    protected Texture texture;
    protected boolean running;
    protected Ref ref;
    EventCallback onDone;
    EventCallbackParam callbackParam;
    protected boolean emittersWaitingDone;
    protected List<FloatingText> floatingText = new ArrayList<>();
    protected AnimMaster master;
    protected CompositeAnim composite;
    protected boolean done;
    protected Vector2 offsetOrigin;
    protected Vector2 offsetDestination;
    protected CompositeAnim parentAnim;
    protected boolean completingVfx;
    protected float speedMod = 1;

    public Anim(Entity active, AnimData params) {
        this(active, params, null);
    }

    public Anim(Entity active, AnimData params, ANIM_PART part) {
        data = params;
        this.part = part;
        this.active = active;
        if (active == null) {
            ref = new Ref();
        } else
            this.ref = active.getRef().getCopy();
        textureSupplier = () -> getTexture();
        reset();
        if (data.getIntValue(ANIM_VALUES.FRAME_DURATION) > 0) {
            frameDuration = data.getIntValue(ANIM_VALUES.FRAME_DURATION) / 100f;
        }
        //        duration= params.getIntValue(ANIM_VALUES.DURATION);
        initEmitters();
    }

    @Override
    public void start(Ref ref) {
        setRef(ref);
        start();
    }

    @Override
    public void start() {
        emittersWaitingDone = false;
        initPosition();
        initDuration();
        initSpeed();
        resetEmitters();
        resetSprites();

        getSprites().forEach(s -> {
            s.setX(getX());
            s.setY(getY());
            s.setOffsetX(0);
            s.setOffsetY(0);
            s.setLoops(loops);
            s.reset();
            s.start();
        });

        if (frameDuration != null) {
            sprites.forEach(s -> s.setFrameDuration(frameDuration));
        }


        SpellMultiplicator.checkMultiplication(this);

        addLight();
        startEmitters();


        running = true;
        GuiEventManager.trigger(GuiEventType.ANIMATION_STARTED, this);
        playSound();
    }

    public void adjustPosForZoom(int zoom) {
        setPosition(getX() * zoom / 100, getY() * zoom / 100);
        // maybe animMaster must be part of the viewport?
        //creating anim at wrong coordinates vs updating

        // not just zoom - coords are often 'wrong': ranged weapon, some melee atks


    }

    public boolean tryDraw(Batch batch) {
        if (Flags.isFootageMode())
            return false;
        return draw(batch);
    }

    @Override
    public boolean draw(Batch batch) {
        float delta = Gdx.graphics.getDeltaTime();
        time += delta;
        if (time < 0) {
            return true; //delay
        }
        checkAddFloatingText();
        if (lifecycleDuration != 0) {
            cycles = (int) (time / lifecycleDuration);
            lifecycle = time % lifecycleDuration / lifecycleDuration;
        }
        if (duration >= 0 || isContinuous()) //|| finished //  lifecycle duration for continuous?
        {
            if (checkFinished()) {
                boolean waitForVfx = false;
                if (!emitterList.isEmpty()) {
                    if (AnimMaster.isSmoothStop(this)) {
                        if (checkVfxCompletion())
                            waitForVfx = true;
                    }
                }
                if (!waitForVfx) {
                    log(ANIM_DEBUG, this + " finished; duration = " + duration);
                    finished();
                    dispose();
                    return false;
                }

            }
        }

        updatePosition(delta);

//        sprites.forEach(s -> {
            //            s.setFlipX(flipX);
            //            s.setFlipX(flipY);
//        });
        applyAnimMods();
        if (isDrawTexture() && getActions().size == 0) {
            draw(batch, alpha);
        }

        sprites.forEach(s -> {
            s.draw(batch);
        });
        batch.setColor(new Color(1, 1, 1, 1));
        for (int i = 0, emitterListSize = emitterList.size(); i < emitterListSize; i++) {
            SpellVfx spellVfx = emitterList.get(i);
            spellVfx.setFlipX(flipX);
            spellVfx.setFlipX(flipY);
            spellVfx.act(delta);
            spellVfx.draw(batch, 1f);
        }
//        emitterList.forEach(e -> {
//            e.draw(batch, 1f);
            //            main.system.auxiliary.log.LogMaster.log(LogMaster.ANIM_DEBUG,
            //             e.getName() +
            //              " drawn at x " + e.getX() + " y " + e.getY());
            //            e.getEffect().getEmitters().forEach(em -> {
            //                main.system.auxiliary.log.LogMaster.log(LogMaster.ANIM_DEBUG,
            //                 em.getName() +
            //                  " emitter at at x " + em.getX() + " y " + em.getY()
            //                  + " ; activecount == " + em.getActiveCount()
            //                );
            //            });
//        });
        return true;
    }

    private boolean checkVfxCompletion() {
        if (!completingVfx) {
            waitForVfx();
            completingVfx = true;
        }
        for (int i = 0, emitterListSize = emitterList.size(); i < emitterListSize; i++) {
            EmitterActor e = emitterList.get(i);
            e.getEffect().allowCompletion();
        }
        for (int i = 0, emitterListSize = emitterList.size(); i < emitterListSize; i++) {
            EmitterActor e = emitterList.get(i);
            if (!e.isComplete()) {
                return true;
            }
        }
        return false;
    }

    protected void waitForVfx() {
        emitterList.forEach(e -> e.getEffect().allowCompletion());
    }

    protected boolean checkFinished() {

        if (isContinuous())
            return isDone();
        return time >= getDuration();
    }

    protected boolean isContinuous() {
        return false;
    }

    protected float getTimeToFinish() {
        float time = 0;
        for (EmitterActor e : emitterList) {
            for (ParticleEmitter emitter : e.getEffect().getEmitters()) {
                float timeLeft = emitter.getDuration().getLowMax() / 1000 *
                        Math.max(0, emitter.getPercentComplete());
                if (timeLeft > time) {
                    time = timeLeft;
                }
            }
        }
        float gracePeriod = 0.25f;
        time = time + time * gracePeriod;
        log(ANIM_DEBUG, this + " adding TimeToFinish: " + time);
        return time;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (getX() == 0 && getY() == 0) {
            getX();
        }
        super.draw(batch, parentAlpha);

        if (!isDrawTexture()) {
            return;
        }
//        Texture currentFrame = textureSupplier.getVar();
//        if (currentFrame != null) {
//            setWidth(currentFrame.getWidth());
//            setHeight(currentFrame.getHeight());
//        }
        Texture texture = getTexture();

        if (texture == null) {
            return;
        }
        if (isDone() || !isRunning()) {
            return;
        }
        Color color = batch.getColor();
        batch.setColor(new Color(1, 1, 1, 1));
        float w = Math.min(64, this.getWidth());
        float h = Math.min(64, this.getHeight());
        batch.draw((texture), this.getX(), getY(), this.getOriginX(),
                this.getOriginY(), w, h, this.getScaleX(), this.getScaleY(),
                this.getRotation(), 0, 0,
                texture.getWidth(), texture.getHeight(), flipX, flipY);

        batch.setColor(color);
    }

    @Override
    public void reset() {
        time = 0;
        //        time = -delay; TODO
        setOffsetX(0);
        setOffsetY(0);
        alpha = 1f;
        initDuration();
        initSpeed();
        floatingText.clear();
        resetEmitters();
    }

    protected void resetSprites() {
        //TODO
        if (sprites == null) {
            sprites = new ArrayList<>();
        } else {
            sprites.clear();
        }
        for (String s : ContainerUtils.openContainer(data.getValue(ANIM_VALUES.SPRITES))) {
            SpriteAnimation sprite = SpriteAnimationFactory.getSpriteAnimation(s);
            sprite.setFrameDuration(getDuration() / sprite.getFrameNumber());
            sprite.setSpeed(speedMod);
            sprites.add(sprite);
        }
    }

    protected void initEmitters() {
        if (emitterList == null || emitterList .isEmpty()) {
            if (data.getValue(ANIM_VALUES.PARTICLE_EFFECTS) != null) {
                setEmitterList(SpellVfxPool.getEmitters(data.getValue(ANIM_VALUES.PARTICLE_EFFECTS)));
            }
        }
    }

    protected void resetEmitters() {

        getEmitterList().forEach(e ->
                {
                    if (e.isGenerated()) {
                        e.getEffect().dispose();
                    }
                }
        );
        if (ListMaster.isNotEmpty(emitterCache))
            setEmitterList(new ArrayList<>(emitterCache));
        else {
            if (!ListMaster.isNotEmpty(emitterList))
                setEmitterList(new ArrayList<>());
        }
        //        emitterCache.clear();
        //        emitterCache.addAll(emitterList);//= new ArrayList<>(emitterList);
        emitterList.forEach(e -> {
            if (!e.isGenerated()) {
                if (e.isAttached()) {
                    e.setTarget(getDestinationCoordinates());
                }
            }
        });

        getEmitterList().forEach(e -> {
            e.reset();
            if (isContinuous(part)) {
            e.getEffect().getEmitters().forEach(t ->
                    t.setContinuous(true));
            }
        });
    }

    private boolean isContinuous(ANIM_PART part) {
        return part== AnimEnums.ANIM_PART.MISSILE;
    }

    protected void initDuration() {

        setDuration(DEFAULT_ANIM_DURATION);
        if (part != null) {
            setDuration(part.getDefaultDuration());
        }
        //        duration*= AnimMaster.getOptions().getAnimationSpeed()
    }

    protected void initFlip() {
        flipX = false;
        flipY = false;
        if (getOriginCoordinates().x < getDestinationCoordinates().x) {
            flipX = true;
        }
        if (getOriginCoordinates().y > getDestinationCoordinates().y) {
            flipY = true;
        }
    }

    protected void initSpeed() {

        if (destination == null) {
            return;
        }
        if (origin == null) {
            return;
        }
        if (origin.equals(destination)) {
            return;
        }

        float pixelsPerSecond = getPixelsPerSecond();
        if (pixelsPerSecond == 0) {
            return;
        }

        double distance = calcDistance();
        if (distance == 0) {
            return;
        }
        if (!isSpeedSupported()) {
            return;
        }
        setDuration(((float) distance / pixelsPerSecond));
        initSpeedForDuration(duration);

    }

    protected double calcDistance() {
        float x = destination.x - origin.x;
        float y = destination.y - origin.y;
        return Math.sqrt(x * x + y * y);
    }

    protected void initSpeedForDuration(float duration) {
        float x = destination.x - origin.x;
        float y = destination.y - origin.y;
        speedX = x / duration;
        speedY = y / duration;
    }
    //        setDuration(getOrigin().dst(getDestination())/new Vector2(getSpeedX(), getSpeedY()).len());

    protected boolean isSpeedSupported() {
        return part == AnimEnums.ANIM_PART.MISSILE;
    }

    public String getTexturePath() {
        if (active == null || Cinematics.ON)
            return "";
        return active.getImagePath();
    }

    protected Texture getTexture() {
        if (texture == null) {
            if (ImageManager.isImage(getTexturePath())) {
                texture = TextureCache.getOrCreateNonEmpty(getTexturePath());
            } else {
                texture = TextureCache.getOrCreateNonEmpty(getDefaultTexturePath());
            }
        }
        return texture;

    }

    protected String getDefaultTexturePath() {
        return
                VISUALS.QUESTION.getImgPath();
    }

    @Override
    public void finished() {
        running = false;
        if (onDone != null) {
            onDone.call(callbackParam);
        }
        GuiEventManager.trigger(GuiEventType.ANIMATION_DONE, this);
    }


    protected void applyAnimMods() {
        if (mods != null) {
            Arrays.stream(mods).forEach((ANIM_MOD mod) -> {
                if (mod instanceof CONTINUOUS_ANIM_MODS) {
                    applyContinuousAnimMod((CONTINUOUS_ANIM_MODS) mod);
                }
                if (mod instanceof OBJ_ANIMS) {
                    applyObjAnimMod((OBJ_ANIMS) mod);
                }
            });
        }


    }

    protected void applyObjAnimMod(OBJ_ANIMS mod) {
        switch (mod) {
            case FADE_IN:
                alpha = 0.1f + time / duration;
        }
    }

    protected void applyContinuousAnimMod(CONTINUOUS_ANIM_MODS mod) {
        switch (mod) {
            case PENDULUM_ALPHA:
                sprites.forEach(s -> {
                    if (cycles % 2 == 0) {
                        s.setAlpha(1f - lifecycle);
                    } else {
                        s.setAlpha(lifecycle);
                    }

                    //                           time%lifecycle/lifecycle
                });
                break;
        }
    }

    protected boolean isDrawTexture() {
        return !Cinematics.ON;
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

    protected void startEmitters() {
        emitterList.forEach(e -> {
            e.start();
            addActor(e);
        });
    }

    protected void dispose() {

        texture = null;

        emitterList.forEach(e -> {
            EmitterPools.freeActor(e);
            e.remove();
            e.getEffect().dispose();

        });
        sprites.forEach(s -> s.dispose());
    }

    public void initPosition() {
        origin = GridMaster
                .getCenteredPos(getOriginCoordinates());
        if (getOffsetOrigin() != null)
            origin.add(getOffsetOrigin());
        //        main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG,
        //         this + " origin: " + origin);

        destination = GridMaster
                .getCenteredPos(getDestinationCoordinates());
        if (getOffsetDestination() != null)
            destination.add(getOffsetDestination());

        //        main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG,
        //         this + " destination: " + destination);


        defaultPosition = getDefaultPosition();
        setX(defaultPosition.x);
        setY(defaultPosition.y);
        //        main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG,
        //         this + " defaultPosition: " + defaultPosition);
    }

    public Coordinates getOriginCoordinates() {
        return getRef().getSourceObj().getCoordinates();

    }

    public void setOrigin(Vector2 origin) {
        this.origin = origin;
    }

    public void setOrigin(Coordinates origin) {
        this.origin = GridMaster
                .getCenteredPos(origin);
    }
    public Coordinates getDestinationCoordinates() {
        if (forcedDestination != null) {
            return forcedDestination;
        }
        if (getRef().getTargetObj() == null) {
            return getRef().getSourceObj().getCoordinates();
        }
        return getRef().getTargetObj().getCoordinates();
    }

    public void setForcedDestination(Coordinates forcedDestination) {
        this.forcedDestination = forcedDestination;
    }

    public void setForcedDestinationForAll(Coordinates forcedDestination) {
        this.forcedDestination = forcedDestination;
        CompositeAnim parent = AnimMaster.getInstance().getParentAnim(getRef());
        if (parent != null) {
            AnimMaster.getInstance().getParentAnim(getRef()).setForcedDestination(forcedDestination);
        }
    }

    protected Vector2 getDefaultPosition() {
        if (part != null) {
            switch (part) {
                case IMPACT:
                case AFTEREFFECT:
                    return new Vector2(destination);
            }
        }
        return new Vector2(origin);
    }


    public void updatePosition(float delta) {
        if (part != null) {
            switch (part) {
                case MISSILE:
                    if (speedX != null) {
                        setOffsetX(getOffsetX() + speedX * delta);
                    } else {
                        setOffsetX((destination.x - origin.x) * time / duration);
                    }
                    if (speedY != null) {
                        setOffsetY(getOffsetY() + speedY * delta);
                    } else {
                        setOffsetY((destination.y - origin.y) * time / duration);
                    }
                    break;
            }
        }
        if (defaultPosition != null)
            if (getActions().size == 0) {
                setX(defaultPosition.x + getOffsetX());
                setY(defaultPosition.y + getOffsetY());
            }
        if (sprites != null)
        sprites.forEach(s -> {
            if (s.isAttached()) {
                if (getActions().size == 0) {
                    s.setOffsetX(getOffsetX());
                    s.setOffsetY(getOffsetY());
                }
            }
            s.setRotation(getRotation());
        });

        emitterList.forEach(e -> {
            if (e.isAttached()) {
                e.updatePosition(getX(), getY());
            }

        });

        if (getActions().size == 0) { //TODO move it somewhere!
            if (origin == null) {
                return;
            }
            setX(origin.x + getWidth() / 2);
            setY(origin.y - getHeight() / 2);
        }
    }

    @Override
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

    public List<SpellVfx> getEmitterList() {
        if (emitterList == null) {
            setEmitterList(new ArrayList<>());
        }
        return emitterList;
    }

    public void setEmitterList(List<SpellVfx> emitterList) {
        this.emitterList = emitterList;
        emitterCache = new ArrayList<>(emitterList);
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

    public List<Pair<GuiEventType, EventCallbackParam>> getEventsOnFinish() {
        return null;
    }

    public List<Pair<GuiEventType, EventCallbackParam>> getEventsOnStart() {
        return null;
    }

    public List<SpriteAnimation> getSprites() {
        if (sprites == null) {
            setSprites(new ArrayList<>());
        }
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

    @Override
    public float getTime() {
        return time;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        float mod = 1 / (AnimMaster.getAnimationSpeedFactor());
        if (mod > 0)
            this.duration = duration * mod;
        else {
            this.duration = duration;
        }
    }

    public float getPixelsPerSecond() {
        if (data.getIntValue(ANIM_VALUES.MISSILE_SPEED) != 0) {
            pixelsPerSecond = data.getIntValue(ANIM_VALUES.MISSILE_SPEED);
        } else
            pixelsPerSecond = getDefaultSpeed();
        float mod = new Float(AnimMaster.getAnimationSpeedFactor());
        if (mod > 0)
            return pixelsPerSecond * mod;
        return pixelsPerSecond;
    }

    protected float getDefaultSpeed() {
        return 100;
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
        if (ref == null)
            return active.getRef();
        return ref;
        //        if (active == null) {
        //            return (DC_Game.game.getManager().getActiveObj().getRef());
        //        }
        //        return active.getRef();
    }

    public void setRef(Ref ref) {
        this.ref = Ref.getCopy(ref);
        //        main.system.auxiliary.log.LogMaster.log(LogMaster.ANIM_DEBUG, this + " started with ref: " + ref);
        if (ref.getTargetObj() == null) {
            log(LogMaster.ANIM_DEBUG, this + " HAS NULL TARGET!");
            if (ref.getActive() != null) {
                ref.setTarget(ref.getActive().getRef().getTarget());
                if (ref.getTargetObj() != null) {
                    log(LogMaster.ANIM_DEBUG, ref.getActive() + " HAD TARGET! " +
                            ref.getTargetObj());
                }
            }
        }
    }

    public float getDelay() {
        return delay;
    }

    public void setDelay(float delay) {
        this.delay = delay;
    }

    @Override
    public void onDone(EventCallback callback, EventCallbackParam param) {
        this.onDone = callback;
        callbackParam = param;
    }

    public void playSound() {
    }

    @Override
    public boolean isRunning() {
        return running;
    }


    public boolean isEmittersWaitingDone() {
        return emittersWaitingDone;
    }

    public void checkAddFloatingText() {
        getFloatingText().forEach(floatingText1 -> {
            if (time >= floatingText1.getDelay()) {
                Vector2 floatTextPos = //localToSctageCoordinates
                        (defaultPosition);
                floatingText1.setX(floatTextPos.x);
                floatingText1.setY(floatTextPos.y);
                GuiEventManager.trigger(GuiEventType.ADD_FLOATING_TEXT, floatingText1);
            }
        });
    }

    public void addFloatingText(FloatingText floatingText) {
        getFloatingText().add(floatingText);
        if (floatingText.getDelay() == 0) {
            floatingText.setDelay(getFloatingText().size() - 1);
        }

    }

    public List<FloatingText> getFloatingText() {
        if (floatingText == null) {
            floatingText = new ArrayList<>();
        }
        return floatingText;
    }

    public AnimMaster getMaster() {
        return master;
    }

    public void setMaster(AnimMaster master) {
        this.master = master;
    }

    public CompositeAnim getComposite() {
        return composite;
    }

    public void setComposite(CompositeAnim composite) {
        this.composite = composite;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Vector2 getOffsetOrigin() {
        return offsetOrigin;
    }

    public void setOffsetOrigin(Vector2 offsetOrigin) {
        this.offsetOrigin = offsetOrigin;
    }

    public Vector2 getOffsetDestination() {
        return offsetDestination;
    }

    public void setOffsetDestination(Vector2 offsetDestination) {
        this.offsetDestination = offsetDestination;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        //        main.system.auxiliary.log.LogMaster.log(LogMaster.ANIM_DEBUG, this + " setOffsetX " +
        //         " from " + this.offsetX +
        //         " to " + offsetX);
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public CompositeAnim getParentAnim() {
        return parentAnim;
    }

    public void setParentAnim(CompositeAnim parentAnim) {
        this.parentAnim = parentAnim;
    }

    public void setSpeedMod(float speedMod) {
        this.speedMod = speedMod;
    }

    public float getSpeedMod() {
        return speedMod;
    }


    public void setOnDone(EventCallback onDone) {
        this.onDone = onDone;
    }

    public EventCallback getOnDone() {
        return onDone;
    }

    public EventCallbackParam getCallbackParam() {
        return callbackParam;
    }
}
