package main.libgdx.anims.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.content.CONTENT_CONSTS2.SFX;
import main.game.battlefield.Coordinates;

/**
 * Created by JustMe on 1/10/2017.
 */
public class EmitterActor extends Actor implements ParticleInterface {

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
       effect= EmitterPools.getEffect(path);
    }


    public void act(float delta) {
        super.act(delta);
//        effect.setPosition(x, y);
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

        effect.draw(batch, Gdx.graphics.getDeltaTime());

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
}
