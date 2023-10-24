package tests.basic_init.action;

import tests.basic_init.basic.BattleInitTest;

/**
 * Created by Alexander on 8/22/2023
 */
public class AttackTest extends BattleInitTest {
    @Override
    public void test() {
        super.test();

        // Weaver.inNewThread(()->
        //         combat().getExecutor().activate(
        //                 ally.getActionSet().getStandard()));
        // WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.SELECTION);
        // WaitMaster.WAIT(1000);
        // int id = enemy.getId();
        // //simulation eh?
        // client().receivedEvent(UserEventType.Selection, id);
        // DialogMaster.confirm("Attacked!");

        //access results statically?

        stdAttack(enemy, ally);
        int hp = enemy.getInt("hp");
        int armor = enemy.getInt("armor");
        int ap = ally.getInt("ap");
        // testDamage(); some basic variant always used? With preset numbers that system must arrive to?

    }
}
