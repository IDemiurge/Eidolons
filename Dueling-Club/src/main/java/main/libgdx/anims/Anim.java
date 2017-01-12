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
import main.libgdx.anims.ANIM_MODS.ANIM_MOD;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.libgdx.texture.TextureManager;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/9/2017.
 */
public class Anim extends Group {
    private   AnimData data;
    Vector2 origin;
    Vector2 destination;
    private Ref ref;

    List<Emitter> emitterList;
    int lightEmission; // its own lightmap?
    Color color;
    List<SpriteAnimation> sprites;

    Supplier<Texture> textureSupplier;
    private ANIM_MOD[] mods;

    float stateTime=0 ;
    float duration;

    float offsetX ;
    float offsetY ;
    float x ;
    float y ;
    Vector2 position;


    public Anim(Entity active, AnimData params) {
        ref = active.getRef();
        data= params;
        textureSupplier = () -> TextureManager.getOrCreate(active.getImagePath());
        //TODO colorize, apply alpha, rotate, warp, ...


//        ref.getSourceObj()

    }

    public boolean draw(Batch batch ) {
//switch(template){
//} applyTemplate()

        stateTime += Gdx.graphics.getDeltaTime();
        Texture currentFrame =  textureSupplier.get();
        if (currentFrame==null ){
            dispose();
            return false;
        }
        updatePosition();
        batch.begin();
        batch.draw(textureSupplier.get(), getX(), getY());
        batch.end();
        addLight();
        addEmitters();
        return true;
    }

    private void addLight() {
    }

    private void addEmitters() {
    }

    private void dispose() {
    }

    public void updatePosition(){
          offsetX = (origin.x - destination.x) * stateTime / duration;
          offsetY = (origin.y - destination.y) * stateTime / duration;
       x = (x +offsetX);
        y =(y +offsetY);
    }



}
