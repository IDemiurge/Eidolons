package boss.demo.logic;

import boss.logic.rules.BossTargeter;
import boss.BossManager;
import boss.demo.DemoBoss;

public class DemoBossTargeter extends BossTargeter {
    public DemoBossTargeter(BossManager<DemoBoss> manager) {
        super(manager);
    }
}
