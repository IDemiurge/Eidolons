package gdx.general.anims;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import libgdx.anims.AnimData;
import libgdx.anims.sprite.SpriteAnimation;
import main.data.filesys.PathFinder;

public class SpriteAnim extends Actor {
    private final SpriteAnimation sprite;
    private Runnable onFinish;

    public SpriteAnim(String spritePath, Vector2 pos, ActionAnims.DUMMY_ANIM_TYPE type) {
        Array<TextureAtlas.AtlasRegion> regions = new TextureAtlas(PathFinder.getImagePath() + spritePath).getRegions();
        sprite = new SpriteAnimation(0.05f, false, regions);
        sprite.setPlayMode(Animation.PlayMode.NORMAL);
        sprite.setX(pos.x);
        sprite.setY(pos.y);
//        sprite.setFps(30);

    }

    @Override
    public void act(float delta) {
        sprite.act(delta);
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.drawThis(batch);
        super.draw(batch, parentAlpha);
    }

    public boolean isFinished() {
        return sprite.isAnimationFinished();
    }

    public void setOnFinish(Runnable onFinish) {
        this.onFinish = onFinish;
    }

    public void finished() {
        onFinish.run();
    }
}
