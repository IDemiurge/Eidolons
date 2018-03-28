package main.game.module.adventure.town;

import main.client.cc.logic.party.Party;
import main.client.dc.HC_SequenceMaster;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.meta.universal.PartyHelper;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.MacroRef;
import main.game.module.adventure.faction.FactionObj;
import main.game.module.adventure.map.Place;
import main.system.datatypes.DequeImpl;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

public class Town extends Place {

    Library library;
    Tavern tavern;
    Shop shop;
    TownHall townHall;

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

        // ++ factions

        // ObjType type =
        // DataManager.getType(getProperty(MACRO_PROPS.TOWN_HALL),
        // MACRO_OBJ_TYPES.SHOP);
        // townHall = new TownHall(getGame(), type, ref);
        // ownerFaction = FactionMaster
        // .getFaction(getProperty(MACRO_PROPS.FACTION));

    }

    public Tavern selectTavern() {
        if (taverns.isEmpty()) {
            return null;
        }
        if (taverns.size() == 1) {
            return taverns.get(0);
        }
        HC_SequenceMaster sequenceMaster = new HC_SequenceMaster();
        sequenceMaster.launchEntitySelection(taverns, PartyHelper.getParty()
         .getLeader(), "Select Tavern");
        if (WaitMaster.waitForInput(WAIT_OPERATIONS.SELECTION) == null) {
            return null;
        }
        return getTavern(sequenceMaster.getSequence().getValue());
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

    public TownHall getTownHall() {
        return townHall;
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
