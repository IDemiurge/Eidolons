package eidolons.libgdx.bf.grid.cell;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.module.cinematic.Cinematics;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.overlays.bar.HpBar;
import eidolons.libgdx.gui.generic.NoHitGroup;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.system.auxiliary.StrPathBuilder;

/**
 * Created by JustMe on 4/6/2018.
 * for structures? anything that does not need QueueView
 */
public class GenericGridView extends UnitView {
    public static final int ARROW_ROTATION_OFFSET = 90;
    protected NoHitGroup arrow;
    protected Image icon;
    protected int arrowRotation;
    protected float alpha = 1f;
    protected boolean cellBackground;
    protected LastSeenView lastSeenView;
    public FadeImageContainer torch;
    private boolean stackView;
    private boolean invisible;
    private final Label infoText = new Label("", StyleHolder.getDebugLabelStyle());

    public GenericGridView(BattleFieldObject obj, UnitViewOptions o) {
        super(o);
        setUserObject(obj);
        init(o.getDirectionPointerTexture(), o.getDirectionValue(), o.getEmblem());
        cellBackground = o.cellBackground;
        setVisible(false);

        infoText.setTouchable(Touchable.disabled);
    }

    @Override
    public void reset() {
        super.reset();
        if (emblemImage != null) {
            emblemImage.setPosition(getWidth() - emblemImage.getWidth(),
                    getHeight() - emblemImage.getHeight());
            emblemLighting.setPosition(getWidth() - emblemLighting.getWidth(),
                    getHeight() - emblemLighting.getHeight());
            if (getTeamColor() != null)
                emblemLighting.setColor(getTeamColor());
        }
        if (arrow != null) {
            arrow.setPosition(getWidth() / 2 //- arrow.getWidth() / 2
                    , 0);
//            arrow.setRotation(arrowRotation);
        }

    }

    protected void init(TextureRegion arrowTexture, int arrowRotation,
                        TextureRegion emblem) {
        if (arrowTexture != null) {
            initArrow(arrowTexture, arrowRotation);
        }
        if (emblem != null) {
            initEmblem(emblem);
        }
        setInitialized(true);
    }

    protected void initArrow(TextureRegion arrowTexture, int arrowRotation) {

        if (arrowTexture != null) {
            arrow = new NoHitGroup();
            arrow.addActor(new Image(arrowTexture));
            arrow.setSize(arrowTexture.getRegionWidth(), arrowTexture.getRegionHeight());
            arrow.addActor(torch = new FadeImageContainer(StrPathBuilder.build("ui", "unit light directional.png")) {
                @Override
                public boolean isAlphaFluctuationOn() {
                    return getBaseAlpha() > 0;
                }

                @Override
                public void setBaseAlpha(float baseAlpha) {
                    super.setBaseAlpha(baseAlpha / 2);
                }

                @Override
                public void setColor(Color color) {
                    super.setColor(color);
                }
            });
            torch.setPosition(GdxMaster.centerWidth(torch) - 3, arrow.getHeight() - torch.getHeight());
            torch.getColor().a = 0;
            torch.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.LIGHT_EMITTER_RAYS);

            addActor(arrow);
//            arrow.setPosition(getWidth() / 2 - arrow.getWidth() / 2, 0);
            arrow.setOrigin(getWidth() / 2, getHeight() / 2);
            this.arrowRotation = arrowRotation;
            updateRotation();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        setVisible(visible, false);
    }
    public void setVisible(boolean visible, boolean quiet) {
        if (invisible) {
            visible = false;
        }
        if (this.isVisible() != visible) {
            super.setVisible(visible);
            if (!quiet)
            if (getParent() instanceof GridCellContainer) {
                ((GridCellContainer) getParent()).recalcUnitViewBounds();

            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (alpha != 1f) {
            parentAlpha = alpha;
        }
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        if (!isVisible())
            return; //TODO make withinCamera()  work with actions
        // if (getY() < 0) TODO this was a check before module grids... do we still need it?
        //     setY(0);
        // if (getX() < 0)
        //     setX(0);
        if (emblemLighting != null)
            alphaFluctuation(emblemLighting, delta);
        super.act(delta);
        if (hpBar != null)
            hpBar.act(delta);
    }


    public void updateRotation() {
        updateRotation(arrowRotation, false);
    }

    public void updateRotation(int val) {
        if ((arrow.getRotation() - ARROW_ROTATION_OFFSET) % 360 == val % 360 || arrowRotation % 360 == val % 360) {
            return;
        }
        updateRotation(val, isVisible());
    }

    public void updateRotation(int val, boolean animated) {
        if (arrow != null) {
            val = val % 360;
            if (animated)
                ActionMaster.addRotateByAction(arrow, arrowRotation + ARROW_ROTATION_OFFSET,
                        val + ARROW_ROTATION_OFFSET);
            else
                arrow.setRotation(val + ARROW_ROTATION_OFFSET);
        }
        if (spritesContainersUnder != null) {
            ActionMaster.addRotateByAction(spritesContainersUnder, arrowRotation + ARROW_ROTATION_OFFSET,
                    val + ARROW_ROTATION_OFFSET);
        }
        if (spritesContainers != null) {
            ActionMaster.addRotateByAction(spritesContainers, arrowRotation + ARROW_ROTATION_OFFSET,
                    val + ARROW_ROTATION_OFFSET);
        }
        arrowRotation = val;
    }


    public boolean isHpBarVisible() {
        if (!isVisible())
            return false;
        if (!getHpBar().canHpBarBeVisible())
            return false;
        if (!isCellBackground() && HpBar.getHpAlwaysVisible())
            return true;
        return getHpBar().isHpBarVisible();
    }


    public void setMainHero(boolean mainHero) {
        super.setMainHero(mainHero);
    }

    protected void updateVisible() {
//        if (isIgnored()) //TODO [quick fix] this should actually be on for speed, but somehow in-camera views getVar ignored
//            return;
        if (border != null) {
            border.setVisible(true);
            if (Cinematics.ON) {
                border.setVisible(false);
            }
        }
        if (torch != null) {
            torch.setVisible(Cinematics.ON);
        }
        if (getOutline() != null) {
            if (emblemImage != null)
                emblemImage.setVisible(false);
            if (modeImage != null)
                modeImage.setVisible(false);
            if (arrow != null)
                arrow.setVisible(false);
            if (getHpBar() != null)
                getHpBar().setVisible(false);

        } else {
            if (emblemImage != null)
                emblemImage.setVisible(true);
            if (modeImage != null)
                modeImage.setVisible(true);
            if (arrow != null)
                arrow.setVisible(true);
            if (getHpBar() != null)
                getHpBar().setVisible(isHpBarVisible());
        }
    }

    public void setPortraitTexture(TextureRegion textureRegion) {
        if (((FileTextureData) textureRegion.getTexture().getTextureData()).getFileHandle().name().toLowerCase().
                contains("unknown")) {
            if (getUserObject().isWater()) {
                return;
            }
        }
        getPortrait().setTexture(TextureCache.getOrCreateTextureRegionDrawable(textureRegion));
    }

    @Override
    public float getWidth() {
        if (super.getWidth() == 0)
            return GridMaster.CELL_W;
        return super.getWidth();
    }

    @Override
    public float getHeight() {
        if (super.getHeight() == 0)
            return GridMaster.CELL_H;
        return super.getHeight();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();

        if (icon != null) {
            icon.setPosition(0, getHeight() - icon.getImageHeight());
        }

        if (arrow != null) {
            arrow.setOrigin(arrow.getWidth() / 2, getHeight() / 2);
            arrow.setX(getWidth() / 2 - arrow.getWidth() / 2);
//       TODO was it ever necessary?
//        arrow.setRotation(arrowRotation);
        }

        if (getScaledWidth() == 0)
            return;
        if (getScaledHeight() == 0)
            return;

        if (emblemImage != null)
            emblemImage.setScale(getScaledWidth(), getScaledHeight());
    }


    public int getId() {
        return curId;
    }

    public boolean isCellBackground() {
        return cellBackground;
    }


    @Override
    public void setHpBar(HpBar hpBar) {
        super.setHpBar(hpBar);
        hpBar.setPosition(GdxMaster.centerWidth(hpBar) - 32, -hpBar.getHeight() / 2);
    }

    @Override
    public boolean isCachedPosition() {
        return isCellBackground();
    }

    public void animateHpBarChange() {
        if (!getHpBar().isVisible())
            return;
        getHpBar().animateChange();

    }

    public LastSeenView getLastSeenView() {
        return lastSeenView;
    }

    public void setLastSeenView(LastSeenView lastSeenView) {
        this.lastSeenView = lastSeenView;
    }

    public void setStackView(boolean stackView) {
        this.stackView = stackView;
    }

    public boolean isStackView() {
        return stackView;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public boolean getInvisible() {
        return invisible;
    }

    public Label getInfoText() {
        return infoText;
    }

}

