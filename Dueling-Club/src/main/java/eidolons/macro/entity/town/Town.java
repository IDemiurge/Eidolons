package eidolons.macro.entity.town;

import eidolons.macro.MacroGame;
import eidolons.macro.entity.MacroRef;
import eidolons.macro.entity.faction.FactionObj;
import eidolons.macro.map.Place;
import eidolons.game.module.herocreator.logic.party.Party;
import main.entity.type.ObjType;
import main.system.datatypes.DequeImpl;

public class Town extends Place {

    Library library;
    Tavern tavern;
    Shop shop;

    DequeImpl<Library> libraries = new DequeImpl<>();
    DequeImpl<Shop> shops = new DequeImpl<>();
    DequeImpl<Tavern> taverns = new DequeImpl<>();
    DequeImpl<TownPlace> townPlaces = new DequeImpl<>();
    DequeImpl<FactionQuarters> fqs = new DequeImpl<>();
    DequeImpl<Party> parties = new DequeImpl<>();

    FactionObj ownerFaction;
    private boolean readyToInit;

    public Town(MacroGame game, ObjType t, MacroRef ref) {
        super(game, t, ref);
        readyToInit = true;
        init();
    }

    @Override
    public void init() {
        if (!readyToInit) {
            return;
        }
        /*
         * setting shops for a town... > SHOT_TYPE? could be relatively easy...
		 */
        super.init();
        TownInitializer.initTownPlaces(this);

    }

    public Tavern getTavern(String tabName) {
        for (Tavern s : getTaverns()) {
            if (s.getName().equals(tabName)) {
                return s;
            }
        }
        return null;
    }

    public Shop getShop(String tabName) {
        for (Shop s : getShops()) {
            if (s.getName().equals(tabName)) {
                return s;
            }
        }
        return null;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public Tavern getTavern() {
        return tavern;
    }

    public void setTavern(Tavern tavern) {
        this.tavern = tavern;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }


    public DequeImpl<Library> getLibraries() {
        return libraries;
    }

    public DequeImpl<Shop> getShops() {
        return shops;
    }

    public DequeImpl<TownPlace> getTownPlaces() {
        return townPlaces;
    }

    public DequeImpl<FactionQuarters> getFqs() {
        return fqs;
    }

    public DequeImpl<Party> getParties() {
        return parties;
    }

    public FactionObj getOwnerFaction() {
        return ownerFaction;
    }

    public void setOwnerFaction(FactionObj ownerFaction) {
        this.ownerFaction = ownerFaction;
    }

    public DequeImpl<Tavern> getTaverns() {
        return taverns;
    }

    public void addTavern(Tavern tavern) {
        getTaverns().add(tavern);
    }

}
