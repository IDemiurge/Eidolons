package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import main.libgdx.StyleHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabbedPanel extends Table {
    protected HashMap<String, Container> tabsToNamesMap;
    private List<Container> tabs = new ArrayList<>();
    private ButtonGroup<Button> buttonGroup = new ButtonGroup();
    private Table buttonLayout;
    private Container<Container> panelLayout;

    public TabbedPanel() {
        tabsToNamesMap = new HashMap<>();
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        setFillParent(true);
        left().bottom();
    }

    private void initContainer(float h, float prefHeight) {
        buttonLayout = new Table();
        //buttonLayout.setDebug(true);
        buttonLayout.left();
        add(buttonLayout).fill().bottom().left().height(h);

        row();
        panelLayout = new Container<>();
        panelLayout.fill().bottom().left();
        add(panelLayout).fill().bottom().left();
    }

    public void addTab(Container container, String tabName) {
        TextButton b = new TextButton(tabName, getButtonStyle());

        if (buttonLayout == null) {
            initContainer(b.getHeight(), container.getPrefHeight());
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

        tabs.add(container);

        panelLayout.setActor(container);
        panelLayout.fill().left().bottom();
        panelLayout.align(container.getAlign());
        buttonGroup.setChecked(tabName);
        tabsToNamesMap.put(tabName, container);
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
