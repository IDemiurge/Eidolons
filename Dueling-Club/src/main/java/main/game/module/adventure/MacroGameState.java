package main.game.module.adventure;

import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.ConcurrentMap;
import main.entity.obj.Obj;
import main.game.battlecraft.logic.meta.faction.FactionObj;
import main.game.core.state.GameState;
import main.game.module.adventure.entity.MacroParty;
import main.game.module.adventure.map.Place;
import main.game.module.adventure.map.Region;
import main.game.module.adventure.map.Route;
import main.game.module.adventure.town.Town;
import main.system.datatypes.DequeImpl;

public class MacroGameState extends GameState {
    /*
     * party effects? world effects?
	 * 
	 * but most of all - Places, Settlements
	 */

    DequeImpl<FactionObj> factions = new DequeImpl<>();
    DequeImpl<Town> towns = new DequeImpl<>();
    DequeImpl<Place> places = new DequeImpl<>();
    DequeImpl<Region> regions = new DequeImpl<>();
    DequeImpl<Route> routes = new DequeImpl<>();
    DequeImpl<MacroParty> parties = new DequeImpl<>();

    public MacroGameState(MacroGame game) {
        super(game);
        manager = new MacroStateManager(this);
    }

    @Override
    public void addObject(Obj obj) {
        if (obj instanceof MacroParty) {
            parties.add((MacroParty) obj);
        }

        if (obj instanceof Place) {
            places.add((Place) obj);

            if (obj instanceof Town) {
                towns.add((Town) obj);
            }
            if (obj instanceof Route) {
                routes.add((Route) obj);
            }

        } else if (obj instanceof Region) {
            regions.add((Region) obj);
        }

        super.addObject(obj);
    }

    public void allToBase() {
        for (MacroParty p : parties) {
            p.toBase();
        }
        // PartyManager.getParty().toBase();

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
