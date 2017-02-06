package main.client.battle.arcade;

import main.client.cc.logic.party.PartyObj;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.data.DataManager;
import main.swing.generic.components.G_Panel;
import main.system.SortMaster;
import main.system.auxiliary.StringMaster;

import java.util.List;

public class HallOfFame {
    /*
	 * where to store data? Perhaps each arcade-party should have its 'victory
	 * data' while here, we'll be creating component based on their data!
	 * 
	 * should be 'browsable' from main menu!
	 */

    private static List<String> getSortedArcadeParties() {
        // TODO filter the *finishers*
        return SortMaster.sortByValue(DataManager.getTypesSubGroupNames(
                OBJ_TYPES.PARTY, StringMaster.ARCADE), PARAMS.GLORY,
                OBJ_TYPES.PARTY, false);
    }

    public static int getPlace(PartyObj party) {
        return DataManager.toTypeList(getSortedArcadeParties(),
                OBJ_TYPES.PARTY).indexOf(party) + 1;
    }

    public static String getComment(int place) {
        if (place < 1) {
            return "!! But for how long?..";
        }
        if (place < 2) {
            return "! A great honor indeed!";
        }
        if (place < 3) {
            return "! Who would have thought?";
        }
        if (place < 5) {
            return "! Quite Remarkable...";
        }
        if (place < 8) {
            return "! Remarkable...";
        }
        if (place < 12) {
            return ". Some may yet toast to your deeds...";
        }
        if (place < 16) {
            return "... Hardly worth mentioning.";
        }
        if (place < 25) {
            return "... You should be back.";
        }
        return "... No comment";
    }

    public void nominate(PartyObj party) {
        /**
         * equate to some unit from same aspect as main hero... calculate
         * statistics for each hero and party as a whole... calculate deaths and
         * kills!
         */

    }

    public G_Panel getComponent() {
        List<String> sortedParties = getSortedArcadeParties();
        return null;
    }

}
