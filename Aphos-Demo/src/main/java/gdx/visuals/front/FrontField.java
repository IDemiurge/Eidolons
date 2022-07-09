package gdx.visuals.front;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import gdx.dto.FrontFieldDto;
import gdx.general.Textures;
import gdx.general.view.ADtoView;
import libgdx.GdxMaster;
import libgdx.assets.texture.TextureCache;
import logic.lane.HeroPos;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class FrontField extends ADtoView<FrontFieldDto> {

    private final String path = "ui/main/logo64.png";
    FrontCells left, right;

    public FrontField() {

        GuiEventManager.bind(GuiEventType.DTO_FrontField, p -> setDto((FrontFieldDto) p.get()));
    }

    @Override
    protected void update() {
        for (int i = 0; i < 7; i++) {
            for (boolean side : new boolean[]{true, false}) {
                Image img = null;
                HeroPos pos = new HeroPos(i, side);
                addActor(img = new Image(Textures.getOrCreateR(path)));
                float y = HeroZone.HEIGHT;
                y = y - ViewManager.getHeroYInverse(pos);
                float w = img.getImageWidth();
                float h = img.getImageHeight();
                img.setPosition(ViewManager.getHeroX(pos)-w/2, y-h/2);

            }
        }
        //TODO How will it really work? Maybe just by coordinates, without cell actors?
//        TextureRegion cell= Textures.getOrCreateR(dto.getTxt());
//        TextureRegion halfCell= Textures.getOrCreateR(dto.getTxt2());
//        clearChildren();
//        addActor(left = new FrontCells(cell, halfCell, dto.getN(), false));
//        addActor(right = new FrontCells(cell, halfCell, dto.getN(), true));
    }
}
