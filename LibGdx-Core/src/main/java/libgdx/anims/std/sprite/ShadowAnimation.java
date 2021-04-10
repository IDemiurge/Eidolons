package libgdx.anims.std.sprite;

import eidolons.game.core.Eidolons;
import eidolons.content.consts.Sprites;
import main.entity.Entity;

public class ShadowAnimation extends CustomSpriteAnim {
    public ShadowAnimation(boolean death, Entity active, Runnable runnable) {
        super(active, getShadowSpritePath(death));
        setOnDone(p->{
            Eidolons.onNonGdxThread(runnable);
        });
    }

    private static String getShadowSpritePath(boolean death) {
        return
                 death ? Sprites.SHADOW_DEATH : Sprites.SHADOW_SUMMON;
    }

}
