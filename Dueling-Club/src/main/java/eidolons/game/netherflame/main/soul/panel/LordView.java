package eidolons.game.netherflame.main.soul.panel;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.texture.Sprites;

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


















