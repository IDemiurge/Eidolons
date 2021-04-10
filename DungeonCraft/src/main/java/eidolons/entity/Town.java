package eidolons.entity;

import eidolons.content.PROPS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.universal.shop.Shop;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.quest.advanced.Quest;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.system.audio.MusicEnums;
import eidolons.system.audio.MusicEnums.AMBIENCE;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.MACRO_PROPS;
import main.entity.LightweightEntity;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.DequeImpl;

import java.util.Set;

import static eidolons.content.consts.VisualEnums.CONTAINER.STASH;
import static eidolons.content.consts.VisualEnums.CONTAINER.UNASSIGNED;

@Deprecated
public class Town extends LightweightEntity {

    Shop shop;

    DequeImpl<Shop> shops = new DequeImpl<>();
    DequeImpl<Party> parties = new DequeImpl<>();
    private Set<Quest> quests;
    private Set<DC_HeroItemObj> stash;

    public Town(ObjType type) {
        super(type);
    }


    public boolean isVisible() {
        return true;
    }

    public Set<Quest> getQuests() {
        return quests;
    }

    public void setQuests(Set<Quest> quests) {
        this.quests = quests;
        // quests.forEach(quest -> quest.setTown(this));
    }

    // public void init() {
    //     if (!readyToInit) {
    //         return;
    //     }
    //     /*
    //      * setting shops for a town... > SHOT_TYPE? could be relatively easy...
	// 	 */
    //     super.init();
    //     TownInitializer.initTownPlaces(this);
    //
    //     stash = new LinkedHashSet<>();
    // }

    public void exited() {
        Unit hero = Eidolons.getMainHero();
        for (Shop shop : shops) {
            shop.exited(hero);
        }

        hero.setProperty(PROPS.STASH, ContainerUtils.toIdContainer(stash), true);
    }
    public Shop getShop(String tabName) {
        for (Shop s : getShops()) {
            if (s.getName().equals(tabName)) {
                return s;
            }
        }
        return null;
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
        item.setContainer(UNASSIGNED);
        return true;
    }

    public void addToStash(DC_HeroItemObj item) {
        getStash().add(item);
        item.setContainer(STASH);
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
            int id = Integer.parseInt(substring);
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
