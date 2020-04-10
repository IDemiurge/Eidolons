package eidolons.game.battlecraft.logic.dungeon.test;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData.PARTY_VALUE;
import eidolons.game.module.herocreator.logic.party.Party;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;
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


    @Override
    public List<Unit> spawn(UnitsData data, DC_Player owner, SPAWN_MODE mode) {
        if (data.getValue(PARTY_VALUE.MEMBERS) == null)
            return new ArrayList<>();
        String units = data.getValue(PARTY_VALUE.MEMBERS).
         replace(DataUnitFactory.getContainerSeparator(UnitsData.FORMAT), "");
        if (FileManager.isFile(units))
            return spawnUnitGroup(owner.isMe(), units);
        return super.spawn(data, owner, mode);
    }


    public void spawnPartyAt(Party party, Coordinates coordinates) {
        spawnUnitsAt(party.getMembers(), coordinates);
    }

    public void spawnUnitsAt(List<Unit> units, Coordinates coordinates) {
        List<String> partyTypes = ContainerUtils.toNameList(units);
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
