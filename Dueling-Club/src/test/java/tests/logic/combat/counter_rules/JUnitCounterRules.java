package tests.logic.combat.counter_rules;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.counter.generic.DamageCounterRule;
import eidolons.game.core.atb.AtbTurnManager;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.UnitDataModelSnapshot;
import org.junit.Test;
import tests.entity.JUnitSingleUnit;

/**
 * Created by JustMe on 4/9/2018.
 */
public class JUnitCounterRules extends JUnitSingleUnit {

    @Test
    public void test() {
        for (DamageCounterRule rule : game.getRules().getTimedRules().keySet()) {
            addCounters(unit, rule);
            UnitDataModelSnapshot snapshot = makeSnapshot(unit);
            passTime(10f);
          boolean  result = checkEffect(unit, snapshot, rule);
//          if (!result)
//              fail()
        }
    }

    private UnitDataModelSnapshot makeSnapshot(Unit unit) {
        return new UnitDataModelSnapshot(unit);
    }

    private boolean checkEffect(Unit unit, UnitDataModelSnapshot snapshot,
                                DamageCounterRule rule) {
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

        return false;
    }

    private void passTime(Float time) {
        if (ExplorationMaster.isExplorationOn()) {
            if (game.getTurnManager() instanceof AtbTurnManager) {
                game.getDungeonMaster().getExplorationMaster().getTimeMaster().timePassed(time);
// TODO ???                game.getDungeonMaster().getExplorationMaster().getTimeMaster().checkTimedEvents();
            }
        } else {
            ((AtbTurnManager) game.getTurnManager()).getAtbController().passTime(time);
        }
    }

    private void addCounters(Unit unit, DamageCounterRule rule) {
        unit.addCounter(rule.getCounterName(), getDefaultCounterNumber() + "");
    }

    private int getDefaultCounterNumber() {
        return 10;
    }
}
