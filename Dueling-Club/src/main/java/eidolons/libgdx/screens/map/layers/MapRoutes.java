package eidolons.libgdx.screens.map.layers;

import com.badlogic.gdx.scenes.scene2d.Group;
import eidolons.macro.map.Place;
import eidolons.macro.map.Route;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActorMaster;
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

                hoverOff(sub);
            }
            if (r != null)
                hover(map.get(r));
        });
        GuiEventManager.bind(MapEvent.ROUTE_ADDED, p -> {
            try {
                Route sub = (Route) p.get();
                RouteActor actor = new RouteActor(sub);
                map.put(sub, actor);
                addActor(actor);
                actor.setFluctuateAlpha(true);
                actor.setPosition(sub.getX(), sub.getY());
                actor.setVisible(false);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
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

    private void hoverOff(RouteActor sub) {
        sub.setHighlighted(false);
        ActorMaster.addMoveToAction(sub, sub.getRoute().getX(), sub.getRoute().getY(), 0.5f);
    }

    private void hover(RouteActor sub) {
        sub.setHighlighted(true);
        ActorMaster.addMoveToAction(sub, sub.getRoute().getX(), sub.getRoute().getY() + GdxMaster.adjustSize(25), 0.5f);
    }

    public boolean isRouteHighlighted() {
        for (RouteActor sub : map.values()) {
            if ((sub).isHighlighted())
                return true;
        }
        return false;
    }
}
