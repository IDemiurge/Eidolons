package eidolons.game.battlecraft.logic.dungeon.test;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.logic.battle.arena.Wave;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.location.building.MapBlock;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitData;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitData.PARTY_VALUE;
import eidolons.game.core.game.DC_Game.GAME_MODES;
import eidolons.game.module.herocreator.logic.party.Party;
import main.data.XList;
import main.game.bf.Coordinates;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.data.DataUnitFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 5/8/2017.
 */
public class TestSpawner extends Spawner<TestDungeon> {


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

    @Override
    public List<Unit> spawn(UnitData data, DC_Player owner, SPAWN_MODE mode) {
        if (data.getValue(PARTY_VALUE.MEMBERS) == null)
            return new ArrayList<>();
        String units = data.getValue(PARTY_VALUE.MEMBERS).
         replace(DataUnitFactory.getContainerSeparator(UnitData.FORMAT), "");
        if (FileManager.isFile(units))
            return spawnUnitGroup(owner.isMe(), units);
        return super.spawn(data, owner, mode);
    }


    public void spawnPartyAt(Party party, Coordinates coordinates) {
        spawnUnitsAt(party.getMembers(), coordinates);
    }

    public void spawnUnitsAt(List<Unit> units, Coordinates coordinates) {
        List<String> partyTypes = StringMaster.toNameList(units);
        List<Coordinates> coordinateList = ((TestPositioner) getPositioner())
         .initPartyCoordinates(partyTypes, null);
        int index = 0;
        for (Unit m : units) {
            m.setCoordinates(coordinateList.get(index));
            index++;
        }
    }

    public boolean isUnitGroupMode(boolean me) {
        return me ? TestSpawner.isPlayerUnitGroupMode() : isEnemyUnitGroupMode();
    }

    public boolean isEnemyUnitGroupMode() {
        return enemyUnitGroupMode;
    }
}
