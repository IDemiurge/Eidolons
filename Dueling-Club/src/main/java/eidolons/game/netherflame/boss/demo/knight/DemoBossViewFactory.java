package eidolons.game.netherflame.boss.demo.knight;

import eidolons.game.netherflame.boss.BossManager;
import eidolons.game.netherflame.boss.anims.generic.BossVisual;
import eidolons.game.netherflame.boss.anims.view.BossViewFactory;
import eidolons.game.netherflame.boss.logic.BossCycle;
import eidolons.game.netherflame.boss.logic.entity.BossUnit;

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
