package eidolons.libgdx.gui.panels.headquarters.party;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.texture.TextureCache;
import main.system.images.ImageManager.BORDER;

/**
 * Created by JustMe on 4/16/2018.
 */
public class HqHeroPreview extends SuperActor {
    private final HqHeroDataSource data;
    private boolean highlight;

    public HqHeroPreview(HqHeroDataSource sub) {
        this.data = sub;
        addActor(new ImageContainer(sub.getImagePath()));
        if (data.isDead()) {
//            addActor(new ImageContainer(Images.DEAD_HERO_128));
        }
        addActor(border = new Image(
         TextureCache.getOrCreateR(BORDER.NEO_INFO_SELECT_HIGHLIGHT_SQUARE_128
          .getImagePath())));
        border.setPosition((128 - border.getWidth()) / 2, (128 - border.getHeight()) / 2);
        setTeamColor(GdxColorMaster.GOLDEN_WHITE);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (highlight)
            alphaFluctuation(border, delta);
        else
            border.setVisible(false);
    }

    public boolean isHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }
}
