package eidolons.game.netherflame.boss.demo.knight;

import com.badlogic.gdx.graphics.g2d.Animation;
import eidolons.game.netherflame.boss.anims.generic.BossSpriteVisual;
import eidolons.game.netherflame.boss.logic.entity.BossUnit;
import eidolons.libgdx.anims.Assets;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.texture.Sprites;
import main.system.launch.CoreEngine;

public class AriusBodyPassive extends BossSpriteVisual {
    public AriusBodyPassive(BossUnit unit) {
        super(unit);
    }

    @Override
    protected float getFrameDuration() {
        return SpriteAnimationFactory.fps30;
    }

    @Override
    protected int getEnd() {
        return 0;
    }

    @Override
    protected int getBegin() {
        return 0;
    }

    protected Animation.PlayMode getPlayMode() {
        return Animation.PlayMode.LOOP;
    }
    @Override
    protected String getSpriteKey() {
        if (CoreEngine.TEST_LAUNCH) {
            // return Assets.getKtxAtlasPath(Sprites.BOSS_KNIGHT);
            return Assets.getScaledAtlasPath(Sprites.BOSS_KNIGHT);
        }
        return Sprites.BOSS_KNIGHT;
    }
    /*
    so, just an idle sprite?
     */
}
