package eidolons.macro.entity.town;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.universal.PartyHelper;
import eidolons.macro.MacroGame;
import eidolons.macro.MacroManager;
import eidolons.macro.entity.party.MacroParty;
import eidolons.macro.global.time.TimeMaster;
import eidolons.macro.generation.HeroGenerator;
import eidolons.game.module.herocreator.logic.party.Party;
import main.content.DC_TYPE;
import main.content.enums.entity.HeroEnums.BACKGROUND;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.entity.FilterMaster;

import java.util.ArrayList;
import java.util.List;

public class Tavern extends TownPlace {
    private TOWN_PLACE_TYPE TYPE;
    private List<Unit> heroes = new ArrayList<>();
    // buy drinks...
    private List<Unit> mercs;
    private List<Party> stayingParties;
    private TAVERN_MODIFIER modifier;

    public Tavern(MacroGame game, ObjType type, Ref ref) {
        super(game, type, ref);
        if (!MacroManager.isEditMode()) {
            generateHeroes();
        }
    }

    public TOWN_PLACE_TYPE getTYPE() {
        if (TYPE == null) {
            TYPE = new EnumMaster<TOWN_PLACE_TYPE>().retrieveEnumConst(
             TOWN_PLACE_TYPE.class,
             getProperty(MACRO_PROPS.TOWN_PLACE_TYPE));
        }
        return TYPE;
    }

    public TAVERN_MODIFIER getModifier() {
        if (modifier == null) {
            modifier = new EnumMaster<TAVERN_MODIFIER>().retrieveEnumConst(
             TAVERN_MODIFIER.class,
             getProperty(MACRO_PROPS.TOWN_PLACE_MODIFIER));
        }
        return modifier;
    }

    public void generateHeroes() {
        // special campaign heroes
        // getTown().getFactions();

        int total = TavernMaster.getXpPool(this);
        setParam(MACRO_PARAMS.C_HERO_POWER_POOL, total);
        Integer number = getIntParam(MACRO_PARAMS.PREFERRED_HERO_NUMBER); // min/max
        if (number == 0) {
            number = 4;
        }
        while (true) {
            int xp = total / number * RandomWizard.getRandomIntBetween(40, 60)
             / 50;
            if (xp < TavernMaster.getMinimumHeroXp(this)) {
                if (total < TavernMaster.getMinimumHeroXp(this)) {
                    break;
                }
                xp = TavernMaster.getMinimumHeroXp(this);
                total -= xp;
                number--;
            }
            boolean background = false;
            // RandomWizard.random();TODO
            if (!newHero(background, xp)) {
                break;
            }
            if (number <= heroes.size()) {
                break;
            }
        }
    }

    public boolean newHero(boolean background, int xp) {
        // chance
        Unit hero;
        if (!background) {
            ObjType type = getRandomHeroType(xp);
            if (type == null) {
                return false;
            }
            hero = new Unit(type);
            initHeroHireParams(hero);
            HeroGenerator.alterHero(hero);
        } else {
            ObjType type = getRandomHeroBackground();
            hero = generateHeroFromBackground(type, xp);
        }
        hero.setHidden(true);
        if (hero == null) {
            return false;
        }

        return addHero(hero);
    }

    private Unit generateHeroFromBackground(ObjType type, int xp) {
        Unit hero = HeroGenerator.generateHero(type, xp, this);
        initHeroHireParams(hero); // random?
        return hero;
    }

    private void initHeroHireParams(Unit hero) {
        int cost = hero.calculatePower() * 5; // TODO
        hero.setParam(MACRO_PARAMS.HIRE_COST, cost, true);
        int share = 100;
        hero.setParam(MACRO_PARAMS.GOLD_SHARE, share, true);
    }

    private boolean addHero(Unit hero) {
        modifyParameter(MACRO_PARAMS.C_HERO_POWER_POOL,
         -hero.getIntParam(PARAMS.TOTAL_XP));
        heroes.add(hero);
        return getIntParam(MACRO_PARAMS.C_HERO_POWER_POOL) < TavernMaster
         .getMinimumHeroXp(this);
    }

    public void newTurn() {
        modifyParameter(
         MACRO_PARAMS.C_FOOD_STORE,
         // TavernMaster.getFoodPerTurn(this)
         getIntParam(MACRO_PARAMS.FOOD_STORE)
          * TimeMaster.getHoursPerTurn() / 100);
        checkRemoveHeroes();

        modifyParameter(MACRO_PARAMS.C_HERO_POWER_POOL,
         TavernMaster.getXpPerTurn(this));
        LogMaster.log(LOG_CHANNEL.MACRO_DYNAMICS, getName() + " now has "
         + getIntParam(MACRO_PARAMS.C_HERO_POWER_POOL) + " "
         + MACRO_PARAMS.C_HERO_POWER_POOL.getName());
        checkAddNewHeroes();
    }

    private void checkAddNewHeroes() {

        if (getIntParam(MACRO_PARAMS.C_HERO_POWER_POOL) > TavernMaster
         .getMinimumHeroXp(this)) {
            // chance
            newHero(true, getIntParam(MACRO_PARAMS.C_HERO_POWER_POOL));
        }

    }

    private void checkRemoveHeroes() {
        int removeChance = 25;
        while (RandomWizard.chance(removeChance)) {
            heroes.remove(RandomWizard.getRandomListIndex(heroes));
        }
    }

    private ObjType getRandomHeroBackground() {
        List<ObjType> list = DataManager.getTypesGroup(DC_TYPE.CHARS,
         StringMaster.BACKGROUND);
        String prop = new RandomWizard<BACKGROUND>().getObjectByWeight(
         getHeroBackgrounds(), BACKGROUND.class).toString();

        FilterMaster.filter(list, G_PROPS.BACKGROUND.getName(), prop,
         DC_TYPE.CHARS, true, false, false);
        return new RandomWizard<ObjType>().getRandomListItem(list);
    }

    private String getHeroBackgrounds() {
        String property = getProperty(MACRO_PROPS.HERO_BACKGROUNDS);
        if (property.isEmpty()) {
            TavernMaster.generateTavernHeroBackground(this);
            property = getProperty(MACRO_PROPS.HERO_BACKGROUNDS);
        }
        return property;
    }

    private ObjType getRandomHeroType(int xp) {
        int minXp = xp / 3 * 2;
        int maxXp = xp * 3 / 2;
        List<ObjType> list = DataManager.toTypeList(DataManager
          .getTypesSubGroupNames(DC_TYPE.CHARS, StringMaster.PRESET),
         DC_TYPE.CHARS);
        // DataManager.getTypesSubGroup(OBJ_TYPES.CHARS,
        // StringMaster.PRESET);
        // background allowed
        FilterMaster.filterByParam(list, PARAMS.TOTAL_XP, minXp,
         DC_TYPE.CHARS, true);
        FilterMaster.filterByParam(list, PARAMS.TOTAL_XP, maxXp,
         DC_TYPE.CHARS, false);

        Loop.startLoop(25);
        while (!Loop.loopEnded()) {
            ArrayList<ObjType> bufferList = new ArrayList<>(list);
            String prop = new RandomWizard<BACKGROUND>().getObjectByWeight(
             getHeroBackgrounds(), BACKGROUND.class).toString();
            FilterMaster.filter(bufferList, G_PROPS.BACKGROUND.getName(),
             prop, DC_TYPE.CHARS, true, false, false);
            if (!bufferList.isEmpty()) {
                list = bufferList;
                break;
            }
        }
        if (list.isEmpty()) {
            return null;
        }
        // by classes, by principles, by faction, by deity...

        // FilterMaster.filterByProp(list, G_PROPS.GROUP.getName(),
        // getProperty(MACRO_PROPS.HERO_GROUPS), OBJ_TYPES.CHARS, true,
        // false, false);
        // FilterMaster.filterByProp(list, "group", "tavern");
        // FilterMaster.filterByProp(list, "tavern type",
        // getProperty(MACRO_PROPS.TOWN_PLACE_TYPE));
        // if (!getProperty(MACRO_PROPS.TOWN_PLACE_MODIFIER).isEmpty())
        // FilterMaster.filterByProp(list, "tavern type",
        // getProperty(MACRO_PROPS.TOWN_PLACE_MODIFIER));
        // background types VS preset types

        return new RandomWizard<ObjType>().getRandomListItem(list);
    }

    public void buyProvisions(Boolean max_min_all) {
        buyProvisions(max_min_all, MacroManager.getActiveParty());
    }

    public void buyProvisions(Boolean max_min_all, MacroParty party) {
        TavernMaster.buyProvisions(this, party, max_min_all);
    }

    public List<Unit> getHeroesForHire() {
        // getHeroJoinConditions(hero){
        // simplified version w/o dialogues - just a list of available heroes
        // with their conditions
        return heroes;
    }

    public void hired(Unit hero) {
        hired(false, MacroManager.getActiveParty(), hero);
    }

    public void hired(boolean merc, MacroParty party, Unit hero) {
        party.getLeader().modifyParameter(PARAMS.GOLD,
         -hero.getIntParam(MACRO_PARAMS.HIRE_COST));
        // TODO shared gold?
        heroes.remove(hero);
        PartyHelper.addMember(hero);
        hero.setOriginalOwner(party.getOwner());
        hero.setOwner(party.getOwner());
        hero.setHidden(false);
        // party.addMember(hero);
    }

    public enum TAVERN_MODIFIER {
        RAVEN_REALM,
        PIRATE,
        UNDERGROUND,
        RAVENGUARD,
        DWARVEN,
        NOBLE,
        WIZARDING,
        MILITARY,
    }

    public enum TOWN_PLACE_TYPE {
        TAVERN, LIBRARY, TEMPLE, QUEST_GIVER,
        FACTION_QUARTER,

        SMITHY, BROTHEL, GUILD, SLAVE_MARKET,
    } // DETERMINES AVAILABLE BUTTONS... pray, whore, take up quest, buy slaves,

}
