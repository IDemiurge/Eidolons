package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.ability.UnitTrainingMaster;
import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.test.UnitGroupMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData.PARTY_VALUE;
import eidolons.game.battlecraft.logic.meta.universal.PartyHelper;
import eidolons.game.core.game.DC_Game.GAME_MODES;
import eidolons.game.core.launch.LaunchDataKeeper;
import eidolons.game.module.herocreator.logic.UnitLevelManager;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.libgdx.bf.BFDataCreatedEvent;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.test.TestMasterContent;
import main.content.C_OBJ_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.MicroObj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.secondary.Bools;
import main.system.graphics.GuiManager;
import main.system.math.MathMaster;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.util.Refactor;

import java.util.ArrayList;
import java.util.List;

import static main.system.GuiEventType.SCREEN_LOADED;


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

    @Refactor
    public void spawn() {
        for (Object player1 : getBattleMaster().getPlayerManager().getPlayers()) {

            DC_Player player = (DC_Player) player1;
            if (player.isNeutral()) {
                continue;
            }
            UnitsData data = player.getUnitData();
            if (data == null)
                data = generateData("", player, null);
            spawn(data, player, getSpawnMode(player, true));
        }
        spawnDone();

    }

    protected void spawnDone() {
//       TODO selective??
// getGame().getMetaMaster().getPartyManager().getParty().getMembers()
        List<Unit> unitsList = new ArrayList<>();
        unitsList.addAll(game.getUnits());
        getFacingAdjuster().adjustFacing(unitsList);

        final Integer cellsX =GuiManager.getCurrentLevelCellsX() ;
        final Integer cellsY = GuiManager.getCurrentLevelCellsY();
        GuiEventManager.trigger(SCREEN_LOADED,
         new BFDataCreatedEvent(cellsX, cellsY, game.getBfObjects()));

        //WaitMaster.waitForInput(WAIT_OPERATIONS.DUNGEON_SCREEN_READY);
    }

    private SPAWN_MODE getSpawnMode(DC_Player player, boolean first) {
        if (player.isMe())
            return SPAWN_MODE.PARTY;
        return SPAWN_MODE.UNIT_GROUP;
    }

    public UnitsData generateData(String dataString, DC_Player player,
                                  Coordinates spawnAt) {
        return LaunchDataKeeper.generateData(dataString,
         player,
         spawnAt, getPositioner());
    }

    public List<Unit> spawn(UnitsData data, DC_Player owner, SPAWN_MODE mode) {
        List<Unit> units = new ArrayList<>();
        int i = 0;
        if (owner == null)
            owner = getPlayerManager().getPlayer(data.getContainerValue(PARTY_VALUE.PLAYER_NAME, i));
        List<String> types = data.getContainerValues(PARTY_VALUE.MEMBERS);
        if (types.isEmpty())
            return new ArrayList<>();
        List<String> coordinates = data.getContainerValues(PARTY_VALUE.COORDINATES);
        if (coordinates.isEmpty()) {
            coordinates = getPositioner().getCoordinates(types, owner, mode);
        }
        while (true) {
            if (i >= types.size())
                return units;
            String facing = data.getContainerValue(PARTY_VALUE.FACING, i);
            String c = coordinates.get(i);

            String type = data.getContainerValue(PARTY_VALUE.MEMBERS, i);
            String level = data.getContainerValue(PARTY_VALUE.LEVEL, i);
            if (!owner.isMe())
                if (owner.isAi())
                    if (NumberUtils.getInteger(level) == 0) {
                        level = getMinLevel(type) + "";
                    }
            units.add(spawnUnit(type, c, owner, facing, level));
            i++;
        }


    }

    public int getMinLevel(String type) {
        int level = 0;
        try {
            level = getGame().getMetaMaster().getPartyManager().getPartyLevel();
        } catch (Exception e) {
        }

        return level;
    }

    public Unit spawnUnit(String typeName, String coordinates, DC_Player owner,
                          String facing, String level) {
        if (coordinates == null) {
//          TODO  getPositioner().getcoo
        }
        Coordinates c = Coordinates.get(coordinates);
        FACING_DIRECTION facing_direction = facing == null
         ? getFacingAdjuster().getFacingForUnit(c, typeName)
         : FacingMaster.getFacing(facing);
        ObjType type = DataManager.getType(typeName, C_OBJ_TYPE.UNITS_CHARS);
        //TODO chars or units?!
        if (level != null) {
            int levelUps = NumberUtils.getInteger(level);
            if (levelUps > 0) {
                type = new UnitLevelManager().getLeveledType(type, levelUps);
            }
        }

        Unit unit = (Unit) game.getManager().getObjCreator().createUnit(type, c.x, c.y, owner, new Ref(game));
        unit.setFacing(facing_direction);
        if (!unit.isHero())
            UnitTrainingMaster.train(unit);
        if (unit.isMine())
            TestMasterContent.addTestItems(unit.getType(), false);
        return unit;
    }

    public void spawnCustomParty(Coordinates origin, Boolean me, ObjType party) {
        // from entrances, from sides, by default, by event, by test - useful!
        // positioner.getCoordinatesForUnitGroup(presetGroupTypes, wave);
        List<String> partyTypes = ContainerUtils.openContainer(party.getProperty(PROPS.MEMBERS));
        List<Coordinates> c = getPositioner().getPartyCoordinates(origin, me, partyTypes);
        String partyData = DC_ObjInitializer.getObj_CoordinateString(partyTypes, c);
        spawnCustomParty(me, partyData);
    }


    public void spawnCustomParty(boolean me) {
        spawnCustomParty(me, null);
    }


    public void spawnCustomParty(boolean me, String partyData) {

        DC_Player player = game.getPlayer(me);
        if (Bools.isTrue(me)) {
            Party party = PartyHelper.getParty();
            if (party != null) {
                DC_SoundMaster.playEffectSound(SOUNDS.READY, party.getLeader());
                spawnPlayerParty(party, partyData);
                return;
            }

        }
        if (!partyData.contains(DC_ObjInitializer.COORDINATES_OBJ_SEPARATOR)) {
            partyData = DC_ObjInitializer.convertVarStringToObjCoordinates(partyData);
        }
        UnitsData data = generateData(partyData, player, null);
        spawn(data, player, SPAWN_MODE.PARTY);
//        List<MicroObj> list = DC_ObjInitializer.processUnitDataString(player, partyData, game);
//        if (!ListMaster.isNotEmpty(list)) {
//            return;
//        }


    }


    private void spawnPlayerParty(Party party, String partyData) {

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

            hero.setSpells(null);
            hero.initSpells(false);
            hero.fullReset(game);

        }
//        game.getPlayer(true).setEmblem(party.getLeader().getEmblem().getImage());
    }

    protected List<Unit> spawnUnitGroup(boolean me, String filePath) {
        String data = UnitGroupMaster.readGroupFile(filePath);
        boolean mirror = me;
        if (UnitGroupMaster.isFactionMode()) {
            if (UnitGroupMaster.factionLeaderRequired) {
                data += UnitGroupMaster.getHeroData(me);
            }
            mirror = !mirror;
        }
        UnitGroupMaster.setMirror(mirror);

        int width = UnitGroupMaster.maxX;
        int height = UnitGroupMaster.maxY;

        Coordinates offset_coordinate;
        Coordinates spawnCoordinates;
        int offsetX = -width / 2;
        int offsetY = -height / 2;
        if (!UnitGroupMaster.isFactionMode()) {
            UnitGroupMaster.setCurrentGroupHeight(MathMaster.getMaxY(data));
            UnitGroupMaster.setCurrentGroupWidth(MathMaster.getMaxX(data));
            width = 1 + UnitGroupMaster.getCurrentGroupWidth();
            height = 2 * UnitGroupMaster.getCurrentGroupHeight();
            offsetX = -width / 2;
            offsetY = -height / 2;
        } else {
            if (UnitGroupMaster.isMirror()) {
                offsetY -= 1;
            }

        }
        spawnCoordinates = (me) ? getPositioner().getPlayerSpawnCoordinates() : getPositioner()
         .getEnemySpawningCoordinates();
        offset_coordinate = spawnCoordinates.getOffsetByX(offsetX).getOffsetByY(offsetY);
        List<MicroObj> units = DC_ObjInitializer.createUnits(game.getPlayer(me), data, offset_coordinate);


        List<Unit> list = new ArrayList<>();
        units.stream().forEach(unit -> list.add((Unit) unit));
        return list;
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
