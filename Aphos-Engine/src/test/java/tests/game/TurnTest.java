package tests.game;

import framework.data.DataManager;
import framework.entity.field.Unit;
import main.system.auxiliary.NumberUtils;
import tests.basic.BattleInitTest;

import java.util.Locale;
import java.util.Map;

import static campaign.run.battle.BattleBuilder.*;
import static combat.sub.BattleManager.combat;

/**
 * Created by Alexander on 8/26/2023
 */
public class TurnTest extends BattleInitTest {

    @Override
    public void test() {
        init();
        initParty(true, "turn test"); // could be a simple yaml data
        initParty(false, "turn test enemy");
        super.test();
        combat().newRound();
        combat().getTurnHandler().getInitiativeGroups();
        /*
        do the while loop until end of turn and check units' states
        let them do the dummy Defend actions
        perhaps a test should be able to supply custom ... input-mocks to Simulation

         */
    }

    protected int getInitialUnitCount() {
        return 8;
    }

    protected Unit getMainAllyUnit() {
        return  combat().getUnitById(0);
    }
    protected Unit getMainEnemyUnit() {
        return  combat().getUnitById(5);
    }
    private void initParty(boolean ally, String name) {
        if (customData == null)
            customData = getBattleData();

        String data = "";

        Map<String, Object> party = DataManager.getPartyData(name);
        for (String key : party.keySet()) {
            String pos = key.toLowerCase().trim();
            if (!pos.startsWith("pos_"))
                continue;
            String unit = party.get(key).toString();
            int index = getPosIndex(pos);
            if (index >= 2) //not VAN
                if (!ally) index += 9;
            data += index + "=" + unit+";";
        }
        String prefix = ally ? ALLIES : ENEMIES;
        customData[ally ? 1 : 2] = prefix+ SEPARATOR + data;
    }

    private int getPosIndex(String pos) {
        if (pos.contains("flank")) {
            return pos.contains("top") ? 2 : 9;
        }
        if (pos.contains("rear")) {
            return 10;
        }
        if (pos.contains("van")) {
            return pos.contains("top") ? 1 : 0;
        }

        int base = pos.contains("front") ? 2 : 5;
        Integer ordinal = Integer.valueOf(pos.substring(pos.length() - 1));
        return base + ordinal;
    }
        /*
            new FieldPos(Cell.Top_Flank_Player), //2
            new FieldPos(Cell.Front_Player_1), //3
            new FieldPos(Cell.Front_Player_2), //4
            new FieldPos(Cell.Front_Player_3), //5
            new FieldPos(Cell.Back_Player_1), //6
            new FieldPos(Cell.Back_Player_2), //7
            new FieldPos(Cell.Back_Player_3), //8
            new FieldPos(Cell.Bottom_Flank_Player), //9
            new FieldPos(Cell.Rear_Player), //10
         */
}
