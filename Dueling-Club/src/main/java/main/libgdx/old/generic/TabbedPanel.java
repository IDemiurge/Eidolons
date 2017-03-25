package main.libgdx.old.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.libgdx.gui.layout.LayoutParser.LAYOUT;
import main.swing.generic.components.G_Panel.VISUALS;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/8/2017.
 */
public class TabbedPanel extends Container {

    int selectedIndex;
    Actor displayedComp;
    TAB_VARIANT variant;
    WidgetContainer tabRow;
    Supplier<Collection<Triple<String, String, Actor>>> tabSupplier;
    private Group contents;

    public TabbedPanel(String imagePath, Supplier<Collection<Triple<String, String, Actor>>> tabSupplier) {
        super(imagePath, LAYOUT.VERTICAL);
//reverse on demand
        this.tabSupplier = tabSupplier;
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
        setComps((Actor) tabRow, contents);
    }

    @Override
    public void update() {
        super.update();
        addTabs();
    }

    public void addTabs() {

        if (tabSupplier != null) {
            tabSupplier.get().forEach(triple -> {
                String text = triple.getLeft();
                String imgPath = triple.getMiddle();
                Actor content = triple.getRight();
                tabRow.addActor(
                        getTab(text, imgPath, content));
            });
        }

    }

    public void addTab(String text, String imgPath, Actor content) {
        tabRow.addActor(
                getTab(text, imgPath, content));
    }

    private Actor getTab(String text, String imgPath, Actor content) {
        TextIconComp tab = new TextIconComp(() -> text,
                () -> isSelected(content)
                        ? VISUALS.TAB_SELECTED.getImgPath() : VISUALS.TAB.getImgPath()
                //TODO size up/down!
        );
        tab.addActor(new Comp(imgPath));
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

    private boolean isSelected(Actor content) {
        return displayedComp == content;
    }

    public void setContents(Actor a) {
        contents.clearChildren();
        contents.addActor(a);
    }

    public enum TAB_VARIANT {
        SMALL,
//auto-adjust size?
        ;

        public String toString() {

            return super.toString();
        }
    }
}
