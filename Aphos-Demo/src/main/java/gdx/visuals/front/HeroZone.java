package gdx.visuals.front;

import com.google.inject.internal.util.ImmutableList;
import gdx.dto.FrontLineDto;
import gdx.dto.HeroDto;
import gdx.general.view.ADtoView;
import gdx.views.FieldView;
import gdx.views.HeroView;
import logic.entity.Entity;
import logic.entity.Hero;
import logic.functions.GameController;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.*;

public class HeroZone extends ADtoView<FrontLineDto> {

    public static final float HEIGHT = 700;
    public static final Map<Hero, HeroView> heroes= new HashMap<>();

    public HeroZone() {
        GuiEventManager.bind(GuiEventType.DTO_HeroZone, p -> setDto((FrontLineDto) p.get()));
    }

    public static FieldView getView(Entity entity) {
        return heroes.get(entity);
    }

    @Override
    protected void update() {
        //when else to update this?
        boolean left = true;
        setHeight(700);
        for (List<Entity> side : ImmutableList.of(dto.getSide(), dto.getSide2()))
            for (Entity entity : side) {
                if (entity instanceof Hero) {
                    Hero hero = ((Hero) entity);
                    HeroView view = heroes.get(hero);
                    if (view == null) {
                        view = createView(left, hero);
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

    public static HeroView createView(boolean side, Entity entity) {
        HeroView heroView = new HeroView(side, new HeroDto((Hero) entity));
        GameController.getInstance().setHeroView(heroView);
        GameController.getInstance().setHero((Hero) entity);
        return heroView;
    }
}
