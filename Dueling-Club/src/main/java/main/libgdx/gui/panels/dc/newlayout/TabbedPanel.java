package main.libgdx.gui.panels.dc.newlayout;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import main.libgdx.StyleHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabbedPanel<T extends Actor> extends Group {

    private static final float buttonH = 32f;

    protected HashMap<String, T> tabsToNamesMap;
    private List<T> tabs = new ArrayList<>();
    private ButtonGroup<Button> buttonGroup = new ButtonGroup<>();
    private List<Button> buttons = new ArrayList<>();

    public TabbedPanel() {
        tabsToNamesMap = new HashMap<>();
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        debug();
    }

    public void addTab(T actor, final String tabName) {
        TextButton button = new TextButton(tabName, getButtonStyle())/* {
            @Override
            public Actor hit(float x, float y, boolean touchable) {
                final Actor hit = super.hit(x, y, touchable);
                System.out.println(hit);
                return hit;
            }
        }*/;
        button.setHeight(buttonH);
        final int sumW = buttons.stream().mapToInt(b -> ((int) b.getPrefWidth())).sum();
        button.setX(sumW + 1);
        final int indx = tabs.size();
        button.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                tabs.forEach(t -> t.setVisible(false));
                buttonGroup.setChecked(tabName);
                tabs.get(indx).setVisible(true);
                return true;
            }
        });

        buttonGroup.add(button);
        buttons.add(button);
        addActor(button);

        tabs.add(actor);
        actor.setPosition(0, 0);
        actor.setSize(getWidth(), getHeight() - buttonH);
        addActor(actor);
        buttonGroup.setChecked(tabName);
        tabsToNamesMap.put(tabName, actor);
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        tabs.forEach(el -> {
            final float height = el.getHeight() + 1;
            final float width = el.getWidth() + 1;
            el.setSize(width, height);
        });

        buttons.forEach(button -> {
            button.getY((int) (getHeight() - button.getHeight()));
        });
    }

    public void resetCheckedTab() {
        if (tabs.size() > 0) {
            buttonGroup.uncheckAll();
            buttons.get(0).setChecked(true);
            tabs.forEach(el -> el.setVisible(false));
            tabs.get(0).setVisible(true);
        }
    }

    protected TextButton.TextButtonStyle getButtonStyle() {
        return StyleHolder.getTextButtonStyle();
    }
}
