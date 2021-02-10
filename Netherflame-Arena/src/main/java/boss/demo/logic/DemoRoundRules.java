package boss.demo.logic;

import boss.logic.rules.RoundRules;
import boss.BossManager;
import boss.demo.DemoBoss;

public class DemoRoundRules extends RoundRules<DemoBoss> {
    public DemoRoundRules(BossManager<DemoBoss> manager) {
        super(manager);
    }
}
