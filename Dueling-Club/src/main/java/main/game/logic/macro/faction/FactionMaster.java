package main.game.logic.macro.faction;

import main.content.properties.MACRO_PROPS;
import main.game.logic.macro.MacroRef;
import main.system.datatypes.DequeImpl;

import java.util.List;

public class FactionMaster {
    /*
     * relations, mercs and companions, special shops/libraries
     */
    static DequeImpl<Faction> factions;
    private static boolean allFactions;

    public static Faction getFaction(String name) {
        for (Faction f : factions) {
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }

    public static void generateFactions(MacroRef ref) {
        allFactions = ref.getGame().getWorld().getProperty(MACRO_PROPS.FACTIONS).isEmpty();

        for (FACTIONS f : FACTIONS.values()) {

        }

    }

    public static List<FACTIONS> getEnemyFactions(FACTIONS f) {
return null ;
    }

    public enum FACTIONS {
        BARON_ULANOR, BARON_MOGRAVE, BARON_GARETH, BARON_ORSON,

        RED_DAWN_CRUSADERS, // CITADEL OF LIGHT
        TWILIGHT_ORDER,
        CONGREGATION,
        BROTHERHOOD_OF_STRIX,

        PIRATE_SYNDICATE, // BLACK SABRES

        GREY_ELVES, // COUNCIL
        HIGH_ELVES,

        INDEPENDENT,

        // ++ DEITY FACTIONS?

    }

    public enum LESSER_FACTIONS {
        THIEVES_GUILD, RAVENGUARD_ULANOR,

        STONESHIELD_CLAN,

        PALE_ORCS,

    }

}
