package main.libgdx.screens.map.sfx;

import com.badlogic.gdx.scenes.scene2d.Group;
import main.game.module.adventure.map.Place;
import main.game.module.adventure.map.Route;
import main.system.GuiEventManager;
import main.system.MapEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/20/2018.
 */
public class MapRoutes extends Group {
    Map<Route, RouteActor> map = new HashMap<>();

    public MapRoutes() {
        GuiEventManager.bind(MapEvent.ROUTE_HOVERED, p -> {
            Route r = (Route) p.get();
            for (RouteActor sub : map.values()) {
                sub.setHighlighted(false);
            }
            if (r!=null )
            map.get(r).setHighlighted(true);
        });
            GuiEventManager.bind(MapEvent.ROUTE_ADDED, p -> {
            Route sub = (Route) p.get();
            RouteActor actor = new RouteActor(sub);
            map.put(sub, actor);
            addActor(actor);
            actor.setFluctuateAlpha(true);
            actor.setPosition(sub.getX(), sub.getY());
            actor.setVisible(false);
        });
        GuiEventManager.bind(MapEvent.PLACE_HOVER, p -> {
            Place place = (Place) p.get();
            if (place == null) {
                for (RouteActor sub : map.values()) {
                    (sub).setVisible(false);
                }
            } else
                for (Route sub : place.getRoutes()) {
                    map.get(sub).setVisible(true);
                }
        });

//init();
    }

    public boolean isRouteHighlighted() {
        for (RouteActor sub : map.values()) {
            if ( (sub).isHighlighted())
                return true;
        }
        return false;
    }
}
