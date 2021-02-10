package macro.entity.faction;

import macro.entity.MacroRef;
import main.content.values.properties.MACRO_PROPS;
import main.system.datatypes.DequeImpl;

import java.util.List;

public class FactionMaster {
    /*
     * relations, mercs and companions, special shops/libraries
     */
    static DequeImpl<FactionObj> factions;
    private static boolean factionsSupported;

    public static FactionObj getFaction(String name) {
        for (FactionObj f : factions) {
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }

    public static void generateFactions(MacroRef ref) {
        boolean allFactions = ref.getGame().getWorld().getProperty(MACRO_PROPS.FACTIONS).isEmpty();

        for (FACTIONS f : FACTIONS.values()) {

        }

    }

    public static List<FACTIONS> getEnemyFactions(FACTIONS f) {
        return null;
    }

    public static boolean isFactionsSupported() {
        return factionsSupported;
    }

    public static void setFactionsSupported(boolean factionsSupported) {
        FactionMaster.factionsSupported = factionsSupported;
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
