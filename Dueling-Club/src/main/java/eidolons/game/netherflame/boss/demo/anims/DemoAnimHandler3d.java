package eidolons.game.netherflame.boss.demo.anims;

import eidolons.game.netherflame.boss.BossManager;
import eidolons.game.netherflame.boss.anims.BossAnim3dHandler;
import eidolons.game.netherflame.boss.anims.generic.BossVisual;
import eidolons.game.netherflame.boss.logic.BossCycle;
import eidolons.libgdx.texture.Sprites;

public class DemoAnimHandler3d extends BossAnim3dHandler {
    public DemoAnimHandler3d(BossManager manager) {
        super(manager);
    }

    @Override
    public String getSpritePath() {
        return Sprites.BOSS_HARVESTER;
    }

    @Override
    public void toggleActive(BossCycle.BOSS_TYPE type, boolean active) {
        // cinematics(type, active);
        BossVisual visual= getVisual(type);
        visual.setActive(active);

    }

}
