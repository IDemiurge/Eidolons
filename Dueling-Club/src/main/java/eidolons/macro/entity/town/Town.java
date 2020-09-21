package eidolons.macro.entity.town;

import eidolons.content.PROPS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.quest.advanced.Quest;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CONTAINER;
import eidolons.macro.MacroGame;
import eidolons.macro.entity.MacroRef;
import eidolons.macro.entity.faction.FactionObj;
import eidolons.macro.entity.shop.Shop;
import eidolons.macro.map.Place;
import eidolons.system.audio.MusicEnums;
import eidolons.system.audio.MusicEnums.AMBIENCE;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.MACRO_PROPS;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.DequeImpl;

import java.util.LinkedHashSet;
import java.util.Set;

public class Town extends Place {

    Tavern tavern;
    Shop shop;

    DequeImpl<Shop> shops = new DequeImpl<>();
    DequeImpl<Tavern> taverns = new DequeImpl<>();
    DequeImpl<TownPlace> townPlaces = new DequeImpl<>();
    DequeImpl<FactionQuarters> fqs = new DequeImpl<>();
    DequeImpl<Party> parties = new DequeImpl<>();


    FactionObj ownerFaction;
    private final boolean readyToInit;
    private Set<Quest> quests;
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

    public Set<Quest> getQuests() {
        return quests;
    }

    public void setQuests(Set<Quest> quests) {
        this.quests = quests;
        quests.forEach(quest -> quest.setTown(this));
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

    }

    public void exited() {
        Unit hero = Eidolons.getMainHero();
        for (Shop shop : shops) {
            shop.exited(hero);
        }

        hero.setProperty(PROPS.STASH, ContainerUtils.toIdContainer(stash), true);
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

    public AMBIENCE getAmbience() {
        return RandomWizard.random() ? MusicEnums.AMBIENCE.SHIP : MusicEnums.AMBIENCE.TOWN;
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

    public void enter(boolean reenter) {
        for (String substring : ContainerUtils.openContainer(
         getProperty(MACRO_PROPS.TOWN_STASH))) {
            //gonna need to store durability etc...
        }
        Unit hero = Eidolons.getMainHero();
        for (String substring : ContainerUtils.openContainer(
         hero.getProperty(PROPS.STASH))) {
            if (!NumberUtils.isInteger(substring)) {
                continue;
            }
            int id = Integer.valueOf(substring);
            Obj item = Eidolons.getGame().getObjectById(id);
            if (item instanceof DC_HeroItemObj)
                stash.add((DC_HeroItemObj) item);
        }
            for (Shop shop1 : shops) {
            shop1.handleDebt(hero);
                if (reenter) {
                shop1.getIncome( 500);
                shop1.sellItems(30 );
                shop1.stockItems(30 );
                }
        }
    }

    public Integer getReputation() {
       return getIntParam(MACRO_PARAMS.REPUTATION );
    }
    public void reputationImpact(int i) {
        modifyParameter(MACRO_PARAMS.REPUTATION, i);
    }
}
