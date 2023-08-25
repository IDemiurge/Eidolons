package tests.action;

import framework.entity.field.Unit;
import framework.entity.sub.UnitAction;
import tests.basic.BattleInitTest;

import static combat.sub.BattleManager.combat;

/**
 * Created by Alexander on 8/22/2023
 */
@Deprecated
public class DefenseActionTest extends BattleInitTest {
    @Override
    public void test() {
        super.test();

        Unit unit = combat().getUnitById(0);
        int before = unit.getInt("defense_min") ;
        UnitAction action = unit.getActionSet().getDefense();
        combat().getExecutor().activate(action );
        check(
                unit.getInt("defense_min") > before);
        before = unit.getInt("defense_min") ;
        combat().getExecutor().activate(action );
        check(
                unit.getInt("defense_min") > before);
        //what to check?
    }
}
