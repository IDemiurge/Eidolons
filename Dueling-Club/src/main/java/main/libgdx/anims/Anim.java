package main.libgdx.anims;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import main.entity.Entity;
import main.entity.Ref;
import main.libgdx.GameScreen;
import main.libgdx.anims.ANIM_MODS.ANIM_MOD;
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
    List<Emitter> emitterList;
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


    public Anim(Entity active, AnimData params) {
        ref = active.getRef();
        data = params;
        textureSupplier = () -> TextureManager.getOrCreate(active.getImagePath());
        //TODO colorize, apply alpha, rotate, warp, ... based on stateTime

        origin = GameScreen.getInstance().getGridPanel()
         .getVectorForCoordinateWithOffset(ref.getSourceObj().getCoordinates() );
        destination = GameScreen.getInstance().getGridPanel()
         .getVectorForCoordinateWithOffset(ref.getTargetObj().getCoordinates() );
        position =new Vector2(origin);

        duration= 3;
//        duration= params.getIntValue(ANIM_VALUES.DURATION);
    }

    public boolean draw(Batch batch) {
//switch(template){
//} applyTemplate()

        stateTime += Gdx.graphics.getDeltaTime();
        Texture currentFrame = textureSupplier.get();
        if (currentFrame == null || stateTime>=duration) {
            dispose();
            return false;
        }
        updatePosition();
//        batch.begin();
        batch.draw(currentFrame, position.x,  position.y);

        sprites.forEach(s-> s.draw(batch));
//        AnimMaster.getDrawer().draw(this, batch);
//        batch.end();
        return true;
    }

    public void start() {
        sprites.forEach(s-> s.setX(position.x));
        sprites.forEach(s-> s.setY(position.y));
        sprites.forEach(s-> s.setOffsetX(0));
        sprites.forEach(s-> s.setOffsetY(0));
        addLight();
        addEmitters();
    }

    private void addLight() {
    }

    private void addEmitters() {
    }

    private void dispose() {
    }

    public void updatePosition() {
        offsetX = (destination.x - origin.x) * stateTime / duration;
        offsetY = (destination.y - origin.y) * stateTime / duration;
        position.set(position.x + offsetX, position.y + offsetY);
        sprites.forEach(s-> s.setOffsetX(offsetX));
        sprites.forEach(s-> s.setOffsetY(offsetY));
//       lightBody.setPosition()
//        emitterList.forEach(e -> {
//        e.setPos
//        });
    }


    public void setSprites(List<SpriteAnimation> sprites) {
        this.sprites = sprites;
    }
}
