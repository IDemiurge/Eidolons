package boss.anims.old;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.bf.grid.cell.UnitViewOptions;
import libgdx.bf.grid.cell.UnitViewSprite;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.List;

/**
 *
 * BossView contains all sprite-parts, controls their animation, as well as
 *  * visibility
 *  * highlight
 *  * zoom
 *  * targeting
 *
 * It should have the same interface as normal unit views
 * But it will do things differently
 */
public class BossView extends UnitViewSprite {
    List<BossPart> parts;

    public BossView(UnitViewOptions o) {
        super(o);
        GuiEventManager.trigger(GuiEventType.BOSS_VIEW_CREATED, this);
    }

    @Override
    protected void initSprite(UnitViewOptions o) {
//overlaySprite.add(new )

    }

    @Override
    protected FadeImageContainer initPortrait(TextureRegion portraitTexture, String path) {
        return super.initPortrait(portraitTexture, path);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        getSpriteModel().act(Gdx.graphics.getDeltaTime());
        super.draw(batch, parentAlpha);
    }
}
