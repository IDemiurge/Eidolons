package main.game.battlecraft.logic.dungeon.test;

import main.data.XList;
import main.game.battlecraft.ai.GroupAI;
import main.game.battlecraft.logic.battle.arena.Wave;
import main.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.battlecraft.logic.dungeon.DungeonMaster;
import main.game.battlecraft.logic.dungeon.Spawner;
import main.game.battlecraft.logic.dungeon.location.building.MapBlock;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game.GAME_MODES;
import main.system.auxiliary.log.LogMaster;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 5/8/2017.
 */
public class TestSpawner extends Spawner<TestDungeon>{


    private static boolean playerUnitGroupMode;
    private boolean enemyUnitGroupMode;

    public TestSpawner(DungeonMaster master) {
        super(master);
    }

    public static boolean isPlayerUnitGroupMode() {
        return playerUnitGroupMode;
    }

    public static void setPlayerUnitGroupMode(boolean b) {
        playerUnitGroupMode = b;

    }

    private void initGroup(Wave group) {
        GroupAI groupAi = new GroupAI(group);
        groupAi.setLeader(group.getParty().getLeader());
        groupAi.setWanderDirection(FacingMaster.getRandomFacing().getDirection());
        group.setAi(groupAi);
        if (getGame().getGameMode() == GAME_MODES.DUNGEON_CRAWL) {
            XList<MapBlock> permittedBlocks = new XList<>();
            permittedBlocks.addAllUnique(group.getBlock().getConnectedBlocks().keySet());
            int wanderBlockDistance = 1;
            for (int i = 0; i < wanderBlockDistance; i++) {
                for (MapBlock b : group.getBlock().getConnectedBlocks().keySet()) {
                    permittedBlocks.addAllUnique(b.getConnectedBlocks().keySet());
                }
            }
            groupAi.setPermittedBlocks(permittedBlocks);
        }
    }


    private void spawnUnitGroup(boolean me, String partyData) {
        String data = UnitGroupMaster.readGroupFile(partyData);
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
        DC_ObjInitializer.createUnits(game.getPlayer(me), data, offset_coordinate);

        LogMaster.logToFile("spawnCoordinates=" + spawnCoordinates + " ;offset_coordinate="

                + offset_coordinate + ";height=" + height + "; width=" + width);
        LogMaster.log(1, "spawnCoordinates=" + spawnCoordinates
         + " ;offset_coordinate=" + offset_coordinate + ";height=" + height + "; width="
         + width);
    }

    public boolean isUnitGroupMode(boolean me) {
        return me ? TestSpawner.isPlayerUnitGroupMode() : isEnemyUnitGroupMode();
    }

    public boolean isEnemyUnitGroupMode() {
        return enemyUnitGroupMode;
    }
}
