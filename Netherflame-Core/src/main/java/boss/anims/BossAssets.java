package boss.anims;

import boss.BossHandler;
import boss.BossManager;
import boss.BossModel;

public class BossAssets<T extends BossModel> extends BossHandler<T> {
    public BossAssets(BossManager<T> manager) {
        super(manager);
    }
}
