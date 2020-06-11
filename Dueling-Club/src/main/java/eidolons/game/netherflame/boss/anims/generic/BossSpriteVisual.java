package eidolons.game.netherflame.boss.anims.generic;

import eidolons.game.netherflame.boss.anims.BossAnims;
import eidolons.game.netherflame.boss.anims.view.SmartSprite;
import eidolons.game.netherflame.boss.logic.entity.BossUnit;
import eidolons.libgdx.anims.anim3d.AnimMaster3d;
import eidolons.libgdx.anims.sprite.SpriteX;
import main.content.enums.GenericEnums;

public abstract class BossSpriteVisual extends BossVisual {

    SmartSprite sprite;

    public BossSpriteVisual(BossUnit unit) {
        super(unit);
        sprite = new SmartSprite(getFrameDuration(), AnimMaster3d.getOrCreateAtlas(
                getSpriteKey()), getBegin(),
                getEnd());
        addActor(new SpriteX(getTemplate(), getAlphaTemplate(), sprite));

        if (isScaled()){
            sprite.setScale(2f);
        }
    }

    private boolean isScaled() {
        return true;
    }

    protected SpriteX.SPRITE_TEMPLATE getTemplate() {
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
                break;
            case death:
                break;
            case idle:
                break;
        }

    }


}
