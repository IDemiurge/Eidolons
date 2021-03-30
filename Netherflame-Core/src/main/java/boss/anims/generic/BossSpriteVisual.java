package boss.anims.generic;

import boss.logic.entity.BossUnit;
import com.badlogic.gdx.graphics.g2d.Animation;
import boss.anims.BossAnims;
import boss.anims.view.SmartSprite;
import eidolons.content.consts.VisualEnums;
import libgdx.anims.sprite.SpriteX;
import libgdx.assets.Atlases;
import main.content.enums.GenericEnums;

public abstract class BossSpriteVisual extends BossVisual {

    SmartSprite sprite;

    public BossSpriteVisual(BossUnit unit) {
        super(unit);
        sprite = new SmartSprite(getPlayMode(), getFrameDuration(), Atlases.getOrCreateAtlas(
                getSpriteKey()), getBegin(),
                getEnd());
        addActor(new SpriteX(getTemplate(), getAlphaTemplate(), sprite));

        if (isScaled()){
            sprite.setScale(2f);
        }
    }

    protected Animation.PlayMode getPlayMode() {
        return Animation.PlayMode.LOOP_PINGPONG;
    }

    private boolean isScaled() {
        return true;
    }

    protected VisualEnums.SPRITE_TEMPLATE getTemplate() {
        return null;
    }

    protected GenericEnums.ALPHA_TEMPLATE getAlphaTemplate() {
        return null;
    }

    protected abstract float getFrameDuration();

    protected abstract int getEnd();

    protected abstract int getBegin();

    protected abstract String getSpriteKey();

    protected void hitAnim() {
        sprite.playBackTo(0, 1f);
    }

    protected void attackAnim() {
        sprite.allowFinish();
    }

    public void animate(BossAnims.BOSS_ANIM_COMMON anim) {
        switch (anim) {
            case hit:
            case hit_large:
                hitAnim();
                break;
            case attack:
                attackAnim();
                break;
            case appear:
            case idle:
            case death:
                break;
        }

    }


}
