package gdx.visuals.front;

import gdx.dto.HeroDto;
import gdx.views.HeroView;
import logic.entity.Entity;
import logic.entity.Hero;
import logic.functions.GameController;

public class ViewManager {
    public static HeroView createView(boolean side, Entity entity) {
        HeroView heroView = new HeroView(side, new HeroDto((Hero) entity));
        GameController.getInstance().setHeroView(heroView);
        return heroView;
    }
}
