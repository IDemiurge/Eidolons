package gdx.visuals.front;

import com.google.inject.internal.util.ImmutableList;
import gdx.dto.FrontLineDto;
import gdx.general.view.ADtoView;
import gdx.views.HeroView;
import logic.entity.Entity;
import logic.entity.Hero;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.*;

public class HeroZone extends ADtoView<FrontLineDto> {

    public static final float HEIGHT = 700;
    Map<Hero, HeroView> heroes;

    public HeroZone() {

        GuiEventManager.bind(GuiEventType.DTO_HeroZone, p -> setDto((FrontLineDto) p.get()));
    }

    @Override
    protected void update() {
        heroes = new HashMap<>();
        boolean left = true;
        setHeight(700);
        for (List<Entity> side : ImmutableList.of(dto.getSide(), dto.getSide2()))
            for (Entity entity : side) {
                if (entity instanceof Hero) {
                    Hero hero = ((Hero) entity);
                    HeroView view = heroes.get(hero);
                    if (view == null) {
                        view = ViewManager.createView(left, hero);
                        addActor(view);
                        heroes.put(hero, view);
                    }

                    float x = ViewManager.getHeroX(hero.getPos());
                    float y = ViewManager.getHeroYInverse(hero.getPos());
                    y =getHeight()-y;
                    view.setPosition(x, y);
                }
                left = !left;
            }
    }
//    List<ObjView> heroes;

}
