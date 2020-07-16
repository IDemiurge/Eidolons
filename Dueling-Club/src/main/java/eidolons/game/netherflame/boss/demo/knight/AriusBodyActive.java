package eidolons.game.netherflame.boss.demo.knight;

import eidolons.game.netherflame.boss.anims.generic.BossSpriteVisual;
import eidolons.game.netherflame.boss.logic.entity.BossUnit;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.assets.Assets;
import eidolons.libgdx.texture.Sprites;
import main.system.launch.CoreEngine;

public class AriusBodyActive extends BossSpriteVisual {
    public AriusBodyActive(BossUnit unit) {
        super(unit);
    }

    @Override
    protected boolean isInitQueueView() {
        return false;
    }

    @Override
    protected float getFrameDuration() {
        return SpriteAnimationFactory.fps30;
    }

    @Override
    protected int getEnd() {
        if (CoreEngine.TEST_LAUNCH)
            return 0;
        return 120;
    }

    @Override
    protected int getBegin() {
        if (CoreEngine.TEST_LAUNCH)
            return 0;
        return 40;
    }

    @Override
    protected String getSpriteKey() {
        if (CoreEngine.TEST_LAUNCH) {
            // return Assets.getKtxAtlasPath(Sprites.BOSS_KNIGHT_ATTACK);
            return Assets.getScaledAtlasPath(Sprites.BOSS_KNIGHT_ATTACK);
        }
        return Sprites.BOSS_KNIGHT_ATTACK;
    }
}
