package eidolons.libgdx.gui.panels.headquarters.party;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.shaders.DarkShader;
import eidolons.libgdx.shaders.ShaderMaster;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 4/16/2018.
 */
public class HqHeroPreview extends FadeImageContainer {
    private final HqHeroDataSource data;
    private boolean highlight;
    private ShaderProgram shader;

    public HqHeroPreview(HqHeroDataSource sub) {
        super(StringMaster.getAppendedImageFile(sub.getImagePath(), " mini"));
        this.data = sub;
//        String path = StringMaster.getAppendedImageFile(sub.getImagePath(), " mini");
//        addActor(new ImageContainer(path));
        setSize(128,46);

        if (data.isDead()) {
//            addActor(new ImageContainer(Images.DEAD_HERO_128));
        }
//        addActor(border = new Image(
//         TextureCache.getOrCreateR(BORDER.NEO_INFO_SELECT_HIGHLIGHT_SQUARE_128
//          .getImagePath())));
//        border.setPosition((128 - border.getWidth()) / 2, (128 - border.getHeight()) / 2);
//        setTeamColor(GdxColorMaster.PALE_GOLD);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if (parentAlpha== ShaderMaster.SUPER_DRAW)
        {
            super.draw(batch, 1);
            return;
        }
        ShaderMaster.drawWithCustomShader( this, batch, shader);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

    }

    public boolean isHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
        if (highlight)
        {
            shader = null;
//            alphaFluctuation(border, delta);
        }
        else
        {
            shader = DarkShader.getShader();
//            border.setVisible(false);
        }
    }
}
