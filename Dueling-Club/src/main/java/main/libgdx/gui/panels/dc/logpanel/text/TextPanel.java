package main.libgdx.gui.panels.dc.logpanel.text;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.libgdx.GdxMaster;
import main.libgdx.gui.panels.dc.logpanel.LogPanel;
import main.libgdx.stage.Closable;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 11/14/2017.
 */
public class TextPanel extends LogPanel implements Closable {

    public static final boolean TEST_MODE = false;

    public TextPanel() {
        setTouchable(Touchable.enabled);
        initListeners();
    }

    @Override
    public void bind() {

    }

    @Override
    protected void setDefaultSize() {
        setSize(550 * GdxMaster.getFontSizeMod(), 850 * GdxMaster.getFontSizeMod());
    }

    public void setText(String text) {

        scrollPanel.getInnerScrollContainer().getActor().  clear();
        //TODO split?!
        for (String substring : StringMaster.openContainer(text, StringMaster.NEW_LINE)) {
            Message message=TextBuilder.createNew().addString(substring).build(getWidth());
            for (Actor sub : message.getChildren()) {
                if (sub instanceof Label) {
                    ((Label) sub).setStyle(TextBuilder.createNew().getDefaultLabelStyle());
                }
            }
            scrollPanel.addElement(message);
        }
//        outside.setTouchable(Touchable.enabled);
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
