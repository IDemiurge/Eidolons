package main.libgdx.gui.panels.dc.logpanel.text;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import main.data.filesys.PathFinder;
import main.libgdx.GdxMaster;
import main.libgdx.gui.panels.dc.logpanel.LogPanel;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

/**
 * Created by JustMe on 11/14/2017.
 */
public class TextPanel extends LogPanel {

    public static final boolean TEST_MODE = false;
    private final Actor outside;

    public TextPanel() {
        outside = new Actor();
        outside.setBounds(0, 0, GdxMaster.getWidth(), GdxMaster.getHeight());
        outside.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                TextPanel.this.setVisible(false);
                outside.setTouchable(Touchable.disabled);
                return false;
            }


        });
        addActor(outside);

        setTouchable(Touchable.enabled);
    }

    @Override
    public void bind() {

    }

    @Override
    protected void setDefaultSize() {
        setSize(550 * GdxMaster.getFontSizeMod(), 850 * GdxMaster.getFontSizeMod());
    }

    public void setText(String text) {
        text = FileManager.readFile(
         StrPathBuilder.build(PathFinder.getTextPath(),
          "russian", "info", "manual.txt"));
//        scrollPanel.clear();
        //TODO split?!
        for (String substring : StringMaster.openContainer(text, StringMaster.NEW_LINE)) {
            scrollPanel.addElement(TextBuilder.createNew().addString(substring).build(getWidth()));
        }
        outside.setTouchable(Touchable.enabled);
        initListeners();
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
