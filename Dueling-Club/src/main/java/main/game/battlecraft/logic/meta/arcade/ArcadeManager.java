package main.game.battlecraft.logic.meta.arcade;

import main.client.cc.logic.HeroLevelManager;
import main.client.cc.logic.party.PartyObj;
import main.client.dc.Launcher;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.DungeonEnums;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.content.enums.entity.ItemEnums.QUALITY_LEVEL;
import main.content.enums.rules.ArcadeEnums;
import main.content.enums.rules.ArcadeEnums.ARCADE_REGION;
import main.content.enums.rules.ArcadeEnums.ARCADE_ROUTE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.OrConditions;
import main.elements.conditions.StringComparison;
import main.entity.Ref.KEYS;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.battlecraft.logic.meta.universal.PartyHelper;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_Game.GAME_MODES;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.DC_Formulas;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.entity.FilterMaster;
import main.system.util.Refactor;

import java.util.List;

public class ArcadeManager {
    /*
     * Battlecraft Skirmish Arcades
	 * 
	 *  Simple mode 
	 *  	 *  
	 *  Dungeons to loot - choose 
	 *  Skirmishes to win - not a choice 
	 *  Combining Crawling and Battling in a way
	 *  
	 *  Dungeon Pool 
	 * 	
	 * Skirmishes will happen as you go, basically on certain levels i guess... 
	 * Or maybe time-based? 
	 * 
	 * Old Style dungeons? 
	 * What will be the next playability point?
	 * 
	 * Wave sequences... 
	 * Perhaps the new style can be more about pre-spawning... though it will require behaviors 
	 * 
	 * For skirmishes, it's really more about getting attacked on all sides OR attacking the enemy yourself
	 * 
	 * So maybe the 'dungeons' could just combine the two - first you getOrCreate through the mob,
	 * which crawl from all sides as you go, then there is a space to prepare and attack the boss... 
	 * 
	 * difference from DC vision will be in the focus on combat for now...
	 * 1 or 2 floors 
	 * perhaps with a surface level too...
	 * 
	 * 
	 * 
	 * 
	 *  Enhanced perhaps: 
	 *  >> Objectives/Quests 
	 *  >> Custom battlefields 
	 *  
	 *  Not yet Dungeoncraft - simple dungeons, single level mostly
	 *  No save/load therefore, 
	 *  
	 *  
	 *  
	 * 
	 * 
	 */

    private static final ARCADE_REGION STARTING_REGION = ArcadeEnums.ARCADE_REGION.DUSK_DALE;
    private DC_Game game;

    /*
     * Arcade.xml Current region Completed/remaining dungeons - or perhaps I can
     * store this in Party.xml? And have those as party's properties!
     *
     * * If there are things that are only determined upon entering the dungeon
     * - loot, alt encounters - loading will be an option, but a costly one,
     * perhaps exponentially so!
     *
     * Glory Rating
     *
     * Persistence >> death >> durability
     */
    private Arcade arcade;
    private PartyObj party;
    private Dungeon dungeon;
    private ARCADE_REGION region;
    private LootManager lootManager;
    private ARCADE_ROUTE route;

    public ArcadeManager(DC_Game game) {
        this.game = game;
        lootManager = new LootManager(game);
    }

    public static void constructDungeonLevelForArcade(ObjType chosenType) {

    }

    public static void checkPartyLevelUps() {
        for (Unit hero : PartyHelper.getParty().getMembers()) {
            int xpForLevel = DC_Formulas.getXpForLevel(hero.getLevel());
            if (hero.getIntParam(PARAMS.XP) > xpForLevel) {
                HeroLevelManager.levelUp(hero, null);
            }
        }

    }

    public void newArcadeForParty() {
        // overwrite previous arcade data!
    }

    public void initializeSkirmish(PartyObj party) {
        List<String> fullPool = DataManager.getTypeNames(DC_TYPE.DUNGEONS);

        FilterMaster.filterByProp(fullPool, G_PROPS.GROUP.getName(), StringMaster.ARCADE,
                DC_TYPE.DUNGEONS);

        party.setProperty(G_PROPS.DUNGEONS_PENDING, StringMaster.constructContainer(fullPool), true);
    }

    public void initializeArcade(PartyObj party) {
        arcade = new Arcade();
        this.party = party;
        region = null;
        if (!party.checkProperty(G_PROPS.ARCADE_REGION)
                || !party.checkProperty(G_PROPS.DUNGEONS_PENDING) || checkRegionComplete(party)) {
            nextRegion();
        }
        PartyHelper.savePartyAs(true);
        PartyHelper.getParty().setProperty(PROPS.ARCADE_STATUS, "" + ARCADE_STATUS.PRESTART, true);
        PartyHelper.writeLatestPartyType();
        // party.setProp(shop_level) => use in vendor if not empty!
        // generate quality/material ranges per shop level!
    }

    private boolean checkRegionComplete(PartyObj party) {
        return StringMaster.compareContainersIdentical(party
                .getProperty(G_PROPS.DUNGEONS_COMPLETED), party
                .getProperty(G_PROPS.DUNGEONS_PENDING), true, ";");
    }

    public void loadArcade() {
        // re-load party object from party.xml, samt mit allen helden und stoff!

        // there won't be any "save" option, only *back*

        // hero deaths ... will probably be possible, and so I guess it is a
        // good idea
        // to allow for some adding of *random* companions from the Region's
        // settlement!
    }

    // load/save - gonna need to *save* all party data!

    public void saveArcade() {
        // save to party.xml?
    }

    public void continueArcade() {
        region = new EnumMaster<ARCADE_REGION>().retrieveEnumConst(ARCADE_REGION.class, party
                .getProperty(G_PROPS.ARCADE_REGION));

        // preCheck load? yes - perhaps the party.xml should write something on
        // battle begin and end, so that we know if there was a "disconnect"

    }

    public void nextRegion() {

        if (region == null) {
            region = STARTING_REGION;
            party.setProperty(G_PROPS.ARCADE_REGION, region.toString(), true);
        } else {
            region = region.getNext();
            region = new EnumMaster<ARCADE_REGION>().retrieveEnumConst(ARCADE_REGION.class, party
                    .getProperty(G_PROPS.ARCADE_REGION));
            if (region == null) {
                victory();
            }
        }

        String pool = "";
        // region.getDungeonPool();
        List<String> fullPool = DataManager.getTypesGroupNames(DC_TYPE.DUNGEONS, region
                .toString());

        FilterMaster.filterByProp(fullPool, G_PROPS.GROUP.getName(), StringMaster.ARCADE);
        FilterMaster.filterByProp(fullPool, G_PROPS.DUNGEON_TYPE.getName(), DungeonEnums.DUNGEON_TYPE.BOSS + "",
                true);

        // StringMaster.openContainer(region
        // .getDungeonPool())
        for (int i = 0; i < region.getMinPoolSize(); i++) {
            String dungeonName = fullPool.get(RandomWizard.getRandomListIndex(fullPool));
            pool += dungeonName + ";";
            fullPool.remove(dungeonName);
        }

        List<String> bossPool = DataManager.getTypesGroupNames(DC_TYPE.DUNGEONS, region
                .toString());
        FilterMaster.filterByProp(bossPool, G_PROPS.DUNGEON_TYPE.getName(), DungeonEnums.DUNGEON_TYPE.BOSS
                .toString());
        pool += StringMaster.constructContainer(bossPool);
        party.setProperty(G_PROPS.DUNGEONS_PENDING, pool, true);

    }

    private void victory() {
        PartyHelper.getParty().setProperty(PROPS.ARCADE_STATUS, "" + ARCADE_STATUS.FINISHED, true);

        int place = HallOfFame.getPlace(party);
        DialogMaster.inform("Congratulations, " + StringMaster.getPossessive(party.getName())
                + " BattleCraft Arcade is complete!" + " The Glory you earned amounts to "
                + " which puts you onto " + place + StringMaster.getOrdinalEnding(place)
                + " place among the heroes of Edalar " + HallOfFame.getComment(place));

        Launcher.getMainManager().exitToMainMenu();
        // new VictoryDialogue(party).update(); TODO
        // HallOfFame.checkNomination(party); // ++ difficulty TODO

    }

    public void chooseDungeon() {
        // setView(selection)
        // Launcher.getMainManager().getSequenceMaster()

        route = chooseRoute();
        boolean result = RandomWizard.chance(route.getDanger()); // survival/stealth?

        if (result) {

        } else {
            routeComplete();
        }

    }

    public ARCADE_ROUTE chooseRoute() {
        // at least 2 routes to each dungeon I suppose
        return null;
        // non-Dungeon combat? what's gonna be different?

    }

    public void routeComplete() {
        // game.setMode();
        // update dialog? let getOrCreate back to HC?

        // let choose whether to proceed or turn back - lose time, but possible
        // to resurrect/buy/repair
    }

    @Refactor
    public void dungeonComplete() {
        // award loot

        PartyHelper.getParty().setProperty(PROPS.ARCADE_STATUS, "" + ARCADE_STATUS.IN_PROGRESS,
                true);
        dungeon = game.getDungeonMaster().getDungeonWrapper().getDungeon();
        if (game.getGameMode() == GAME_MODES.ARENA_ARCADE) {
//            game.getArenaArcadeMaster().levelWon();
        } else {
            PartyHelper.levelUp();
        }
        party.addProperty(G_PROPS.DUNGEONS_COMPLETED, dungeon.getName(), true);
        party.setProperty(G_PROPS.GROUP, StringMaster.ARCADE, true);
        try {
            lootManager.awardLoot(party, dungeon);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (dungeon != null) {
            if (dungeon.getProperty(G_PROPS.DUNGEON_TYPE).equalsIgnoreCase(
                    DungeonEnums.DUNGEON_TYPE.BOSS.toString())) {
                if (DialogMaster
                        .confirm("Are you ready to lay down your sword and feast in Valhalla?")) {
                    victory();
                }
            }
        }
        // if (checkRegionComplete(party))
        // nextRegion(); TODO
        // rewardManager.grantReward();
        // checkDeathsInParty(); prompt for resurrect with gold or unused
        // elixirs of rebirth?

        // and if neither is available?
        // does the death count as permanent then?

    }

    public Condition generateMaterialQualityForRegion(ARCADE_REGION region) {
        Conditions c = new Conditions();
        MATERIAL[] materials = new MATERIAL[0];
        QUALITY_LEVEL[] qualities = new QUALITY_LEVEL[0];
        // TODO and what about jewelry?
        switch (region) {
            case DUSK_DALE:
                break;
            case GREYLEAF_WOODS:
                break;
            case WRAITH_MARSHES:
                break;
            case MISTY_SHORES:
                break;
            case BLIGHTSTONE_DESERT:
                qualities = new QUALITY_LEVEL[]{ItemEnums.QUALITY_LEVEL.ANCIENT, ItemEnums.QUALITY_LEVEL.OLD,
                        ItemEnums.QUALITY_LEVEL.MASTERPIECE,};
                materials = new MATERIAL[]{ItemEnums.MATERIAL.ADAMANTIUM};
                break;

        }
        OrConditions mC = new OrConditions();
        OrConditions qC = new OrConditions();
        for (MATERIAL m : materials) {
            mC.add(new StringComparison(StringMaster.getValueRef(KEYS.MATCH, G_PROPS.MATERIAL), m
                    .getName(), true));
        }
        for (QUALITY_LEVEL q : qualities) {
            mC.add(new StringComparison(
                    StringMaster.getValueRef(KEYS.MATCH, G_PROPS.QUALITY_LEVEL), q.getName(), true));
        }
        c.add(mC);
        c.add(qC);
        return c;
    }

    public enum ARCADE_STATUS {
        PRESTART, IN_PROGRESS, FINISHED,
    }

}
