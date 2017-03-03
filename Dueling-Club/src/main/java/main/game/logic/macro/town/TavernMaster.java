package main.game.logic.macro.town;

import main.content.enums.entity.HeroEnums;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.MACRO_PROPS;
import main.entity.obj.unit.Unit;
import main.game.logic.macro.MacroManager;
import main.game.logic.macro.global.TimeMaster;
import main.game.logic.macro.party.MacroPartyManager;
import main.game.logic.macro.travel.MacroParty;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;

import java.util.LinkedList;
import java.util.List;

public class TavernMaster {
    /*
     * it's not just "taverns" really, all kinds of sanctuaries and even temples perhaps...
     * maybe there is good reason to have TAVERN OBJ_TYPE
     */
    public static final String DEFAULT_TAVERN = "Raven's Eye";

    public static String generateTavernName(Tavern tavern) {
        List<String> list = new LinkedList<>();
        list.add("Joe's");
        // switch(tavern.getTavernType()){
        // }
        // list.removeAll(presentNames); if (list.isEmpty()) getUniqueName
        String name = new RandomWizard<String>().getRandomListItem(list);
        tavern.setName(name);
        return name;
    }

    public static void buyProvisions(Tavern tavern, MacroParty party,
                                     boolean max_min_all) {
        Integer amount;
        if (max_min_all) {
            amount = tavern.getIntParam(MACRO_PARAMS.FOOD_STORE);
        } else {
            amount = DialogMaster.inputInt();
        }
        if (amount <= 0) {
            return;
        }
        int cost = amount * tavern.getIntParam(MACRO_PARAMS.FOOD_COST);
        if (party.getSharedGold() < cost) {
            // "Well, you only got enough for " _ amount + ", interested?";
            amount = party.getSharedGold()
                    / tavern.getIntParam(MACRO_PARAMS.FOOD_COST);
            cost = amount * tavern.getIntParam(MACRO_PARAMS.FOOD_COST);
        }
        // check(cost, party) return;
        // MathManager.addFactor(amount, goldMod)
        // applyAverageQuality(tavern.getIntParam(MACRO_PARAMS.FOOD_QUALITY));
        MacroPartyManager.reduceSharedGold(party, cost);
        party.modifyParameter(MACRO_PARAMS.C_PROVISIONS, amount);
        // ++ nice doing business with you!
    }

    public static void buyDrinks(Tavern tavern, MacroParty party) {
        /*
         *
		 */
        Boolean result = DialogMaster.askAndWait("I'll buy a drink for...",
                true, "all patrons!", "all of us", "myself");

//        int quality = tavern.getIntParam(MACRO_PARAMS.DRINK_QUALITY);
        List<Unit> drinkers = party.getMembers();
        if (result) {
            tavern.getHeroesForHire();
        } else {
            drinkers = new ListMaster<Unit>().getList(party.getLeader());
        }
        for (Unit member : drinkers) {
//            member.modifyParamByPercent(MACRO_PARAMS.COMBAT_READINESS,
//                    quality - 100);
        }

        // tavern.getIntParam(MACRO_PARAMS.DRINK_QUALITY);
        // tavern.getIntParam(MACRO_PARAMS.DRINK_COST);

        // dialog message
        // reduce battle readiness, increase morale and loyalty (up to a limit)

    }

    public static void rentRooms(Tavern tavern) { // or 'seek sanctuary' in a
        // temple
        Boolean result = DialogMaster.askAndWait(
                "How long will you be needing the rooms?", "24 hours",
                "For...", "Until...");

        Boolean preference = DialogMaster.askAndWait(
                "And... what class do you think you will afford?",
                "We'll be fine with cheap", "Common should do",
                "It better be decent");
        String roomOption; // if has
        int rentDuration = 12;
        // tavern.setProperty(MACRO_PROPS.ROOM_TYPE, roomOption);
        tavern.modifyParameter(MACRO_PARAMS.RENT_DURATION, rentDuration);

        // offer rented rooms when resting in this town?
		/*
         * note that while in town, not all members are 'together'... how to control this?
		 * 
		 * you still getOrCreate to choose what to *do* - study, rest and meditation are better in Rooms
		 */

        // tavern.getIntParam(MACRO_PARAMS.QUIETNESS);
        // tavern.getIntParam(MACRO_PARAMS.MEDITATION_MOD);
        // tavern.getIntParam(MACRO_PARAMS.);

        // choices? One night/while I'm payin' ye/permanent lodging

        // prompt end turn OR choose Macro Modes (via MapView? or add panel to
        // TavernView?)
    }

    public static void brawl(Tavern tavern) {

    }

    public static void entertain(Tavern tavern) {
		/*
         * getOrCreate some gold and raise mood of patrons and even inn-keep himself
		 */
    }

    public static boolean checkCanHire(MacroParty macroParty, Unit hero,
                                       Tavern tavern, Unit e) {
        // TODO principles, party size,
        return macroParty.getMembers().size() <= 4;
    }

    public static int getXpPerTurn(Tavern tavern) {
        return tavern.getIntParam(MACRO_PARAMS.TAVERN_XP_PER_HOUR)
                * TimeMaster.getHoursPerTurn();
    }

    public static Integer getMinimumHeroXp(Tavern tavern) {
        return Math.max(450, getXpPool(tavern) / 5);
    }

	/*
	 * Inn-keeper dialogue: (std obj type) 
	 * buy drinks 
	 * rent rooms
	 * buy
	 */

    public static String generateTavernHeroBackground(Tavern tavern) {
        String backgrounds = "";
        switch (tavern.getModifier()) {
            case RAVEN_REALM: {
                // (var-chance)? could still work as a filter string, and then
                // help with randoms
                backgrounds += HeroEnums.BACKGROUND.MAN_OF_RAVEN_REALM.toString()
                        + "(50);";
                backgrounds += HeroEnums.BACKGROUND.WOMAN_OF_RAVEN_REALM.toString()
                        + "(35);";
                backgrounds += HeroEnums.BACKGROUND.STONESHIELD_DWARF.toString()
                        + "(20);";
                backgrounds += HeroEnums.BACKGROUND.STRANGER.toString() + "(20);";
                backgrounds += HeroEnums.BACKGROUND.ELF.toString() + "(15);";
                backgrounds += HeroEnums.BACKGROUND.DWARF.toString() + "(15);";
                backgrounds += HeroEnums.BACKGROUND.FEY_ELF.toString() + "(15);";
                backgrounds += HeroEnums.BACKGROUND.EASTERLING.toString() + "(10);";
                backgrounds += HeroEnums.BACKGROUND.MAN_OF_WOLF_REALM.toString()
                        + "(20);";
                backgrounds += HeroEnums.BACKGROUND.WOMAN_OF_WOLF_REALM.toString()
                        + "(15);";
                backgrounds += HeroEnums.BACKGROUND.MAN_OF_KINGS_REALM.toString()
                        + "(15);";
                backgrounds += HeroEnums.BACKGROUND.WOMAN_OF_KINGS_REALM.toString()
                        + "(12);";
                backgrounds += HeroEnums.BACKGROUND.MAN_OF_EAGLE_REALM.toString()
                        + "(10);";
                backgrounds += HeroEnums.BACKGROUND.WOMAN_OF_EAGLE_REALM.toString()
                        + "(7);";
                backgrounds += HeroEnums.BACKGROUND.WOMAN_OF_KINGS_REALM.toString()
                        + "(15);";
                backgrounds += HeroEnums.BACKGROUND.WOLFSBANE_DWARF.toString() + "(5);";

                break;
            }
        }
        tavern.setProperty(MACRO_PROPS.HERO_BACKGROUNDS, backgrounds, true);
        return backgrounds;

    }

    public static int getXpPool(Tavern tavern) {
        return 10
                * // test
                MacroManager.getCampaign().getIntParam(
                        MACRO_PARAMS.HOURS_ELAPSED)
                * tavern.getIntParam(MACRO_PARAMS.TAVERN_XP_PER_HOUR)
                + tavern.getIntParam(MACRO_PARAMS.HERO_POWER_POOL);
    }
}
