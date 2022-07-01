package gdx.visuals.front;

import gdx.dto.FrontLineDto;
import gdx.general.view.ADtoView;
import gdx.views.HeroView;
import logic.entity.Entity;
import logic.entity.Hero;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.ArrayList;
import java.util.List;

public class FrontLine extends ADtoView<FrontLineDto> {

    List<HeroView> heroes;

    public FrontLine() {

        GuiEventManager.bind(GuiEventType.DTO_HeroLines, p -> setDto((FrontLineDto) p.get()));
    }

    @Override
    protected void update() {
        heroes = new ArrayList<>();
        boolean side = false;
        for (Entity entity : dto.getSide()) {
            if (entity instanceof Hero)
                heroes.add(ViewManager.createView(side, entity));
        }
    }
//    List<ObjView> heroes;

}
