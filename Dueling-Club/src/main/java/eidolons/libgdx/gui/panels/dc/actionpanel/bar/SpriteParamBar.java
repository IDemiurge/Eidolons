package eidolons.libgdx.gui.panels.dc.actionpanel.bar;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.texture.Sprites;
import main.content.enums.GenericEnums;

import java.util.function.Supplier;

public abstract class SpriteParamBar extends DualParamBar {

    private static final float FPS = 12;
    SpriteAnimation overSprite;
    SpriteAnimation underSprite;

    public SpriteParamBar(Supplier<BattleFieldObject> supplier) {
        super(supplier);
    }

    @Override
    protected String getBarImagePath(boolean over) {
        return over? Sprites.SOULFORCE_BAR : Sprites.SOULFORCE_BAR_BG;
    }

    @Override
    protected void initRegions() {
        overSprite = SpriteAnimationFactory.getSpriteAnimation(getBarImagePath(true));
        underSprite = SpriteAnimationFactory.getSpriteAnimation(getBarImagePath(false));
        overSprite.setCustomAct(true);
        underSprite.setCustomAct(true);
        overSprite.setFps(FPS);
        underSprite.setFps(FPS);
        underSprite.setPlayMode(Animation.PlayMode.LOOP);
        overSprite.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    protected void initSizes() {
        overBarRegion=overSprite.getCurrentFrame();
        underBarRegion= underSprite.getCurrentFrame();

        height = underBarRegion.getRegionHeight();
        innerWidth = underBarRegion.getRegionWidth();
        offsetX = 5; //?
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        overSprite.act(delta);
        underSprite.act(delta);
        overBarRegion=overSprite.getCurrentFrame();
        underBarRegion= underSprite.getCurrentFrame();
        initColors();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.SCREEN);
        super.draw(batch, parentAlpha);
        ((CustomSpriteBatch) batch).resetBlending();
    }

    @Override
    protected String getBarBgPath() {
        return null;
    }

}
