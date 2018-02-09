package main.game.module.adventure.gui.map;

import main.content.values.parameters.MACRO_PARAMS;
import main.entity.obj.Obj;
import main.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.game.bf.Coordinates;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.MacroManager;
import main.game.module.adventure.gui.MacroGuiManager;
import main.game.module.adventure.gui.map.obj.MapObjComp;
import main.game.module.adventure.gui.map.obj.PartyComp;
import main.game.module.adventure.gui.map.obj.PlaceComp;
import main.game.module.adventure.gui.map.obj.RouteComp;
import main.game.module.adventure.map.Area;
import main.game.module.adventure.map.Place;
import main.game.module.adventure.map.Region;
import main.game.module.adventure.map.Route;
import main.game.module.adventure.map.area.AreaManager;
import main.game.module.adventure.map.MacroCoordinates;
import main.game.module.adventure.entity.MacroParty;
import main.swing.generic.components.CompVisuals;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.audio.DC_SoundMaster;
import main.system.auxiliary.RandomWizard;
import main.system.images.ImageManager.STD_IMAGES;
import main.system.math.MathMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MapComp implements MouseListener {
    private static final int DEST_OFFSET_X = 0;
    private static final int DEST_OFFSET_Y = 0;
    private static final int LAST_OFFSET_X = 0;
    private static final int LAST_OFFSET_Y = 0;
    private Dimension dimensions;
    private G_Panel comp;
    private Map<Coordinates, PlaceComp> places = new HashMap<>(15);
    private Map<Place, PlaceComp> placeComps = new HashMap<>(45);
    private Map<Coordinates, RouteComp> routes = new HashMap<>(15);
    private Map<Place, RouteComp> routeComps = new HashMap<>(45);
    private PartyComp playerPartyComp;
    private MouseListener listener;
    private int index;

    private boolean allRoutesDisplayedOn;
    private boolean allPlacesDisplayedOn;

    public MapComp() {
        generateComp();
        refresh();
    }

    public void generateComp() {
        setComp(getBackgroundComp());
        if (!MacroManager.isEditMode()) // TODO just remove and add again
        {
            getComp().addMouseListener(this);
        }
    }

    public void highlightsOff() {
        for (Obj o : placeComps.keySet()) {
            MapObjComp component = placeComps.get(o);
            component.setHighlighted(false);
        }
        for (Obj o : routeComps.keySet()) {
            MapObjComp component = routeComps.get(o);
            component.setHighlighted(false);
        }
    }

    public void highlight(Collection<Obj> targets) {
        for (Obj o : targets) {
            MapObjComp component = getComponent(o);
            component.setHighlighted(true);
            component.refresh();
        }
    }

    private MapObjComp getComponent(Obj o) {
        return getComponent(o, true);
    }

    private MapObjComp getComponent(Obj o, boolean repeat) {
        MapObjComp component = placeComps.get(o);
        if (component == null) {
            component = routeComps.get(o);
        }
        if (repeat) {
            if (component == null) {
                initComp(o);
                return getComponent(o, false);
            }
        }
        return component;
    }

    public void refresh() {
        // re-evaluate available Routes
        getComp().removeAll();
        G_Panel backgroundComp = getBackgroundComp();
        getComp().add(backgroundComp); // ++ border of sorts?
        if (listener != null) {
            backgroundComp.addMouseListener(listener);
        }
        index = 0;
        if (playerPartyComp == null) {
            playerPartyComp = new PartyComp(getParty());
        }
        Coordinates ppp = getPlayerPartyPoint();
        getComp().add(playerPartyComp.getComp(), "pos " + ppp.x + " " + ppp.y);
        comp.setComponentZOrder(playerPartyComp.getComp(), index);
        index++;

        resetVisiblePlaces();

        resetDisplayedRoutes();

        for (Coordinates p : routes.keySet()) {
            RouteComp routeComp = routes.get(p); // ++ refresh
            add(routeComp, "pos " + p.x + " " + p.y);
            routeComp.refresh();
        }
        for (Coordinates p : places.keySet()) {
            PlaceComp placeComp = places.get(p); // ++ symbol if not available?
            // Selectable?
            getComp().add(placeComp.getComp(), "pos " + p.x + " " + p.y);
            comp.setComponentZOrder(placeComp.getComp(), index);
            index++;
            placeComp.refresh();
        }
        comp.setComponentZOrder(backgroundComp, index);

        addSpecialIcons();

        // Route route = getParty().getCurrentRoute();
        // if (route != null) {
        // getComp().add(
        // route.getComp().getComp(),
        // "pos " + route.getMapPoint().x + " "
        // + route.getMapPoint().y);
        // }
        // only display the active route if traveling!
        getComp().revalidate();
    }

    private void add(JComponent comp, Coordinates c) {
        add(comp, "pos " + c.x + " " + c.y);
    }

    private void add(MapObjComp comp, String constraints) {
        add(comp.getComp(), constraints);
    }

    private void add(JComponent comp, String constraints) {
        getComp().add(comp, constraints);
        this.comp.setComponentZOrder(comp, index);
        index++;
    }

    private void addSpecialIcons() {
        Place dest = MacroGame.getGame().getPlayerParty()
                .getCurrentDestination();
        if (dest != null) {
            Coordinates c = new Coordinates(true, dest.getCoordinates().x
                    + DEST_OFFSET_X, dest.getCoordinates().y + DEST_OFFSET_Y);
            add(new GraphicComponent(STD_IMAGES.FLAG), c);
        }
        Place lastLocation = MacroGame.getGame().getPlayerParty()
                .getLastLocation();
        if (lastLocation != null) {
            Coordinates c = new Coordinates(true,
                    lastLocation.getCoordinates().x + LAST_OFFSET_X,
                    lastLocation.getCoordinates().y + LAST_OFFSET_Y);
            add(new GraphicComponent(STD_IMAGES.FOOT), c);
        }
        // last battle to be displayed?
        // cities - special comps? select icon-mode - display known taverns,
        // shop, ...

        // ++ active route should be enlarged or so...

    }

    private void initComp(Obj o) {
        if (o instanceof Route) {
            Route route = (Route) o;
            RouteComp component = new RouteComp(route);
            routeComps.put(route, component);
        }
        if (o instanceof Place) {
            Place place = (Place) o;
            PlaceComp component = new PlaceComp(place);
            placeComps.put(place, component);
        }

    }

    private void resetVisiblePlaces() {
        places.clear();
        for (Place p : getRegion().getPlaces()) {
            if (p.isVisible()) {
                PlaceComp placeComp = getPlaceComp(p);

                places.put(adjustPlaceCoordinates(placeComp), placeComp);
            }
        }

    }

    private Coordinates adjustPlaceCoordinates(PlaceComp placeComp) {
        Coordinates c = placeComp.getObj().getCoordinates();
        int x = c.x - placeComp.getSize().width / 2;
        int y = c.y - placeComp.getSize().height / 2;
        return new MacroCoordinates(x, y);
    }

    private void resetDisplayedRoutes() {
        routes.clear();
        for (Route r : getRegion().getRoutes()) {
            r.setCoordinatesValid(false);
        }
        for (Route r : getRegion().getRoutes()) {
            // TODO not all routes should be *displayed*, really...
            r.resetCoordinates();
            if (r.isVisible()) {
                if (r.isAvailable() || isAllRoutesDisplayedOn()) {
                    RouteComp routeComp = getRouteComp(r);
                    routes.put(adjustRouteCoordinate(routeComp), routeComp);
                }
            }
        }
    }

    private Coordinates adjustRouteCoordinate(RouteComp routeComp) {
        Coordinates c = routeComp.getObj().getCoordinates();
        int x = c.x - routeComp.getSize().width / 2;
        int y = c.y - routeComp.getSize().height / 2;
        return new MacroCoordinates(x, y);
    }

    private Coordinates getPartyPoint(MacroParty party) {
        Coordinates c;

        if (party.getCurrentRoute() != null) {
            Route route = party.getCurrentRoute();
            int displacement = party
                    .getIntParam(MACRO_PARAMS.ROUTE_PROGRESS_PERCENTAGE);
            Coordinates c1 = route.getOrigin().getCoordinates();
            Coordinates c2 = route.getDestination().getCoordinates();
            if (route.isBackwards(party)) {
                displacement = 100 - displacement;
                c = c1;
                c2 = c1;
                c1 = c;
            }
            c = CoordinatesMaster.getCoordinateBetween(c1, c2, displacement,
                    RandomWizard.getRandomIntBetween(
                            MacroGuiManager.PARTY_MAP_DISPLACEMENT_X / 2,
                            MathMaster.applyMod(route.getLength(),
                                    route.getBendFactor())));
            party.setCoordinates(c);
            return c;
        }

        if (party.getCurrentLocation() != null) {
            c = party.getCurrentLocation().getCoordinates();
        } else {
            c = party.getCurrentLocation().getCoordinates();
        }
        // 'attached' objects?
        return new Coordinates(true, c.x
                + MacroGuiManager.PARTY_MAP_DISPLACEMENT_X, c.y
                + MacroGuiManager.PARTY_MAP_DISPLACEMENT_Y);

    }

    public boolean isAllRoutesDisplayedOn() {
        return allRoutesDisplayedOn;
    }

    public void setAllRoutesDisplayedOn(boolean allRoutesDisplayedOn) {
        this.allRoutesDisplayedOn = allRoutesDisplayedOn;
    }

    private Coordinates getPlayerPartyPoint() {
        return getPartyPoint(MacroGame.getGame().getPlayerParty());

    }

    private G_Panel getBackgroundComp() {
        return new G_Panel(new CompVisuals(dimensions, getMapImagePath())) {
            @Override
            protected boolean isBackgroundMouseListener() {
                return true;
            }
        };
    }

    private Region getRegion() {
        return MacroGame.getGame().getRef().getRegion();
    }

    private MacroParty getParty() {
        return MacroGame.getGame().getPlayerParty();
    }

    private RouteComp getRouteComp(Route p) {
        RouteComp comp = routeComps.get(p);
        if (comp == null) {
            comp = new RouteComp(p);
            routeComps.put(p, comp);
        }
        return comp;
    }

    private PlaceComp getPlaceComp(Place p) {
        PlaceComp placeComp = placeComps.get(p);
        if (placeComp == null) {
            placeComp = new PlaceComp(p);
            placeComps.put(p, placeComp);
        }
        return placeComp;
    }

    private String getMapImagePath() {
        // if (night) path += ""
        return getRegion().getMapImagePath();
    }

    public G_Panel getComp() {
        return comp;
    }

    public void setComp(G_Panel comp) {
        this.comp = comp;
    }

    public void setMouseListener(MouseListener l) {
        this.listener = l;
        getComp().addMouseListener(listener);

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        MacroCoordinates c = new MacroCoordinates(e.getPoint().x
                - MacroGuiManager.getMapOffsetX(), e.getPoint().y
                - MacroGuiManager.getMapOffsetY());
        Area area = AreaManager.getAreaForCoordinate(c);
        if (area != null) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK);
            MacroGame.getGame().getManager().infoSelect(area);
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }
}
