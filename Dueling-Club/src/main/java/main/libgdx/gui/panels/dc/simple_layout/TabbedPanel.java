package main.libgdx.gui.panels.dc.simple_layout;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import main.libgdx.StyleHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabbedPanel<T extends Actor> extends TablePanel<T> {
    protected HashMap<String, T> tabsToNamesMap;
    private List<T> tabs = new ArrayList<>();
    private ButtonGroup<Button> buttonGroup = new ButtonGroup<>();
    private Cell<T> panelLayout;
    private TablePanel<Button> buttonLayout;

    public TabbedPanel() {
        tabsToNamesMap = new HashMap<>();
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        pad(0, 10, 0, 10);
    }

    private void initContainer() {
        buttonLayout = new TablePanel<>();
        add(buttonLayout).expand(0, 0).fill(0, 0).left();

        row();
        panelLayout = addElement(null);
    }

    public void addTab(T actor, String tabName) {
        TextButton b = new TextButton(tabName, getButtonStyle());

        if (buttonLayout == null) {
            initContainer();
        }

        int indx = tabs.size();
        b.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonGroup.setChecked(tabName);
                panelLayout.setActor(tabs.get(indx));
                return true;
            }
        });
        buttonGroup.add(b);
        buttonLayout.add(b).left();

        tabs.add(actor);
        buttonGroup.setChecked(tabName);
        tabsToNamesMap.put(tabName, actor);
    }

    public void resetCheckedTab() {
        if (tabs.size() > 0) {
            buttonGroup.uncheckAll();
            buttonGroup.getButtons().first().setChecked(true);
            panelLayout.setActor(tabs.get(0));
        }
    }

    protected TextButton.TextButtonStyle getButtonStyle() {
        return StyleHolder.getTextButtonStyle();
    }
}
