package gdx.visuals.front;

import gdx.dto.FrontLineDto;
import gdx.dto.HeroDto;
import gdx.general.view.ADtoView;
import gdx.views.FieldView;
import gdx.views.HeroView;
import logic.core.Aphos;
import logic.entity.Entity;
import logic.entity.Hero;
import main.system.GuiEventManager;
import content.AphosEvent;

import java.util.*;

public class HeroZone extends ADtoView<FrontLineDto> {

    public static final float HEIGHT = 700;
    public static final Map<Hero, HeroView> heroes= new HashMap<>();

    public HeroZone() {
        GuiEventManager.bind(AphosEvent.DTO_HeroZone, p -> setDto((FrontLineDto) p.get()));
    }

    public static FieldView getView(Entity entity) {
        return heroes.get(entity);
    }

    @Override
    protected void update() {
        //when else to update this?
        setHeight(700);
            for (Entity entity : dto.getHeroes()) {
                boolean left = entity.isLeftSide();
                if (entity instanceof Hero) {
                    Hero hero = ((Hero) entity);
                    HeroView view = heroes.get(hero);
                    if (view == null) {
                        view = createView(left, hero);
                        addActor(view);
                        heroes.put(hero, view);
                    }
                    float x = ViewManager.getHeroX(hero.getPos());
                    float y = ViewManager.getHeroY(hero.getPos());
                    view.setPosition(x, y);
                }
            }
    }
//    List<ObjView> heroes;

    public static HeroView createView(boolean side, Entity entity) {
        HeroView heroView = new HeroView(side, new HeroDto((Hero) entity));
        Aphos.view = (heroView);
        return heroView;
    }

}
