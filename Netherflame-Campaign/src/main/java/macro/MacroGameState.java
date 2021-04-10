package macro;

import eidolons.macro.entity.faction.FactionObj;
import eidolons.macro.entity.party.MacroParty;
import eidolons.macro.entity.town.Town;
import eidolons.macro.map.Place;
import eidolons.macro.map.Region;
import eidolons.macro.map.Route;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.ConcurrentMap;
import main.entity.obj.Obj;
import main.game.core.state.GameState;
import main.system.datatypes.DequeImpl;

public class MacroGameState extends GameState {
    DequeImpl<FactionObj> factions = new DequeImpl<>();
    DequeImpl<Town> towns = new DequeImpl<>();
    DequeImpl<Place> places = new DequeImpl<>();
    DequeImpl<Region> regions = new DequeImpl<>();
    DequeImpl<Route> routes = new DequeImpl<>();
    DequeImpl<MacroParty> parties = new DequeImpl<>();

    public MacroGameState(MacroGame game) {
        super(game);
    }

    @Override
    public void removed(Obj obj) {
        if (obj instanceof MacroParty) {
            parties.remove(obj);
        }

        if (obj instanceof Place) {

            if (obj instanceof Town) {
                towns.remove(obj);
            } else if (obj instanceof Route) {
                routes.remove(obj);
            } else
                places.remove(obj);

        } else if (obj instanceof Region) {
            regions.remove(obj);
        }

    }

    @Override
    public void addObject(Obj obj) {
        if (obj instanceof MacroParty) {
            parties.add((MacroParty) obj);
        }

        if (obj instanceof Place) {

            if (obj instanceof Town) {
                towns.add((Town) obj);
            } else if (obj instanceof Route) {
                routes.add((Route) obj);
            } else
                places.add((Place) obj);

        } else if (obj instanceof Region) {
            regions.add((Region) obj);
        }

        objMap.put(obj.getId(), obj);
    }

    @Override
    protected void initTypeMaps() {

        for (MACRO_OBJ_TYPES TYPE : MACRO_OBJ_TYPES.values()) {
            getObjMaps().put(TYPE, new ConcurrentMap<>());
        }
    }


    public DequeImpl<FactionObj> getFactions() {
        return factions;
    }

    public DequeImpl<Town> getTowns() {
        return towns;
    }

    public DequeImpl<Place> getPlaces() {
        return places;
    }

    public DequeImpl<Region> getRegions() {
        return regions;
    }

    public DequeImpl<Route> getRoutes() {
        return routes;
    }

    public DequeImpl<MacroParty> getParties() {
        return parties;
    }

}
