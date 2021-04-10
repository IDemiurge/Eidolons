package libgdx.gui.panels.headquarters.party;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import libgdx.shaders.DarkShader;
import libgdx.shaders.ShaderDrawer;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 4/16/2018.
 */
public class HqHeroPreview extends FadeImageContainer {
    private boolean highlight;
    private ShaderProgram shader;

    public HqHeroPreview(HqHeroDataSource sub) {
        super(StringMaster.getAppendedImageFile(sub.getImagePath(), " mini"));
        //        String path = StringMaster.getAppendedImageFile(sub.getImagePath(), " mini");
//        addActor(new ImageContainer(path));
//        setSize(128,46);

        if (sub.isDead()) {
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

        if (parentAlpha== ShaderDrawer.SUPER_DRAW)
        {
            super.draw(batch, 1);
            return;
        }
        ShaderDrawer.drawWithCustomShader( this, batch, shader);
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
            shader = DarkShader.getDarkShader();
//            border.setVisible(false);
        }
    }
}
