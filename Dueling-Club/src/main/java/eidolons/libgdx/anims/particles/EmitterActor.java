package eidolons.libgdx.anims.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import main.content.CONTENT_CONSTS2.SFX;
import main.game.bf.Coordinates;
import eidolons.libgdx.bf.SuperActor;

/**
 * Created by JustMe on 1/10/2017.
 */
public class EmitterActor extends SuperActor implements ParticleInterface {

    static public boolean spriteEmitterTest = false;
    private final int defaultCapacity = 12;
    private final int defaultMaxCapacity = 24;
    public String path;
    protected ParticleEffect effect;
    protected ParticleEffectPool pool;
    protected SFX sfx;
    boolean flipX;
    boolean flipY;
    private Sprite sprite;
    private boolean attached = true;
    private boolean generated;
    private Coordinates target;
    private boolean test;
    private float speed = 1;

    public EmitterActor(SFX fx) {
        this(fx.path);
        this.sfx = fx;
    }

    public EmitterActor(String path, boolean test) { //TODO refactor!
        this(path);
        this.test = test;
    }

    public EmitterActor(String path) {
//        path =PathFinder.getSfxPath() + "templates\\sprite test";
        this.path = path;
        effect = EmitterPools.getEffect(path);
    }

    @Override
    public String toString() {
        if (path != null)
            return "emitter: " + path;
        if (sfx != null)
            return "emitter: " + sfx.path;
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EmitterActor) {
            EmitterActor e = ((EmitterActor) obj);
            if (e.getPath().equals(getPath()) || e.getEffect().equals(getEffect())) {
                if (getX() == e.getX())
                    if (getY() == e.getY())
                        return true;
            }

        }
        return super.equals(obj);
    }

    @Override
    public boolean isIgnored() {
        return super.isIgnored();
    }

    public void act(float delta) {
        super.act(delta);
        effect.setPosition(getX(), getY());
//        effect.update(delta); TODO now drawing with alpha!

    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
    }

    public void updatePosition(float x, float y) {
        setPosition(x, y);
    }

    @Override
    public SFX getTemplate() {
        return sfx;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        effect.setPosition(getX(), getY());
//        sprite = effect.getEmitters().first().getSprite();
//        sprite.setRotation(new Random().nextInt(360));
//        if (speed!=null )
        float delta = Gdx.graphics.getDeltaTime() * speed;
        effect.draw(batch, delta);

    }

    @Override
    public ParticleEffect getEffect() {
        return effect;
    }

    @Override
    public boolean isContinuous() {
        return false;
    }

    @Override
    public boolean isRunning() {
        return false;
    }


    @Override
    public void start() {
        effect.start();
    }

    public SFX getSfx() {
        return sfx;
    }

    public void setSfx(SFX sfx) {
        this.sfx = sfx;
    }

    public boolean isAttached() {
        return attached;
    }

    public void setAttached(boolean attached) {
        this.attached = attached;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public Coordinates getTarget() {
        return target;
    }

    public void setTarget(Coordinates target) {
        this.target = target;
    }

    public void reset() {
        getEffect().reset();
    }

    public String getPath() {
        return path;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
