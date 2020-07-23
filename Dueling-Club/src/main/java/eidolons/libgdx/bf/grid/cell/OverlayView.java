package eidolons.libgdx.bf.grid.cell;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.LightConsts;
import eidolons.game.module.dungeoncrawl.objects.KeyMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.overlays.bar.HpBar;
import main.content.CONTENT_CONSTS;
import main.content.enums.GenericEnums;
import main.content.enums.rules.VisionEnums;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;

import java.util.function.Function;

public class OverlayView extends BaseView implements HpBarView, Colored {
    public static final float SCALE = 0.5F;
    private final Function<Coordinates, Color> colorFunc;
    private DIRECTION direction;
    HpBar hpBar;
    private double offsetX;
    private double offsetY;
    private Float origX;
    private Float origY;
    private boolean screen;

    public void resetHpBar() {
        if (getHpBar() == null)
            setHpBar(createHpBar());
        getHpBar().reset();
    }

    protected HpBar createHpBar() {
        HpBar bar = new HpBar(getUserObject());
        return bar;
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
    }

    @Override
    public void addAction(Action action) {
        super.addAction(action);
    }

    public HpBar getHpBar() {
        return hpBar;
    }

    @Override
    public BattleFieldObject getUserObject() {
        return (BattleFieldObject) super.getUserObject();
    }

    public void setHpBar(HpBar hpBar) {
        if (this.hpBar != null) this.hpBar.remove();
        this.hpBar = hpBar;
        hpBar.setVisible(false);
        hpBar.setScale(0.66f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        applyColor(colorFunc.apply(getUserObject().getCoordinates()));

        for (SpriteX spriteX : overlaySprites) {
            spriteX.setBlending(GenericEnums.BLENDING.SCREEN);
            // screen = true; get rid of attached sprites, and really, attached views..
        }
        if (GdxMaster.isHpBarAttached() && !GridMaster.isHpBarsOnTop()) {
            addActor(hpBar);
        }
        if (origX == null) origX = getX();
        if (origY == null) origY = getY();
        for (SpriteX spriteX : underlaySprites) {
            spriteX.getSprite().setOffsetX(getOffsetX());
            spriteX.getSprite().setOffsetY(getOffsetY());
        }
        for (SpriteX spriteX : overlaySprites) {
            // spriteX.setScale(1);
            //            if (getWidth() >=128 )
            //                spriteX.getSprite().setOffsetX(getWidth()/ 2);
            //            else
            switch ((int) getWidth()) {
                case 64:
                    spriteX.getSprite().setOffsetX(getWidth());
                    break;
                case 128:
                    spriteX.getSprite().setOffsetX(getWidth() / 2);
                    break;
                case 84:
                    spriteX.getSprite().setOffsetX(getWidth() / 3 * 2 + 6);
                    break;
            }
            spriteX.getSprite().setOffsetY(getHeight() / 2);
            spriteX.setX(getX() - origX - spriteX.getWidth() / 4);
            spriteX.setY(getY() - origY);
            spriteX.setZIndex(342354);
        }
    }

    @Override
    public void applyColor(float lightness, Color c) {
        if (lightness > LightConsts.MIN_SCREEN && getPortrait().getColor().a>0) {
            screen=true;
            getPortrait().setScreenOverlay(lightness);
        } else
        {
            getPortrait().setScreenOverlay(0);
            screen=false;
        }
        getPortrait().setColor(c.r, c.g, c.b, getPortrait().getColor().a);
    }

    public void drawScreen(Batch batch) { if (!isVisible()) return;
        getPortrait().setScreenEnabled(true);
        getPortrait().setPosition(getX(), getY());
        getPortrait().draw(batch,1f);
        getPortrait().setScreenEnabled(false);
    }
    public boolean isScreen() {
        return screen;
    }

    @Override
    public float getMinLightness() {
        return getUserObject().getVisibilityLevelForPlayer().compareTo(
                VisionEnums.VISIBILITY_LEVEL.CLEAR_SIGHT) >=0
                ?  LightConsts.MIN_LIGHTNESS_CELL_SEEN
                :  LightConsts.MIN_LIGHTNESS_CELL_UNSEEN;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x= portrait.getX();
        float y= portrait.getY();
        portrait.setX(0);
        portrait.setY(0);
        super.draw(batch, parentAlpha);
        portrait.setX(x);
        portrait.setY(y);
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY);
    }

    public OverlayView(UnitViewOptions viewOptions, Function<Coordinates, Color> colorFunc ) {
        super(viewOptions);
        this.colorFunc = colorFunc;
        if (portrait != null)
            portrait.remove();

        portrait = new FadeImageContainer(viewOptions.getPortraitPath());
        if (flip == CONTENT_CONSTS.FLIP.VERT) {
            portrait.setFlipY(true);
        } else if (flip == CONTENT_CONSTS.FLIP.HOR) {
            portrait.setFlipX(true);
        }
        addActor(portrait);
        initSprite(viewOptions);
    }



    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
        }
    }

    @Override
    public boolean isCachedPosition() {
        return true;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {

        return super.hit(x, y, touchable) != null ? this : null;
    }

    public DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }

    public float getScale() {
        if (getUserObject().getName().contains("Inscription")) {
            return 0.66f;
        }
        if (KeyMaster.isKey(getUserObject())) {
            return 0.66f;
        }
        return OverlayView.SCALE;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetX() {
        return (float) offsetX;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public float getOffsetY() {
        return (float) offsetY;
    }

}
