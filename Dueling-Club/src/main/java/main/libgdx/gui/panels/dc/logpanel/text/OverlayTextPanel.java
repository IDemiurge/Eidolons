package main.libgdx.gui.panels.dc.logpanel.text;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.libgdx.gui.panels.dc.logpanel.LogPanel;
import main.libgdx.stage.Closable;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/14/2017.
 */
public class OverlayTextPanel extends LogPanel implements Closable {

    public static final boolean TEST_MODE = false;

    public OverlayTextPanel() {
        setTouchable(Touchable.enabled);
        initListeners();
    }

    @Override
    public void bind() {

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

    @Override
    protected void initScrollPanel() {
        super.initScrollPanel();
        scrollPanel.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return super.touchDown(event, x, y, pointer, button);
            }
        });
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
//    public void open() {
//        if (getStage() instanceof StageWithClosable) {
//            ((StageWithClosable) getStage()).setDisplayedClosable(this);
//        }
//        setVisible(true);
//    }



    @Override
    public void close() {
        setVisible(false);
    }
}
