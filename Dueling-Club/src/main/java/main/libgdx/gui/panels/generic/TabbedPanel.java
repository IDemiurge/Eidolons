package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.google.gwt.rpc.server.WebModeClientOracle.Triple;
import main.libgdx.gui.layout.LayoutParser.LAYOUT;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/8/2017.
 */
public class TabbedPanel extends Container {

    public enum TAB_VARIANT {
        SMALL,
//auto-adjust size?
    }

    private Group contents;
    Group tabRow;
    Supplier<Collection<Triple<String, String, Actor>>> tabSupplier;

    public TabbedPanel(String imagePath, Supplier<Collection<Triple<String, String, Actor>>> tabSupplier) {
        super(imagePath, LAYOUT.VERTICAL);
//reverse on demand
        this.tabSupplier=tabSupplier;
        tabRow = getGroup(LAYOUT.HORIZONTAL);
        tabRow.addListener(new ClickListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                //next tab
                return super.scrolled(event, x, y, amount);
            }
        });
        contents = new Group(); //could be scrolled etc
    }

    @Override
    public void initComps() {
        setComps(tabRow, contents);
    }

    @Override
    public void update() {
        super.update();
        addTabs();
    }

    public void addTabs() {

        if (tabSupplier!=null  )
            tabSupplier.get().forEach(triple -> {
                String text = triple.getA();
                String imgPath = triple.getB();
                Actor content = triple.getC()[0];
                tabRow.addActor(
                getTab(text, imgPath, content));
            });

    }

    public void addTab(String text, String imgPath, Actor content) {
        tabRow.addActor(
         getTab(text, imgPath, content));
    }
        private Actor getTab(String text, String imgPath, Actor content) {
        TextIconComp tab = new TextIconComp(text, imgPath);
        tab.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //darken pressed tab
                setContents(content);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        return tab;
    }

    public void setContents(Actor a) {
        contents.clearChildren();
        contents.addActor(a);
    }
}
