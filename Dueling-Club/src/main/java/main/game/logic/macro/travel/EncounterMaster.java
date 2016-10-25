package main.game.logic.macro.travel;

import main.client.battle.Wave;
import main.client.dc.Launcher;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.parameters.MACRO_PARAMS;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.logic.dungeon.Dungeon;
import main.game.logic.macro.MacroManager;
import main.game.logic.macro.MacroRef;
import main.game.logic.macro.map.Area;
import main.game.logic.macro.map.Route;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EncounterMaster {

    private static boolean encounterBeingResolved;
    private static FACING_DIRECTION playerBfSide;

    private static boolean flee(Encounter e) {
        // get fastest wave in encounter -> re-arrange waves and init battle
        // perhaps the only way a battle is joined is if the party is
        // surrounded...
        // check more encounters - perhaps with luck you can run into even more
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
//		while (true) {
//			// mini-message with progress updates
//
//			if (distance < x)
//				return false;
//			TimeMaster.hoursPassed(1);
//
//			Integer speed = e.getDefendingParty().getIntParam(MACRO_PARAMS.TRAVEL_SPEED);
//			distance += speed;
//			for (MacroGroup group : e.getGroups()) {
//				group.getTravelSpeed();
//				group.chase();
//				if (distance - group.getChaseDistance() > surroundAdvantage) {
//
//				}
//				if (distance > x)
//					e.getGroups().remove(group);
//			}
//			e.getGroups().addAll(checkEncounterGroups(e.getDefendingParty(), 1));
//			// check coordinates - surround?
//			if (e.getGroups().isEmpty())
//				return true;
//		}
        return false;
    }

    private static boolean parlay(Encounter e) {
        // e.getWaves();
        for (Wave wave : e.getWaves()) {
            // set owner? remove from encounter? set in front? delay?
        }
        // middle outcome - persuaded some waves to stay away

        // bribe, intimidate, taunt, ++ converse/chat/blather

        Boolean choice = DialogMaster.askAndWait(getEncounterDescription(e), true, "Bribe",
                "Intimidate", "Taunt");
        if (choice == null) {
            return taunt(e);
        } else {
            if (!choice)
                return intimidate(e);
            return bribe(e);
        }
    }

    private static boolean bribe(Encounter e) {
        for (Wave wave : e.getWaves()) {
            // set owner? remove from encounter? set in front? delay?
        }
        return false;
    }

    private static boolean intimidate(Encounter e) {
        // TODO Auto-generated method stub
        return false;
    }

    private static boolean taunt(Encounter e) {
        // TODO morale? focus? rage counters?
        return false;
    }

    public static boolean resolveEncounter(Encounter e) {
        // return outcome, what for? Fled, Surrendered
        Boolean choice = DialogMaster.askAndWait(getEncounterDescription(e), true, "Fight", "Flee",
                "Parlay");
        boolean result = false;
        if (choice == null) {
            result = parlay(e);
        } else {
            if (!choice)
                result = flee(e);
        }
        if (result)
            return true;

        encounterBeingResolved = true;
        // save macro game
        // DC_Game.game.getBattleManager().setEncounter(e);
        // DC_Game.game.getBattleManager().setEncounter(true);
        DC_Game game = DC_Game.game;
        // game.setPlayerParty(e.getDefendingParty().getMicroParty());
        game.getDungeonMaster().initEncounterDungeon(e);
        if (!game.isBattleInit())
            game.battleInit();
        game.getBattleManager().setEncounter(e);
        game.getArenaManager().getBattleConstructor().setEncounterSequence(getWaveSequence(e));
        Launcher.launchDC(e.getDefendingParty().getName(), false);
        boolean outcome = (boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.BATTLE_FINISHED);
        encounterBeingResolved = false;
        game.getBattleManager().setEncounter(null);
        return outcome;
    }

    private static Map<Wave, Integer> getWaveSequence(Encounter e) {
        Map<Wave, Integer> waves = new XLinkedMap<>();
        int i = 0;
        for (String typeName : StringMaster.openContainer(e.getTypeNames())) {
            ObjType waveType = DataManager.getType(typeName, OBJ_TYPES.ENCOUNTERS);
            Wave wave = new Wave(waveType, DC_Game.game, new Ref(), DC_Game.game.getPlayer(false));
            wave.initUnitMap(); // TODO is the field ready for coordinates?
            int j = i
                    + DC_Game.game.getArenaManager().getBattleConstructor().getRoundsToFight(
                    waveType);
            Integer round = RandomWizard.getRandomIntBetween(i, j);
            i += round;
            waves.put(wave, round);
        }
        return waves;
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
        List<MacroGroup> waves = new LinkedList<>();
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
        // // 1) check random (collision) or ambushes
        // // 2) check stealth
        // waves.add(e);
        //
        // // apparently, we want to pre-initialize 'lurking enemies'!
        // }
        if (waves.isEmpty())
            return null;
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
        for (String wave : StringMaster.openContainer(e.getTypeNames())) {
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
        if (min_max_normal != null)
            prop = min_max_normal ? PROPS.SHRUNK_PRESET_GROUP : PROPS.EXTENDED_PRESET_GROUP;
        List<String> list = StringMaster.openContainer(type.getProperty(prop));
        return getPower(list);
    }

    public static Integer getPower(List<String> list) {
        Integer power = 0;
        for (String unit : list) {
            ObjType objType = DataManager.getType(unit, OBJ_TYPES.UNITS);
            if (objType != null)
                power += objType.getIntParam(PARAMS.POWER);
        }
        return power;
    }

    public static int getMinCreepWavePower() {
        return MacroManager.getCampaign().getIntParam(MACRO_PARAMS.CREEP_POWER_BASE)
                + MacroManager.getCampaign().getIntParam(MACRO_PARAMS.HOURS_ELAPSED)
                * MacroManager.getCampaign().getIntParam(MACRO_PARAMS.CREEP_POWER_PER_HOUR);
    }

    public static int getMaxCreepWavePower() {
        return MacroManager.getCampaign().getIntParam(MACRO_PARAMS.CREEP_POWER_MAX_FACTOR)
                * (MacroManager.getCampaign().getIntParam(MACRO_PARAMS.CREEP_POWER_BASE) + MacroManager
                .getCampaign().getIntParam(MACRO_PARAMS.HOURS_ELAPSED)
                * MacroManager.getCampaign().getIntParam(MACRO_PARAMS.CREEP_POWER_PER_HOUR));
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
