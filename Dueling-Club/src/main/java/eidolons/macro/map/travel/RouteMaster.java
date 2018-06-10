package eidolons.macro.map.travel;

import eidolons.macro.MacroGame;
import eidolons.macro.map.Place;
import eidolons.macro.map.Route;
import eidolons.libgdx.screens.map.editor.MapPointMaster;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.io.File;
import java.util.List;

/**
 * Created by JustMe on 2/22/2018.
 */
public class RouteMaster {

    public RouteMaster() {
        init();
    }

    public static int getDistance(Place location, Place sub) {
//dijstra!
        for (Route route : location.getRoutes()) {
            if (sub.getRoutes().contains(route)) {
                return route.getLength();
            }
        }
        return Integer.MAX_VALUE; //by pixels?
    }

    public void init() {
        List<File> routes = FileManager.getFilesFromDirectory(
         StrPathBuilder.build(PathFinder.getRouteImagePath(), "map"), false, false);

        for (File sub : routes) {
            String[] parts = sub.getName().split("_");
            if (parts.length < 4) {

            }
            String orig = parts[0]; //POINT!
            ObjType type = DataManager.getType(parts[1], MACRO_OBJ_TYPES.ROUTE);
            String dest = parts[2];
            Coordinates coordinates = new Coordinates(true, parts[3]);
            String img = StringMaster.removePreviousPathSegments(sub.getPath(), PathFinder.getImagePath());
            new Route(type, img, orig, dest, coordinates);

        }
        GuiEventManager.bind(MapEvent.MAP_READY, p -> added());
        afterInit();
    }
    public void afterInit() {
        for (Route sub : MacroGame.getGame().getRoutes()) {
            String point = sub.getDestinationPoint();
            sub.setDestination(MapPointMaster.getInstance().getPlaceForPoint(point));

            point = sub.getOriginPoint();
            sub.setOrigin(MapPointMaster.getInstance().getPlaceForPoint(point));

        }
    }

    public void added() {
        for (Route sub : MacroGame.getGame().getRoutes()) {
            GuiEventManager.trigger(MapEvent.ROUTE_ADDED, sub);
        }
    }
}
