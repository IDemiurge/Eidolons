package eidolons.macro.entity.town;

import eidolons.entity.item.DC_HeroItemObj;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CONTAINER;
import eidolons.macro.MacroGame;
import eidolons.macro.entity.MacroRef;
import eidolons.macro.entity.faction.FactionObj;
import eidolons.macro.map.Place;
import eidolons.system.audio.MusicMaster.AMBIENCE;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.MACRO_PROPS;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.DequeImpl;

import java.util.LinkedHashSet;
import java.util.Set;

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
    private Set<ObjType> quests;
    private Set<DC_HeroItemObj> stash;

    public Town(MacroGame game, ObjType t, MacroRef ref) {
        super(game, t, ref);
        readyToInit = true;
        init();
        visibilityStatus = PLACE_VISIBILITY_STATUS.AVAILABLE;
    }

    public boolean isVisible() {
        return true;
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

        stash = new LinkedHashSet<>();
        for (String substring : ContainerUtils.openContainer(
         getProperty(MACRO_PROPS.TOWN_STASH))) {
            //gonna need to store durability etc...

        }

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

    public Set<ObjType> getQuests() {
        return quests;
    }

    public void setQuests(Set<ObjType> quests) {
        this.quests = quests;
    }

    public AMBIENCE getAmbience() {
        return RandomWizard.random() ? AMBIENCE.SHIP : AMBIENCE.TOWN;
    }

    public Set<DC_HeroItemObj> getStash() {
        return stash;
    }

    public int getStashSize() {
        if (!checkParam(MACRO_PARAMS.TOWN_STASH_SIZE)) {
            return 40;
        }
        return getIntParam(MACRO_PARAMS.TOWN_STASH_SIZE);
    }

    public boolean isStashFull() {
        return stash.size() >= getStashSize();
    }

    public boolean removeFromStash(DC_HeroItemObj item) {
        if (!getStash().remove(item))
            return false;
        getStash().remove(item);
        item.setContainer(CONTAINER.UNASSIGNED);
        return true;
    }

    public void addToStash(DC_HeroItemObj item) {
        getStash().add(item);
        item.setContainer(CONTAINER.STASH);
    }
}
