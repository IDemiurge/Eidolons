package eidolons.game.battlecraft.logic.meta.igg.story.brief;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.video.VideoMaster;

/**
 * video!
 */
public class BriefBackground extends FullscreenAnimation {

    public BriefBackground() {
        super(true);
    }
    public BriefBackground(String background) {
        super(true);
        setUserObject(background);
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        if (userObject instanceof String) {
            initSprite(userObject.toString());
        }
    }
}
