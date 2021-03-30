package boss.demo.knight;

import boss.anims.generic.BossVisual;
import boss.anims.view.BossViewFactory;
import boss.logic.BossCycle;
import boss.logic.entity.BossUnit;
import boss.BossManager;

public class DemoBossViewFactory extends BossViewFactory {
    public DemoBossViewFactory(BossManager manager) {
        super(manager);
    }

    @Override
    public BossVisual create(BossCycle.BOSS_TYPE type, BossUnit unit) {
        switch (type) {
            case melee:
                return new AriusBody(unit);
            case caster:
                return new AriusVessel(unit);

        }
        return null;
    }
}
