package main.game.battlecraft.logic.dungeon;

import main.client.cc.logic.UnitLevelManager;
import main.client.cc.logic.party.PartyObj;
import main.client.dc.Launcher;
import main.content.C_OBJ_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.Ref;
import main.entity.obj.MicroObj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.DC_Player;
import main.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.battlecraft.logic.dungeon.UnitData.PARTY_VALUE;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.core.game.DC_Game.GAME_MODES;
import main.libgdx.bf.BFDataCreatedEvent;
import main.system.GuiEventManager;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.test.TestMasterContent;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.test.Refactor;

import java.util.List;

import static main.system.GuiEventType.BF_CREATED;


public class Spawner<E extends DungeonWrapper> extends DungeonHandler<E> {
    public static final Integer MAX_SPACE_PERC_CREEPS = 25; // 1 per cell only
    private static final Integer MAX_SPACE_PERC_PARTY = 0;
    //    public Spawner(String unitData, DC_Player player, SPAWN_MODE mode) {
    boolean coordinatesSet;

    public Spawner(DungeonMaster master) {
        super(master);
    }

    public static void setEnemyUnitGroupMode(boolean b) {

    }

    public static UnitData generateData(String dataString) {
        return generateData(dataString, null, null, null);
    }

    public static UnitData generateData(String dataString,
                                        DC_Player player,
                                        Coordinates spawnAt,
                                        Positioner positioner) {
        String units = "";
        String coordinates = "";
        String data = "";

        for (String substring : StringMaster.openContainer(dataString)) {
            if (dataString.contains("=")) {
                coordinates += substring.split("=")[0];
                units += substring.split("=")[1];
            } else if (dataString.contains("(") && dataString.contains(")")) {
                units += VariableManager.removeVarPart(substring);
                coordinates += VariableManager.getVar(substring);
            } else
                units += substring;
        }

        if (positioner != null)
            if (coordinates.isEmpty()) {
//                List<Coordinates> coordinatesList =
//                 positioner.getCoordinates(player, spawnAt, units);
//                coordinates = StringMaster.joinStringList(
//                 StringMaster.convertToStringList(coordinatesList), ",");
            }
        if (!coordinates.isEmpty())
            data += PARTY_VALUE.COORDINATES + "=" + coordinates + ";";
        data += PARTY_VALUE.MEMBERS + "=" + units + ";";
        return new UnitData(data);
    }

    @Refactor
    public void spawn() {
//        List<MicroObj> units = DC_ObjInitializer.createUnits(player, unitData);
//        initFacing(units, player, mode);
        for (Object player1 : getBattleMaster().getPlayerManager().getPlayers()) {

            DC_Player player = (DC_Player) player1;
            UnitData data = player.getUnitData();
            if (data == null)
                data = generateData("", player, null);
            spawn(data, player);
        }
        final Integer cellsX = game.getDungeon().getCellsX();
        final Integer cellsY = game.getDungeon().getCellsY();
        GuiEventManager.trigger(BF_CREATED,
                new BFDataCreatedEvent(cellsX, cellsY, game.getBfObjects()));

        WaitMaster.waitForInput(WAIT_OPERATIONS.GDX_READY);

        //initEmblem

    }

    public UnitData generateData(String dataString, DC_Player player,
                                 Coordinates spawnAt) {
        return generateData(dataString,
                player,
                spawnAt, getPositioner());
    }

    public void spawn(UnitData data, DC_Player owner) {
        int i = 0;
        if (owner == null)
            owner = getPlayerManager().getPlayer(data.getContainerValue(PARTY_VALUE.PLAYER_NAME, i));
        List<String> types = data.getContainerValues(PARTY_VALUE.MEMBERS);
        while (true) {
            if (i == types.size())
                return;
            String facing = data.getContainerValue(PARTY_VALUE.FACING, i);
            String coordinates = data.getContainerValue(PARTY_VALUE.COORDINATES, i);
            String level = data.getContainerValue(PARTY_VALUE.LEVEL, i);
            String type = data.getContainerValue(PARTY_VALUE.MEMBERS, i);
            i++;
            spawn(type, coordinates, owner, facing, level);
        }
//     coordinate

        //facing

    }

    private void spawn(String typeName, String coordinates, DC_Player owner,
                       String facing, String level) {
        FACING_DIRECTION facing_direction = FacingMaster.getFacing(facing);
        Coordinates c = new Coordinates(coordinates);
        ObjType type = DataManager.getType(typeName, C_OBJ_TYPE.UNITS_CHARS);
        //TODO chars or units?!
        int levelUps = StringMaster.getInteger(level);
        if (levelUps > 0) {
            new UnitLevelManager().getLeveledType(type, levelUps);
        }

        game.getManager().getObjCreator().createUnit(type, c.x, c.y, owner, new Ref(game));
    }

    public void spawnParty(Coordinates origin, Boolean me, ObjType party) {
        // from entrances, from sides, by default, by event, by test - useful!
        // positioner.getCoordinatesForUnitGroup(presetGroupTypes, wave);
        List<String> partyTypes = StringMaster.openContainer(party.getProperty(PROPS.MEMBERS));
        List<Coordinates> c = getPositioner().getPartyCoordinates(origin, me, partyTypes);
        String partyData = DC_ObjInitializer.getObj_CoordinateString(partyTypes, c);
        spawnParty(me, partyData);
    }

    public void spawnParties() {
        try {
            spawnParty(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            spawnParty(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void spawnParty(boolean me) {
        spawnParty(me, null);
    }

    public void spawnUnitsAt(List<Unit> units, Coordinates coordinates) {
        List<String> partyTypes = StringMaster.toNameList(units);
        List<Coordinates> coordinateList = getPositioner()
                .initPartyCoordinates(partyTypes, null);
        int index = 0;
        for (Unit m : units) {
            m.setCoordinates(coordinateList.get(index));
            index++;
        }
    }

    public void spawnPartyAt(PartyObj party, Coordinates coordinates) {
        spawnUnitsAt(party.getMembers(), coordinates);
    }

    @Deprecated
    public void spawnParty(boolean me, String partyData) {

        DC_Player player = game.getPlayer(me);
        if (BooleanMaster.isTrue(me)) {
            PartyObj party = getGame().getMetaMaster().getPartyManager().getParty();
            if (party != null) {
                SoundMaster.playEffectSound(SOUNDS.READY, party.getLeader());
                spawnPlayerParty(party, partyData);
                return;
            }

        }
        if (!partyData.contains(DC_ObjInitializer.COORDINATES_OBJ_SEPARATOR)) {
            partyData = DC_ObjInitializer.convertVarStringToObjCoordinates(partyData);
        }
        List<MicroObj> list = DC_ObjInitializer.processUnitDataString(player, partyData, game);
        if (!ListMaster.isNotEmpty(list)) {
            return;
        }


    }


    private void spawnPlayerParty(PartyObj party, String partyData) {

        DC_ObjInitializer.initializePartyPositions(partyData, party.getMembers());
        int i = 0;
        getPositioner().setMaxSpacePercentageTaken(MAX_SPACE_PERC_PARTY);
        Boolean last = null;
        for (Unit hero : party.getMembers()) {

            if (party.getPartyCoordinates() == null) {
                if (
                        getGame().getGameMode() == GAME_MODES.ARENA ||
                                getGame().getGameMode() == GAME_MODES.ARENA_ARCADE) {
                    hero.setFacing(FacingMaster.getPresetFacing(true));
                }
//                else
//                    hero.setFacing(getPositioner().getPartyMemberFacing(hero.getCoordinates()));
            }

            hero.setOriginalOwner(game.getPlayer(true));
            i++;
            if (i == party.getMembers().size()) {
                last = true;
            }
            if (game.isDebugMode()) {
                TestMasterContent.addTestItems(hero.getType(), last);
            }
            last = false;
            if (!Launcher.isRunning()) {
                continue;
            }
            hero.setSpells(null);
            hero.initSpells(false);
            hero.fullReset(game);

        }
//        game.getPlayer(true).setEmblem(party.getLeader().getEmblem().getImage());
    }

    public void spawnWave(String typeName, DC_Player player, Coordinates coordinate) {
    }


    public enum FACING_TEMPLATE {
        TOWARDS_CENTER,
        OUTWARD_FROM_ORIGIN,
        TOWARDS_PLAYER_HERO,
        OPTIMAL_TOWARDS_ENEMIES,
        RANDOM,

    }

    public enum POSITIONING_MODE {
        ROWS_AT_SIDE,
        LAYERS_AROUND_COORDINATE,

    }

    //after-spawn actions -
    public enum SPAWN_MODE {
        UNIT_GROUP,
        PRESET,
        HARDCODED,
        PARTY,
        WAVE,
        DUNGEON,
        SCRIPT,
    }
}
