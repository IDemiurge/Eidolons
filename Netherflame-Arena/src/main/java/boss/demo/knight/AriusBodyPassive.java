package boss.demo.knight;

import boss.anims.generic.BossSpriteVisual;
import boss.logic.entity.BossUnit;
import com.badlogic.gdx.graphics.g2d.Animation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.assets.Assets;
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
