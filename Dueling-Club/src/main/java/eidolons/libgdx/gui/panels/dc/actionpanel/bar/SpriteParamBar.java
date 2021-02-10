package eidolons.libgdx.gui.panels.dc.actionpanel.bar;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.generic.VerticalValueContainer;
import eidolons.libgdx.gui.tooltips.DynamicTooltip;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.texture.Sprites;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.text.DescriptionTooltips;
import main.content.enums.GenericEnums;
import main.content.values.parameters.PARAMETER;
import main.system.images.ImageManager;

import java.util.Collections;
import java.util.function.Supplier;

public abstract class SpriteParamBar extends DualParamBar {

    public static final boolean TEST = false; 
    private static final float FPS = 14;
    SpriteAnimation overSprite;
    SpriteAnimation underSprite;

    public SpriteParamBar(Supplier<BattleFieldObject> supplier) {
        super(supplier);
        addListener(new DynamicTooltip(this::getTooltipText).getController());
    }

    protected abstract String getTooltipText();


    public void addTooltip(Label label, PARAMETER parameter) {
        ValueTooltip tooltip = new ValueTooltip();
        String description = DescriptionTooltips.tooltip(parameter);
        TextureRegion r = TextureCache.getOrCreateR(ImageManager.getValueIconPath(parameter));
        ValueContainer container = new VerticalValueContainer(r, parameter.getName() + ": " + label.getText(),
                description);
        container.setSize(600, 400);
        container.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
        tooltip.setUserObject(Collections.singletonList(container));
        label.clearListeners();
        label.addListener(tooltip.getController());
    }
    @Override
    protected String getBarImagePath(boolean over) {
        return over? Sprites.SOULFORCE_BAR_WHITE : Sprites.SOULFORCE_BAR_BG_WHITE;
    }

    @Override
    protected void initRegions() {
        overSprite = SpriteAnimationFactory.getSpriteAnimation(getBarImagePath(true), false, false);
        underSprite = SpriteAnimationFactory.getSpriteAnimation(getBarImagePath(false), false, false);
        overSprite.setCustomAct(true);
        underSprite.setCustomAct(true);
        overSprite.setFps(FPS);
        underSprite.setFps(FPS);
        underSprite.setPlayMode(Animation.PlayMode.LOOP);
        overSprite.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
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

    protected int getLabelY() {
        return 120;
    }
    protected void resetLabelPos() {
        int y = getLabelY();
        label1.setPosition(245, y);
        label2.setPosition(23, y);
    }

    protected boolean isLabelsDisplayed() {
        return true;
    }
    @Override
    protected void resetLabel() {
        super.resetLabel();
        addTooltip(label1, getUnderParam(false));
        addTooltip(label2, getOverParam(false));
    }

    @Override
    protected String getBarBgPath() {
        return null;
    }

}
