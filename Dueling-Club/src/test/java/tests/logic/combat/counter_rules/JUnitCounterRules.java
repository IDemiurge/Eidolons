package tests.logic.combat.counter_rules;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.counter.generic.DamageCounterRule;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.UnitDataModelSnapshot;
import org.junit.Test;
import tests.entity.JUnitSingleUnit;

import static org.junit.Assert.fail;

/**
 * Created by JustMe on 4/9/2018.
 */
public class JUnitCounterRules extends JUnitSingleUnit {

    @Test
    public void test() {
        testRules(true, game.getRules().getTimedRules().keySet()
         .toArray(new DamageCounterRule[game.getRules().getTimedRules().size()]));

        testRules(false, game.getRules().getTimedRules().keySet()
         .toArray(new DamageCounterRule[game.getRules().getTimedRules().size()]));

    }

    private void testRules(boolean exploreMode, DamageCounterRule... rules) {
        for (DamageCounterRule rule : rules) {
            helper.resetUnit(unit);
            helper.addCounters(unit, rule.getCounter(), getDefaultCounterNumber());
            UnitDataModelSnapshot snapshot = helper.makeSnapshot(unit);
            helper.passTime(10f);
//            WaitMaster.waitForInput(WAIT_OPERATIONS.ACTIVE_UNIT_SELECTED);

            Eidolons.getGame().getDungeonMaster().getExplorationMaster().getAiMaster().reset();
            game.getDungeonMaster().getExplorationMaster().getTimeMaster().checkTimedEvents();

            boolean result = checkEffect(unit, snapshot, rule);
            if (!result)
                fail();
        }
    }


    protected boolean checkEffect(Unit unit, UnitDataModelSnapshot snapshot,
                                  DamageCounterRule rule) {
        PARAMS p = PARAMS.C_ENDURANCE;
        switch (rule.getCounter()) {
            case Bleeding:
                break;
            case Blaze:
                break;
            case Poison:
                break;
            case Disease:
                break;
            case Lava:
                break;
            case Suffocation:
                break;
        }
        Integer n = unit.getIntParam(p);
        Integer n1 = snapshot.getIntParam(PARAMS.C_ENDURANCE);
        utils.log(rule.getCounter().getName() + " check, " + n1 +
         p.getName() + " before, " + n +
         " after ");
        return n < n1;
    }

    protected int getDefaultCounterNumber() {
        return 10;
    }
}
