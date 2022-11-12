package combat.math;

import framework.TestResultTable;
import framework.TestUtils;
import framework.sim.AtkAccuracySim;
import framework.sim.MathSim;
import org.junit.jupiter.api.Test;

public class AccuracyMathTest {

    @Test
    public void test(){
        TestResultTable table = new TestResultTable("Attack accuracy rate");
        MathSim sim = new AtkAccuracySim();
        table.add("100 vs 50", sim.evaluate());

        // sim = new SpellAtkSim();
        sim.setVars(";;;", ";;;");
        // sim.setStringVars(";;;", ";;;");
        table.add("100 vs 50", sim.evaluate());



        TestUtils.resultsAsTable(table);
    }
}
