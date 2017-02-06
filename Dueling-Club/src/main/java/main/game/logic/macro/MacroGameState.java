package main.game.logic.macro;

import main.entity.obj.Obj;
import main.game.Game;
import main.game.GameState;
import main.game.logic.macro.faction.Faction;
import main.game.logic.macro.map.Place;
import main.game.logic.macro.map.Region;
import main.game.logic.macro.map.Route;
import main.game.logic.macro.town.Town;
import main.game.logic.macro.travel.MacroParty;
import main.system.datatypes.DequeImpl;

public class MacroGameState extends GameState {
    /*
	 * party effects? world effects?
	 * 
	 * but most of all - Places, Settlements
	 */

    DequeImpl<Faction> factions = new DequeImpl<>();
    DequeImpl<Town> towns = new DequeImpl<>();
    DequeImpl<Place> places = new DequeImpl<>();
    DequeImpl<Region> regions = new DequeImpl<>();
    DequeImpl<Route> routes = new DequeImpl<>();
    DequeImpl<MacroParty> parties = new DequeImpl<>();
    public MacroGameState(Game game) {
        super(game);
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

    @Override
    public void resetAll() {
        // TODO
        // afterEffects?

        allToBase();
    }

    @Override
    public void allToBase() {
        for (MacroParty p : parties) {
            p.toBase();
        }
        // PartyManager.getParty().toBase();

    }

    @Override
    protected void initTypeMaps() {
        // TODO Auto-generated method stub

    }

    @Override
    public void checkContinuousRules() {
        // TODO Auto-generated method stub

    }

    @Override
    public void checkCounterRules() {
        // TODO Auto-generated method stub

    }

    @Override
    public void endTurn() {
        // TODO Auto-generated method stub

    }

    @Override
    public void newRound() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void resetCurrentValues() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void applyMods() {
        // TODO Auto-generated method stub

    }

    public DequeImpl<Faction> getFactions() {
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
