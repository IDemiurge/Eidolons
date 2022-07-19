package gdx.visuals.front;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import gdx.dto.FrontFieldDto;
import gdx.general.Textures;
import gdx.general.view.ADtoView;
import gdx.visuals.lanes.LaneConsts;
import libgdx.GdxMaster;
import libgdx.assets.texture.TextureCache;
import logic.lane.HeroPos;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;

public class FrontField extends ADtoView<FrontFieldDto> {
    FrontCells left, right;

    public FrontField() {
        GuiEventManager.bind(GuiEventType.DTO_FrontField, p -> setDto((FrontFieldDto) p.get()));
    }

    @Override
    protected void update() {
        for (int i = 0; i < LaneConsts.CELLS_PER_SIDE; i++) {
            for (boolean side : new boolean[]{true, false}) {
                HeroPos pos = new HeroPos(i, side);
                    FrontCell cell = new FrontCell(pos);
                    addActor(cell);
                    float y = HeroZone.HEIGHT;
                    y = y - ViewManager.getHeroYInverse(pos);
                    cell.setPosition(ViewManager.getHeroX(pos), y);
                    cell.act(5*RandomWizard.getRandomFloat());
            }
        }
        debugAll();

        //TODO How will it really work? Maybe just by coordinates, without cell actors?
//        TextureRegion cell= Textures.getOrCreateR(dto.getTxt());
//        TextureRegion halfCell= Textures.getOrCreateR(dto.getTxt2());
//        clearChildren();
//        addActor(left = new FrontCells(cell, halfCell, dto.getN(), false));
//        addActor(right = new FrontCells(cell, halfCell, dto.getN(), true));
    }
}
