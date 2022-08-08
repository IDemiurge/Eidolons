package gdx.visuals.front;

import gdx.dto.FrontFieldDto;
import gdx.general.view.ADtoView;
import gdx.visuals.lanes.LaneConsts;
import logic.lane.HeroPos;
import main.system.GuiEventManager;
import content.AphosEvent;
import main.system.auxiliary.RandomWizard;

public class FrontField extends ADtoView<FrontFieldDto> {
    FrontCells left, right;

    public FrontField() {
        GuiEventManager.bind(AphosEvent.DTO_FrontField, p -> setDto((FrontFieldDto) p.get()));
    }

    @Override
    protected void update() {
        for (int i = 0; i < LaneConsts.CELLS_PER_SIDE; i++) {
            for (boolean side : new boolean[]{true, false}) {
                HeroPos pos = new HeroPos(i, side);
                    FrontCell cell = new FrontCell(pos);
                    addActor(cell);
                    cell.setPosition(ViewManager.getHeroX(pos),ViewManager.getHeroY(pos));
                    cell.act(5*RandomWizard.getRandomFloat());
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
