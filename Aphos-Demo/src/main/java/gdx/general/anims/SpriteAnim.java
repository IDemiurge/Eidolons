package gdx.general.anims;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import gdx.general.Textures;
import gdx.general.anims.ActionAnims.DUMMY_ANIM_TYPE;
import libgdx.anims.sprite.SpriteAnimation;
import main.data.filesys.PathFinder;

import java.util.HashMap;
import java.util.Map;

public class SpriteAnim extends Actor {
    private final SpriteAnimation sprite;
    private final Map<String, Array<TextureAtlas.AtlasRegion>> cache = new HashMap<>();
    private Runnable onFinish;

    public SpriteAnim(String spritePath, Vector2 pos, DUMMY_ANIM_TYPE type) {
        if (type == DUMMY_ANIM_TYPE.lane_death || type == DUMMY_ANIM_TYPE.explode) {
            Texture texture = Textures.getOrCreateTexture(spritePath);
            //TODO refactor + cache
            //regions = TextureManager.getSpriteSheetFrames(spritePath, false, texture);
            sprite = new SpriteAnimation(0.05f, false, 2, spritePath, texture, false);
        } else {
            Array<TextureAtlas.AtlasRegion> regions = cache.get(spritePath);
            if (regions == null) {
                regions = new TextureAtlas(PathFinder.getImagePath() + spritePath).getRegions();
                cache.put(spritePath, regions);
            }
            float frameDur=0.05f;
            if (type==DUMMY_ANIM_TYPE.lane_hit)
                frameDur = 0.07f;
            sprite = new SpriteAnimation(frameDur, false, regions);
        }
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

    public void addOnFinish(Runnable onFinish) {
        if (this.onFinish != null) {
            Runnable temp = this.onFinish;
            Runnable temp2 = onFinish;
            onFinish = () -> {
                temp.run();
                temp2.run();
            };
        }
        this.onFinish = onFinish;
    }

    public void finished() {
        if (onFinish != null) {
            onFinish.run();
        }
    }
}
