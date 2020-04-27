package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.ability.UnitTrainingMaster;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData.PARTY_VALUE;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.core.launch.LaunchDataKeeper;
import eidolons.game.module.herocreator.logic.UnitLevelManager;
import eidolons.libgdx.bf.GridCreateData;
import eidolons.system.test.TestMasterContent;
import main.content.C_OBJ_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.MicroObj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.auxiliary.NumberUtils;
import main.system.math.MathMaster;
import main.system.util.Refactor;

import java.util.ArrayList;
import java.util.List;

import static main.system.GuiEventType.SCREEN_LOADED;


public class Spawner  extends DungeonHandler  {

    public Spawner(DungeonMaster master) {
        super(master);
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
        List<Unit> unitsList = new ArrayList<>();
        unitsList.addAll(game.getUnits());
        getFacingAdjuster().adjustFacing(unitsList);

        final Integer cellsX = Coordinates.getFloorWidth();
        final Integer cellsY = Coordinates.getFloorHeight();
        final Integer moduleHeight = Coordinates.getModuleHeight();
        final Integer moduleWidth = Coordinates.getModuleWidth();
        GuiEventManager.trigger(SCREEN_LOADED,
                new GridCreateData(cellsX, cellsY, game.getBfObjects(),
                        moduleWidth,
                        moduleHeight
                        ));

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

    public List<Unit> spawn(Coordinates coordinates, List<ObjType> types, DC_Player owner, SPAWN_MODE mode) {
        UnitsData data = new UnitsData(getPositioner().getGroupCoordinates(coordinates, owner, types),
                types);
        return spawn(data, owner, mode);
    }

    public List<Unit> spawn(UnitsData data, DC_Player owner, SPAWN_MODE mode) {
        List<Unit> units = new ArrayList<>();
        int i = 0;
        if (owner == null)
            owner = getPlayerManager().getPlayer(data.getContainerValue(PARTY_VALUE.PLAYER_NAME, i));
        List<String> types = data.getContainerValues(PARTY_VALUE.UNITS);
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

            String type = data.getContainerValue(PARTY_VALUE.UNITS, i);
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
        Coordinates c = Coordinates.get(coordinates);
        return spawnUnit(DataManager.getType(typeName, C_OBJ_TYPE.UNITS_CHARS),
                c, owner, facing, level);
    }

    public Unit spawnUnit(ObjType type, Coordinates c, DC_Player owner,
                          String facing, String level) {

        //TODO chars or units?!
        if (level != null) {
            int levelUps = NumberUtils.getInteger(level);
            if (levelUps > 0) {
                type = new UnitLevelManager().getLeveledType(type, levelUps);
            }
        }

        Unit unit = (Unit) game.getManager().getObjCreator().createUnit(type, c.x, c.y, owner, new Ref(game));

        if (!unit.isHero())
            UnitTrainingMaster.train(unit);
        if (unit.isMine())
            TestMasterContent.addTestItems(unit.getType(), false);

        try {
            FACING_DIRECTION facing_direction = facing == null
                    ? getFacingAdjuster().getFacingForUnit(c, type.getName())
                    : FacingMaster.getFacing(facing);
            unit.setFacing(facing_direction);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            unit.setFacing(FACING_DIRECTION.NORTH);
        }

        return unit;
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
        List<MicroObj> units = null;
//      TODO   DC_ObjInitializer.createUnits(game.getPlayer(me), data, offset_coordinate);


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
