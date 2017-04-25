package main.libgdx.bf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.gui.tooltips.ToolTip;
import main.system.GuiEventManager;

import static main.system.GuiEventType.ADD_OR_UPDATE_INITIATIVE;
import static main.system.GuiEventType.REMOVE_FROM_INITIATIVE_PANEL;

public class GridUnitView extends UnitView {
    private Image arrow;
    private Image icon;
    private int arrowRotation;
    private float alpha = 1f;

    private UnitView initiativeQueueUnitView;

    public GridUnitView(UnitViewOptions o) {
        super(o);
        init(o.getDirectionPointerTexture(), o.getDirectionValue(), o.getIconTexture());

        initQueueView(o);
    }

    @Override
    public void setToolTip(ToolTip toolTip) {
        super.setToolTip(toolTip);
        if (initiativeQueueUnitView != null) {
            initiativeQueueUnitView.setToolTip(toolTip);
        }
    }

    public UnitView getInitiativeQueueUnitView() {
        return initiativeQueueUnitView;
    }

    private void initQueueView(UnitViewOptions o) {
        initiativeQueueUnitView = new UnitView(o, curId);
    }

    @Override
    public void setBorder(TextureRegion texture) {
        super.setBorder(texture);
        if (initiativeQueueUnitView != null) {
            initiativeQueueUnitView.setBorder(texture);
        }
    }

    @Override
    public void updateInitiative(Integer val) {
        super.updateInitiative(val);
        if (clockTexture != null) {
            initiativeQueueUnitView.updateInitiative(val);
            GuiEventManager.trigger(ADD_OR_UPDATE_INITIATIVE, initiativeQueueUnitView);
        }
    }

    private void init(TextureRegion arrowTexture, int arrowRotation, Texture iconTexture) {
        if (arrowTexture != null) {
            arrow = new Image(arrowTexture);
            addActor(arrow);
            arrow.setOrigin(getWidth() / 2 + arrow.getWidth(), getHeight() / 2 + arrow.getHeight());
            arrow.setPosition(getWidth() / 2 - arrow.getWidth() / 2, 0);
            this.arrowRotation = arrowRotation + 90;
        }

        if (iconTexture != null) {
            icon = new Image(iconTexture);
            addActor(icon);
            icon.setPosition(0, getHeight() - icon.getImageHeight());
        }
    }

    public void setVisibleVal(int val) {
        val = Math.max(0, val);
        val = Math.min(100, val);
        alpha = val * 0.01f;
        if (alpha < 1) {
            GuiEventManager.trigger(REMOVE_FROM_INITIATIVE_PANEL, initiativeQueueUnitView);
        } else {
            GuiEventManager.trigger(ADD_OR_UPDATE_INITIATIVE, initiativeQueueUnitView);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (alpha != 1f) {
            parentAlpha = alpha;
        }
        super.draw(batch, parentAlpha);
    }

    public void updateRotation(int val) {
        if (arrow != null) {
            arrowRotation = val + 90;
            arrow.setRotation(arrowRotation);
        }
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
            arrow.setRotation(arrowRotation);
        }
    }

    @Override
    public void setMobilityState(boolean mobilityState) {
        super.setMobilityState(mobilityState);
        if (initiativeQueueUnitView != null) {
            initiativeQueueUnitView.setMobilityState(mobilityState);
            GuiEventManager.trigger(ADD_OR_UPDATE_INITIATIVE, initiativeQueueUnitView);
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && this.getTouchable() != Touchable.enabled) {
            return null;
        }
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
    }

    public int getId() {
        return curId;
    }
}
