package tests.basic_init.action;

import framework.entity.field.Unit;
import framework.entity.sub.UnitAction;
import tests.basic_init.basic.BattleInitTest;

import static combat.sub.BattleManager.combat;

/**
 * Created by Alexander on 8/22/2023
 */
public class DefenseActionTest extends BattleInitTest {
    @Override
    public void test() {
        super.test();

        Unit unit = combat().getUnitById(0);
        int before = unit.getInt("defense_base") ;
        UnitAction action = unit.getActionSet().getDefense();
        combat().getExecutor().activate(action );
        check(
                unit.getInt("defense_base") > before);
        //check that it stacks
        before = unit.getInt("defense_base") ;
        combat().getExecutor().activate(action );
        check(
                unit.getInt("defense_base") > before);

    }
}
