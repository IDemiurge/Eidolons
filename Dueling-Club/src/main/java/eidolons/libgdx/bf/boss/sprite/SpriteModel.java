package eidolons.libgdx.bf.boss.sprite;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.gui.generic.GroupX;

/**
 * controls the main SpriteAnimation for the boss
 *
 * shaders
 * overlay sprites
 * color/alpha
 * speed
 * substitute sprites
 */
public class SpriteModel extends GroupX {

    SpriteAnimation displayedSprite;
    Vector2 pos;

    public SpriteModel(SpriteAnimation displayedSprite) {
        this.displayedSprite = displayedSprite;

        //size?

//        addListener(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (pos != null) {
        displayedSprite.setOffsetY(pos.y);
        displayedSprite.setOffsetX(pos.x);
        }
        displayedSprite.draw(batch);
        super.draw(batch, parentAlpha);
    }

    public void hide(){
    }
    public void setSpeed(){
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }
}
