package eidolons.game.battlecraft.logic.meta.igg.story.brief;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.video.VideoMaster;

/**
 * video!
 */
public class BriefBackground extends TablePanelX {
    private final FadeImageContainer plain;
    SpriteAnimation sprite;
    public BriefBackground() {
        addActor(plain = new FadeImageContainer());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (sprite!=null )
            if (sprite.getRegions().size>0)
                sprite.draw(batch);
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        sprite= SpriteAnimationFactory.getSpriteAnimation(getUserObject().toString());
        if (sprite.getRegions().size==0)
         plain.setImage(getUserObject().toString());
    }
}
