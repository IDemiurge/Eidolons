package eidolons.game.netherflame.boss.anims;

import eidolons.game.netherflame.boss.BossHandler;
import eidolons.game.netherflame.boss.BossManager;
import eidolons.game.netherflame.boss.BossModel;

public class BossAssets<T extends BossModel> extends BossHandler<T> {
    public BossAssets(BossManager<T> manager) {
        super(manager);
    }
}
