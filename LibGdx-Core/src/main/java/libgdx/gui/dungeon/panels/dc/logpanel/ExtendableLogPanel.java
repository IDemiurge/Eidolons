package libgdx.gui.dungeon.panels.dc.logpanel;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import libgdx.GdxMaster;
import libgdx.anims.actions.FloatActionLimited;
import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.generic.btn.ButtonStyled;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class ExtendableLogPanel extends LogPanel {
    private final boolean top;
    private float prevHeight;
    private final SymbolButton extendButton;

    public ExtendableLogPanel(boolean top) {
        this.top = top;
        extendButton = new SymbolButton(ButtonStyled.STD_BUTTON.UP);
        extendButton.setFlipY(true);
        extendButton.setNoClickCheck(true);
        // extendButton.setSize(100, 40);
        // extendButton.setScale(1f);
        addActor(extendButton);
        extendButton.setPosition(getWidth() / 2 - extendButton.getWidth() / 2 - 1,
                top ? -11 : getHeight() + 5);

        extendButton.addCaptureListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (y > 100) {
                    return; //???
                }
                adjustHeight(getHeight() +  (top ? -y : y));

            }
        });
        updateAct();

        GuiEventManager.bind(GuiEventType.MINIMIZE_UI_ON, p -> {
            toggle(getMinAdjustHeight());
        });
        GuiEventManager.bind(GuiEventType.MINIMIZE_UI_OFF, p -> {
            toggle(prevHeight);
        });
    }

    private void toggle(float height) {
        heightAction.reset();
        prevHeight = getHeight();
        heightAction.setStart(prevHeight);
        heightAction.setDuration(1f);
        heightAction.setEnd(height);
        addAction(heightAction);
        heightAction.setTarget(this);
        heightAction.setReverse(true);
        heightAction.restart();
    }

    FloatActionLimited heightAction= new FloatActionLimited();

    @Override
    public void act(float delta) {
        super.act(delta);

        if (heightAction.isReverse()) {
            adjustHeight(heightAction.getValue());
        }
        if (heightAction.getTime() >= heightAction.getDuration()) {
            heightAction.setReverse(false  );
        }
        extendButton.setPosition(getWidth() / 2 - extendButton.getWidth() / 2 - 1,
                top ? -extendButton.getHeight()/2 : getHeight() + 5);
        extendButton.setZIndex(0);
    }

    private void adjustHeight(float val) {
        float max = getMaxAdjustHeight();
        float min = getMinAdjustHeight();
        if (val > max)
            return;
        setHeight(Math.min(Math.max(val, min),
                max));
        //                main.system.auxiliary.log.LogMaster.log(1, "dragged to " + getHeight());
        if (top) {
            GdxMaster.top(ExtendableLogPanel.this);
        }
        extendButton.setPosition(getWidth() / 2 - extendButton.getWidth() / 2 - 1,
                top ? -11 : getHeight());
        updatePos = true;
    }

    private float getMaxAdjustHeight() {
        return GdxMaster.adjustHeight(GdxMaster.getHeight() - 100);
    }

    private float getMinAdjustHeight() {
        return 100;
    }

    @Override
    protected void updateAct() {
        super.updateAct();
    }
}
