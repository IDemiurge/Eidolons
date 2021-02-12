package libgdx.gui.panels.lord;

import com.badlogic.gdx.graphics.g2d.Batch;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.gui.panels.TablePanelX;
import eidolons.content.consts.Sprites;

public class LordView extends TablePanelX {
    private final SpriteAnimation sprite;

    /**
     * any info there in the header?
     * buttons?
     *
     * sprite vs static?
     * border?
     *
     * swap view with Nethergate?
     *
     */


    public LordView() {
        super(650, 1050);
        sprite = SpriteAnimationFactory.getSpriteAnimation(Sprites.HERO_KESERIM);
        sprite. centerOnScreen();
//        new SpriteActor()
    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
        super.draw(batch, parentAlpha);
    }
}


















