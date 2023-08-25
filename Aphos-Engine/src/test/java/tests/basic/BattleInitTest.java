package tests.basic;

import campaign.run.battle.BattleBuilder;
import combat.Battle;
import combat.init.BattleSetup;
import framework.AphosTest;
import framework.Core;
import framework.data.DataManager;
import framework.entity.field.Unit;
import framework.field.FieldPos;
import resources.TestData;

import static combat.sub.BattleManager.combat;
import static org.junit.Assert.assertTrue;
import static resources.TestData.*;

/**
 * Created by Alexander on 8/22/2023
 */
@Deprecated
public class BattleInitTest extends AphosTest  {

    protected Unit ally;
    protected Unit enemy;
    private static boolean testInitDone;

    @Override
    public void test() {
        // new MockRun();
        if (!testInitDone){
        Core.init();
        DataManager.init(entityData);
        TestData.initAllPresets();
            testInitDone= true;
        }
        
        BattleSetup setup = new BattleBuilder().build(battleData);
        Battle battle = new Battle(setup);
        // battle.init();
        battle.start();
        //after this, we can use combat() freely?

        assertTrue(combat().getEntities().getUnits().size()== 3);
         ally = combat().getUnitById(0);
         enemy = (Unit) combat().getField().getByPos(new FieldPos(12));
        assertTrue(ally.getName().equals(unit_name_ally));
        assertTrue(enemy.getName().equals(unit_name_enemy));
        assertTrue(combat().getBattleState().getRound() == 1);
    }
}
