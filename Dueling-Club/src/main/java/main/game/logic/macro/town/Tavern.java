package main.game.logic.macro.town;

import main.client.battle.arcade.PartyManager;
import main.client.cc.gui.neo.choice.ChoiceSequence;
import main.client.cc.gui.neo.choice.TavernView;
import main.client.cc.logic.party.PartyObj;
import main.client.dc.HC_SequenceMaster;
import main.client.dc.Launcher;
import main.client.dc.Launcher.VIEWS;
import main.content.CONTENT_CONSTS.BACKGROUND;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.parameters.MACRO_PARAMS;
import main.content.properties.G_PROPS;
import main.content.properties.MACRO_PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.game.logic.macro.MacroGame;
import main.game.logic.macro.MacroManager;
import main.game.logic.macro.global.TimeMaster;
import main.game.logic.macro.travel.MacroParty;
import main.game.logic.macro.utils.HeroGenerator;
import main.system.FilterMaster;
import main.system.auxiliary.*;
import main.system.auxiliary.LogMaster.LOG_CHANNELS;

import java.util.LinkedList;
import java.util.List;

public class Tavern extends TownPlace {
    private TOWN_PLACE_TYPE TYPE;
    private List<DC_HeroObj> heroes = new LinkedList<>();
    // buy drinks...
    private List<DC_HeroObj> mercs;
    private List<PartyObj> stayingParties;
    private TAVERN_MODIFIER modifier;
    public Tavern(MacroGame game, ObjType type, Ref ref) {
        super(game, type, ref);
        if (!MacroManager.isEditMode())
            generateHeroes();
    }

    public TOWN_PLACE_TYPE getTYPE() {
        if (TYPE == null)
            TYPE = new EnumMaster<TOWN_PLACE_TYPE>().retrieveEnumConst(
                    TOWN_PLACE_TYPE.class,
                    getProperty(MACRO_PROPS.TOWN_PLACE_TYPE));
        return TYPE;
    }

    public TAVERN_MODIFIER getModifier() {
        if (modifier == null)
            modifier = new EnumMaster<TAVERN_MODIFIER>().retrieveEnumConst(
                    TAVERN_MODIFIER.class,
                    getProperty(MACRO_PROPS.TOWN_PLACE_MODIFIER));
        return modifier;
    }

    public void generateHeroes() {
        // special campaign heroes
        // getTown().getFactions();

        int total = TavernMaster.getXpPool(this);
        setParam(MACRO_PARAMS.C_HERO_POWER_POOL, total);
        Integer number = getIntParam(MACRO_PARAMS.PREFERRED_HERO_NUMBER); // min/max
        if (number == 0)
            number = 4;
        while (true) {
            int xp = total / number * RandomWizard.getRandomIntBetween(40, 60)
                    / 50;
            if (xp < TavernMaster.getMinimumHeroXp(this)) {
                if (total < TavernMaster.getMinimumHeroXp(this))
                    break;
                xp = TavernMaster.getMinimumHeroXp(this);
                total -= xp;
                number--;
            }
            boolean background = false;
            // RandomWizard.random();TODO
            if (!newHero(background, xp))
                break;
            if (number <= heroes.size())
                break;
        }
    }

    public boolean newHero(boolean background, int xp) {
        // chance
        DC_HeroObj hero = null;
        if (!background) {
            ObjType type = getRandomHeroType(xp);
            if (type == null)
                return false;
            hero = new DC_HeroObj(type);
            initHeroHireParams(hero);
            HeroGenerator.alterHero(hero);
        } else {
            ObjType type = getRandomHeroBackground();
            hero = generateHeroFromBackground(type, xp);
        }
        hero.setHidden(true);
        if (hero == null)
            return false;

        if (!addHero(hero))
            return false;

        return true;
    }

    private DC_HeroObj generateHeroFromBackground(ObjType type, int xp) {
        DC_HeroObj hero = HeroGenerator.generateHero(type, xp, this);
        initHeroHireParams(hero); // random?
        return hero;
    }

    private void initHeroHireParams(DC_HeroObj hero) {
        int cost = hero.calculatePower() * 5; // TODO
        hero.setParam(MACRO_PARAMS.HIRE_COST, cost, true);
        int share = 100;
        hero.setParam(MACRO_PARAMS.GOLD_SHARE, share, true);
    }

    private boolean addHero(DC_HeroObj hero) {
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
        LogMaster.log(LOG_CHANNELS.MACRO_DYNAMICS, getName() + " now has "
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
        List<ObjType> list = DataManager.getTypesGroup(OBJ_TYPES.CHARS,
                StringMaster.BACKGROUND);
        String prop = new RandomWizard<BACKGROUND>().getObjectByWeight(
                getHeroBackgrounds(), BACKGROUND.class).toString();

        FilterMaster.filter(list, G_PROPS.BACKGROUND.getName(), prop,
                OBJ_TYPES.CHARS, true, false, false);
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
                        .getTypesSubGroupNames(OBJ_TYPES.CHARS, StringMaster.PRESET),
                OBJ_TYPES.CHARS);
        // DataManager.getTypesSubGroup(OBJ_TYPES.CHARS,
        // StringMaster.PRESET);
        // background allowed
        FilterMaster.filterByParam(list, PARAMS.TOTAL_XP, minXp,
                OBJ_TYPES.CHARS, true);
        FilterMaster.filterByParam(list, PARAMS.TOTAL_XP, maxXp,
                OBJ_TYPES.CHARS, false);

        Loop.startLoop(25);
        while (!Loop.loopEnded()) {
            LinkedList<ObjType> bufferList = new LinkedList<>(list);
            String prop = new RandomWizard<BACKGROUND>().getObjectByWeight(
                    getHeroBackgrounds(), BACKGROUND.class).toString();
            FilterMaster.filter(bufferList, G_PROPS.BACKGROUND.getName(),
                    prop, OBJ_TYPES.CHARS, true, false, false);
            if (!bufferList.isEmpty()) {
                list = bufferList;
                break;
            }
        }
        if (list.isEmpty())
            return null;
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

    public List<DC_HeroObj> getHeroesForHire() {
        // getHeroJoinConditions(hero){
        // simplified version w/o dialogues - just a list of available heroes
        // with their conditions
        return heroes;
    }

    public void hired(DC_HeroObj hero) {
        hired(false, MacroManager.getActiveParty(), hero);
    }

    public void hired(boolean merc, MacroParty party, DC_HeroObj hero) {
        party.getLeader().modifyParameter(PARAMS.GOLD,
                -hero.getIntParam(MACRO_PARAMS.HIRE_COST));
        // TODO shared gold?
        heroes.remove(hero);
        PartyManager.addMember(hero);
        hero.setOriginalOwner(party.getOwner());
        hero.setOwner(party.getOwner());
        hero.setHidden(false);
        // party.addMember(hero);
    }

    public ChoiceSequence openView() {
        generateHeroes();
        HC_SequenceMaster sm = new HC_SequenceMaster() {
            public void doneSelection() {
                getSequence().getValue();
                DC_HeroObj hero = (DC_HeroObj) getSequence().getResults()
                        .get(0);
                hired(hero);
                Launcher.resetView(VIEWS.HC);
            }

            ;

            @Override
            public void cancelSelection() {
                Launcher.resetView(VIEWS.HC);
            }
        };
        // toggle between heroes and mercs?
        // mini-dialogue upon selection or 'approach'?
        ChoiceSequence cs = new ChoiceSequence();
        cs.setManager(sm);
        cs.addView(new TavernView(this, cs));
        cs.start();
        return cs;

    }

    public void rentRooms(Boolean waitForInput) {
        // TODO Auto-generated method stub

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
        INN, GUILD, SLAVE_MARKET, FACTION_QUARTER, TEMPLE, BROTHEL,
    } // DETERMINES AVAILABLE BUTTONS... pray, whore, take up quest, buy slaves,

    public enum HERO_GROUPS {
        // per class, per faction, per race
    }

    public enum PRESET_HEROES {
        // or should there be some group tag for ObjTypes?
    }

}
