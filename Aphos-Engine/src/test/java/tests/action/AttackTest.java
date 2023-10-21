package tests.action;

import logic.execution.ActionExecutor;
import logic.execution.event.user.UserEventType;
import main.system.threading.WaitMaster;
import main.system.threading.Weaver;
import main.system.util.DialogMaster;
import tests.basic.BattleInitTest;

import javax.swing.*;

import static combat.sub.BattleManager.combat;
import static framework.client.ClientConnector.client;

/**
 * Created by Alexander on 8/22/2023
 */
@Deprecated
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
