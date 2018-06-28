package eidolons.game.module.adventure.map.travel.encounter;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.macro.MacroInitializer;
import eidolons.macro.entity.MacroRef;
import eidolons.macro.entity.party.MacroParty;
import eidolons.macro.map.Route;
import eidolons.macro.map.area.Area;
import eidolons.macro.map.area.MacroGroup;
import main.content.DC_TYPE;
import main.content.values.parameters.MACRO_PARAMS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

public class EncounterMaster {

    private static boolean encounterBeingResolved;
    private static FACING_DIRECTION playerBfSide;

    private static boolean flee(Encounter e) {
        // getOrCreate fastest wave in encounter -> re-arrange waves and init battle
        // perhaps the only way a battle is joined is if the party is
        // surrounded...
        // preCheck more encounters - perhaps with luck you can run into even more
        // enemies...
        // DialogWizard.newMiniDialogue("Enemies have you surrounded, battle is now inevitable...",
        // true, "Curse our luck!", "Death has come for us...", "To Arms!");

        if (!checkEscape(e)) {
            e.setSurrounded(true);
            return false;
        }

        // e.getWaves()

        return true;
    }

    private static boolean checkEscape(Encounter e) {
        int distance = 0;
        int headstart = 10;

        return false;
    }



    public static void newTurn() {
        // add lurkers
        // per area and route
        // set random ambush point... within distance from last
    }

    public static Encounter checkEncounter(MacroParty party, int progress) {
        return checkEncounter(party.getArea(), party.getCurrentRoute(), party, false, progress);
    }

    public static Encounter checkEncounter(MacroParty party, int progress, boolean explore) {
        return checkEncounter(party.getArea(), party.getCurrentRoute(), party, explore, progress);
    }

    /**
     * @param progress Route Progress that will be achieved if party travels full
     *                 time freely
     * @return
     */
    public static Encounter checkEncounter(Area area, Route route, MacroParty party,
                                           boolean explore, int progress) {

        List<MacroGroup> encounters = area.getGroups();

        Integer c_progress = party.getIntParam(MACRO_PARAMS.ROUTE_PROGRESS_PERCENTAGE);
        int collisionPoint = -1; // TODO ??? for each group... getCoordinates()?
        List<MacroGroup> waves = new ArrayList<>();
        for (MacroGroup e : encounters) {
            // if (e.getCoordinates() )closest point... find shortest tangent

            int creepPoint = RandomWizard.getRandomInt(100); // first collision?
            collisionPoint = RandomWizard.getRandomInt(100); // first collision?

            int GAP = 10;

            if (collisionPoint != -1) {
                creepPoint = collisionPoint + creepPoint - 50;
            }
            boolean collision = Math.abs(collisionPoint - progress) < GAP;
            // roll stealth TODO
            // party.getIntParam(PARAMS.STEALTH);
            if (collision) {
                waves.add(e);
                collisionPoint = creepPoint;
            }

            // resolving one encounter does not mean free travel afterward
        }
        // TODO AMBUSHES substite @progress for @collisionPoint
        // encounters = (route.getAmbushingGroups());
        // int ambushPoint = route.getIntParam(MACRO_PARAMS.CREEP_AMBUSH_POINT);
        // // pre-random!
        //
        // boolean ambushed = c_progress < ambushPoint
        // && ambushPoint < c_progress + progress;
        // for (String e : encounters) {
        // // so each encounter string should already be initialized? I could
        // // use ObjTypes...
        //
        // // 1) preCheck random (collision) or ambushes
        // // 2) preCheck stealth
        // waves.add(e);
        //
        // // apparently, we want to pre-initialize 'lurking enemies'!
        // }
        if (waves.isEmpty()) {
            return null;
        }
        Encounter encounter = new Encounter(route, party, (waves));

        encounter.setProgress(collisionPoint); // TODO
        return encounter;
    }

    private static String getEncounterDescription(Encounter e) {
        // scout the enemies
        // boolean ambush = e.isAmbush(); // explore?
        // String string="It appears you have been ambushed!";
        // String string="It appears you have been ambushed!";
        String string = "It appears you have run into hostiles...";
        String enemies = " Among the spotted enemies: \n";
        for (String wave : StringMaster.open(e.getTypeNames())) {
            enemies += wave + ", ";
        }
        enemies = StringMaster.cropLast(enemies, 2);
        return string + enemies;
    }

    public static Integer getMinimumPower(ObjType type) {
        return getPower(type, true);
    }

    public static Integer getPower(ObjType type, Boolean min_max_normal) {

        PROPS prop = PROPS.PRESET_GROUP;
        if (min_max_normal != null) {
            prop = min_max_normal ? PROPS.SHRUNK_PRESET_GROUP : PROPS.EXTENDED_PRESET_GROUP;
        }
        List<String> list = StringMaster.openContainer(type.getProperty(prop));
        return getPower(list);
    }

    public static Integer getPower(List<String> list) {
        Integer power = 0;
        for (String unit : list) {
            ObjType objType = DataManager.getType(unit, DC_TYPE.UNITS);
            if (objType != null) {
                power += objType.getIntParam(PARAMS.POWER);
            }
        }
        return power;
    }

    public static int getMinCreepWavePower() {
        return Integer.MIN_VALUE;
//        return MacroInitializer.getCampaign().getIntParam(MACRO_PARAMS.CREEP_POWER_BASE)
//         + MacroInitializer.getCampaign().getIntParam(MACRO_PARAMS.HOURS_ELAPSED)
//         * MacroInitializer.getCampaign().getIntParam(MACRO_PARAMS.CREEP_POWER_PER_HOUR);
    }

    public static int getMaxCreepWavePower() {
        return Integer.MAX_VALUE;
//        return MacroInitializer.getCampaign().getIntParam(MACRO_PARAMS.CREEP_POWER_MAX_FACTOR)
//         * (MacroInitializer.getCampaign().getIntParam(MACRO_PARAMS.CREEP_POWER_BASE) + MacroInitializer
//         .getCampaign().getIntParam(MACRO_PARAMS.HOURS_ELAPSED)
//         * MacroInitializer.getCampaign().getIntParam(MACRO_PARAMS.CREEP_POWER_PER_HOUR));
    }

    public static boolean isEncounterBeingResolved() {
        return encounterBeingResolved;
    }

    public static FACING_DIRECTION getPlayerBfSide() {
        return playerBfSide;
    }

    public static ObjType getSubstituteEncounterType(ObjType waveType, Dungeon dungeon,
                                                     int preferredPower) {
        // TODO Auto-generated method stub
        return null;
    }

    // to be invoked multiple times between threats?
    public Encounter checkEncounter(MacroRef ref, boolean explore, int progress) {
        MacroParty party = ref.getParty();
        Route route = ref.getRoute();
        Area area = route.getArea();
        return checkEncounter(area, route, party, explore, progress);
    }

}
