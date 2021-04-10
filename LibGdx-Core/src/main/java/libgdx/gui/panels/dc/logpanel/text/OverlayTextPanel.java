package libgdx.gui.panels.dc.logpanel.text;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import libgdx.gui.panels.ScrollPanel;
import libgdx.gui.panels.dc.logpanel.LogPanel;
import libgdx.shaders.ShaderDrawer;
import libgdx.stage.Blocking;
import libgdx.stage.OverlayingUI;
import libgdx.stage.StageWithClosable;
import libgdx.shaders.ShaderDrawer;
import libgdx.stage.Blocking;
import libgdx.stage.OverlayingUI;
import libgdx.stage.StageWithClosable;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/14/2017.
 */
public class OverlayTextPanel extends LogPanel implements Blocking, OverlayingUI {

    public static final boolean TEST_MODE = false;

    @Override
    protected ScrollPanel<Message> createScrollPanel() {
        return new TextScroll() {

            protected float getUpperLimit() {
                return innerScrollContainer.getHeight() * 0.85f;
            }
        };
    }

    public OverlayTextPanel() {
        setTouchable(Touchable.enabled);
        initListeners();
        setVisible(false);
    }

    @Override
    public void fadeOut() {
        super.fadeOut();
    }


    protected int getInitialYOffset() {
        return -450;
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        getStage().setScrollFocus(scrollPanel);
        if (parentAlpha== ShaderDrawer.SUPER_DRAW)
        {
            super.draw(batch, 1);
            return;
        }
        ShaderDrawer.drawWithCustomShader(this, batch, null);
    }

    @Override
    public void bind() {

    }

    @Override
    public StageWithClosable getStageWithClosable() {
        return (StageWithClosable) getStage();
    }
    @Override
    protected float getDefaultHeight() {
        return 850;
    }

    @Override
    protected float getDefaultWidth() {
        return 550;
    }


    @Override
    public void act(float delta) {
        super.act(delta);
    }

    protected int getFontSize() {
        return 20;
    }

    protected FONT getFontStyle() {
        return FONT.RU;
    }

    private void initListeners() {

        addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                event.stop();
                super.scrolled(event, x, y, amount);
                return false;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return true;
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                event.stop();
                return true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                //System.out.println("mouse exit form");
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                //GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, new EventCallbackParam(null));
            }
        });

    }
}
