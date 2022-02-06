package libgdx.bf.grid.cell;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.exploration.story.cinematic.Cinematics;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.bf.GridMaster;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.bf.overlays.bar.HpBar;
import libgdx.gui.generic.NoHitGroup;
import main.content.enums.GenericEnums;
import main.system.auxiliary.StrPathBuilder;

/**
 * Created by JustMe on 4/6/2018.
 * for structures? anything that does not need QueueView
 */
public class GenericGridView extends UnitView {
    public static final int ARROW_ROTATION_OFFSET = 90;
    protected Image icon;
    protected float alpha = 1f;
    protected boolean cellBackground;
    protected LastSeenView lastSeenView;
    private boolean stackView;
    private boolean invisible;
    private final Label infoText = new Label("", StyleHolder.getDebugLabelStyle());

    public GenericGridView(BattleFieldObject obj, UnitViewOptions o) {
        super(o);
        setUserObject(obj);
        init(o.getEmblem());
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
    }

    protected void init(TextureRegion emblem) {
        if (emblem != null) {
            initEmblem(emblem);
        }
        setInitialized(true);
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
        if (getOutline() != null) {
            if (emblemImage != null)
                emblemImage.setVisible(false);
            if (modeImage != null)
                modeImage.setVisible(false);
            if (getHpBar() != null)
                getHpBar().setVisible(false);

        } else {
            if (emblemImage != null)
                emblemImage.setVisible(true);
            if (modeImage != null)
                modeImage.setVisible(true);
            if (getHpBar() != null)
                getHpBar().setVisible(isHpBarVisible());
        }
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

