package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.libgdx.StyleHolder;
import main.libgdx.gui.tooltips.ToolTip;
import main.system.GuiEventManager;
import main.system.auxiliary.log.LogMaster;

import java.util.concurrent.atomic.AtomicInteger;

import static main.system.GuiEventType.REMOVE_FROM_INITIATIVE_PANEL;

public class UnitView extends BaseView {
    protected static AtomicInteger lastId = new AtomicInteger(1);
    protected final int curId;
    protected int initiativeIntVal;
    protected TextureRegion clockTexture;
    protected Label initiativeLabel;
    protected Image clockImage;

    public UnitView(UnitViewOptions o) {
        super(o);
        curId = lastId.getAndIncrement();
        init(o.getClockTexture(), o.getClockValue(), o.getPortrateTexture());
    }

    protected UnitView(UnitViewOptions o, int curId) {
        super(o);
        this.curId = curId;
        init(o.getClockTexture(), o.getClockValue(), o.getPortrateTexture());
    }

    public void setToolTip(ToolTip toolTip) {
        addListener(toolTip.getController());
    }

    private void init(TextureRegion clockTexture, int clockVal, TextureRegion portraitTexture) {
        this.initiativeIntVal = clockVal;
        portrait = new Image(portraitTexture);
        addActor(portrait);

        if (clockTexture != null) {
            this.clockTexture = clockTexture;
            initiativeLabel = new Label("[#00FF00FF]" + String.valueOf(clockVal) + "[]", StyleHolder.getDefaultLabelStyle());
            clockImage = new Image(clockTexture);
            addActor(clockImage);
            addActor(initiativeLabel);
        }
    }

    @Override
    protected void sizeChanged() {
        if (initiativeLabel != null) {
            clockImage.setPosition(
                    getWidth() - clockTexture.getRegionWidth(),
                    0
            );

            initiativeLabel.setPosition(
                    clockImage.getX() + (clockTexture.getRegionWidth() / 2 - initiativeLabel.getWidth()),
                    clockImage.getY() + (clockTexture.getRegionHeight() / 2 - initiativeLabel.getHeight() / 2));
        }

        portrait.setSize(getWidth(), getHeight());
    }

    public void updateInitiative(Integer val) {
        if (clockTexture != null) {
            initiativeIntVal = val;
            initiativeLabel.setText("[#00FF00FF]" + String.valueOf(val) + "[]");

            initiativeLabel.setPosition(
                    clockImage.getX() + (clockTexture.getRegionWidth() / 2 - initiativeLabel.getWidth()),
                    clockImage.getY() + (clockTexture.getRegionHeight() / 2 - initiativeLabel.getHeight() / 2));
        } else {
            LogMaster.error("Initiative set to wrong object type != OBJ_TYPES.UNITS");
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public int getCurId() {
        return curId;
    }

    public int getInitiativeIntVal() {
        return initiativeIntVal;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            GuiEventManager.trigger(REMOVE_FROM_INITIATIVE_PANEL, this);
        }
    }
}
