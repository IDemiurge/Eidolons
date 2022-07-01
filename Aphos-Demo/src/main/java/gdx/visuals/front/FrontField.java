package gdx.visuals.front;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import gdx.dto.FrontFieldDto;
import gdx.general.view.ADtoView;
import libgdx.assets.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class FrontField extends ADtoView<FrontFieldDto> {

    FrontCells left, right;

    public FrontField() {
        GuiEventManager.bind(GuiEventType.DTO_FrontField , p-> setDto((FrontFieldDto) p.get()));
    }

    @Override
    protected void update() {
        TextureRegion cell= TextureCache.getOrCreateR(dto.getTxt());
        TextureRegion halfCell= TextureCache.getOrCreateR(dto.getTxt2());
        clearChildren();
        addActor(left = new FrontCells(cell, halfCell, dto.getN(), false));
        addActor(right = new FrontCells(cell, halfCell, dto.getN(), true));
    }
}
