package eidolons.game.netherflame.boss.demo.knight;

import eidolons.game.netherflame.boss.anims.generic.BossSwitchVisuals;
import eidolons.game.netherflame.boss.anims.generic.BossVisual;
import eidolons.game.netherflame.boss.logic.entity.BossUnit;

public class AriusBody extends BossSwitchVisuals {
    public AriusBody(BossUnit unit) {
        super(unit);
    }

    @Override
    protected BossVisual createPassive(BossUnit unit) {
        return new AriusBodyPassive(unit);
    }

    @Override
    protected BossVisual createActive(BossUnit unit) {
        return new AriusBodyActive(unit);
    }


    /*

     */

}
