package boss.anims.generic;

import boss.logic.entity.BossUnit;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import boss.anims.BossAnims;
import boss.anims.view.BossQueueView;
import libgdx.bf.grid.cell.QueueView;
import libgdx.gui.generic.GroupX;
import libgdx.gui.dungeon.panels.dc.topleft.atb.AtbPanel;
import libgdx.gui.dungeon.tooltips.SmartClickListener;
import main.game.bf.Coordinates;
import main.system.launch.CoreEngine;

public abstract class BossVisual extends GroupX {
    /*
    better than override gridView!

    targeting
    camera bounds
    move?

    linked QueueVIew?
     */
    protected BossQueueView queueView;
    protected boolean hovered;
    protected boolean targetHighlight;
    protected boolean aiMoving;
    protected boolean active;

    BossUnit unit;

    public BossVisual(BossUnit unit) {
        this.unit = unit;
        setUserObject(unit);
        init();
        if (!CoreEngine.isLevelEditor()) {
            if (CoreEngine.TEST_LAUNCH)
                debugAll();
        }

    }

    public void targetHighlight(boolean on) {
        targetHighlight = on;
    }

    public void setAiMoving(boolean on) {
        aiMoving = on;
    }

    public void setActive(boolean on) {
        active = on;
    }

    public void hovered(boolean on) {
        hovered = on;
    }

    public void init() {
        if (isInitListeners())
            addListener(createListener());

        // addListener(new BossTooltip(this).getController());
        if (unit == null) {
            return;
        }
        setSize(unit.getWidth() * 128, unit.getHeight() * 128);

        if (isInitQueueView()) {
            queueView = new BossQueueView(unit, getPortrait(), this);
            queueView.setUserObject(unit);
            queueView.setParentView(this);
            queueView.setSize(AtbPanel.imageSize, AtbPanel.imageSize);
            queueView.setHoverResponsive(true);
        }
    }

    protected boolean isInitListeners() {
        return true;
    }

    protected String getPortrait() {
        return unit.getImagePath();
    }

    protected boolean isInitQueueView() {
        return true;
    }

    public abstract void animate(BossAnims.BOSS_ANIM_COMMON anim);

    protected EventListener createListener() {
        return new SmartClickListener(this) {
            //radial?
            @Override
            protected void entered() {
                hovered(true);
                super.entered();
            }

            @Override
            protected void exited() {
                hovered(false);
                super.exited();
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
            }

        };
    }

    public BossUnit getUnit() {
        return unit;
    }

    public QueueView getQueueView() {
        return queueView;
    }

    public Coordinates getCoordinates() {
        return getUnit().getCoordinates();
    }
}
