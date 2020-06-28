package eidolons.libgdx.bf.grid.cell;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.module.dungeoncrawl.objects.KeyMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.overlays.bar.HpBar;
import eidolons.libgdx.texture.TextureCache;
import main.content.CONTENT_CONSTS;
import main.content.enums.GenericEnums;
import main.game.bf.directions.DIRECTION;

public class OverlayView extends BaseView implements HpBarView {
    public static final float SCALE = 0.5F;
    private DIRECTION direction;
    HpBar hpBar;
    private double offsetX;
    private double offsetY;
    private Float origX;
    private Float origY;

    public void resetHpBar() {
        if (getHpBar() == null)
            setHpBar(createHpBar());
        getHpBar().reset();
    }

    protected HpBar createHpBar() {
        HpBar bar = new HpBar(getUserObject());
        return bar;
    }


    public HpBar getHpBar() {
        return hpBar;
    }

    @Override
    public BattleFieldObject getUserObject() {
        return (BattleFieldObject) super.getUserObject();
    }

    public void setHpBar(HpBar hpBar) {
        if (this.hpBar != null) {
            this.hpBar.remove();
        }
        this.hpBar = hpBar;
        hpBar.setVisible(false);
        hpBar.setScale(0.66f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (GdxMaster.isHpBarAttached() && !GridMaster.isHpBarsOnTop()) {
            addActor(hpBar);
        }
        if (origX == null) {
            origX = getX();
        }
        if (origY == null) {
            origY = getY();
        }
        for (SpriteX spriteX : underlaySprites) {
            spriteX.getSprite().setOffsetX(getOffsetX());
            spriteX.getSprite().setOffsetY(getOffsetY());
        }
        for (SpriteX spriteX : overlaySprites) {
            spriteX.setScale(1);
//            if (getWidth() >=128 )
//                spriteX.getSprite().setOffsetX(getWidth()/ 2);
//            else
            switch ((int)getWidth()) {
                case 64:
                    spriteX.getSprite().setOffsetX(getWidth());
                    break;
                case 128:
                    spriteX.getSprite().setOffsetX(getWidth()/2);
                    break;
                case 84:
                    spriteX.getSprite().setOffsetX(getWidth()/3*2+6);
                    break;
            }
            spriteX.getSprite().setOffsetY(getHeight() / 2);
            spriteX.setX(getX() - origX - spriteX.getWidth()/4);
            spriteX.setY(getY() - origY);
            spriteX.setZIndex(342354);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (SpriteX spriteX : overlaySprites) {
            spriteX.setBlending(GenericEnums.BLENDING.SCREEN);
        }
        super.draw(batch, parentAlpha);
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY);
    }

    public OverlayView(UnitViewOptions viewOptions, BattleFieldObject bfObj) {
        super(viewOptions);
        if (portrait != null)
            portrait.remove();

        TextureRegion portraitTexture = viewOptions.getPortraitTexture();
        if (flip == CONTENT_CONSTS.FLIP.VERT) {
            portraitTexture=  TextureCache.getFlippedRegion(false, true, viewOptions.getPortraitPath());
        }  else if (flip == CONTENT_CONSTS.FLIP.HOR) {
            portraitTexture=  TextureCache.getFlippedRegion( true,false, viewOptions.getPortraitPath());
        }
        portrait = new FadeImageContainer(new Image(portraitTexture));
        addActor(portrait);
//        ValueTooltip tooltip = new ValueTooltip();
//        tooltip.setUserObject(Arrays.asList(new ValueContainer(viewOptions.getName(), "")));
//         addListener(tooltip.getController());

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
