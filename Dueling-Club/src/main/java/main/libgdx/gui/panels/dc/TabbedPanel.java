package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import main.libgdx.StyleHolder;

import java.util.ArrayList;
import java.util.List;

public class TabbedPanel extends Container<Table> {
    private List<Container> tabs = new ArrayList<>();
    private ButtonGroup<Button> buttonGroup = new ButtonGroup();
    private Table buttonLayout;
    private Container<Container> panelLayout;

    public TabbedPanel() {
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        fill();
        left().bottom();
    }

    private void initContainer(float h, float prefHeight) {
        buttonLayout = new Table();
        //buttonLayout.setDebug(true);
        buttonLayout.left();
        Table container = new Table();
        container.add(buttonLayout).fill().left().height(h);

        container.row();
        panelLayout = new Container<>();
        panelLayout.height(getPrefHeight());
        panelLayout.width(getPrefWidth());
        panelLayout.fill().bottom().center();
        container.add(panelLayout).fill().bottom().center().height(prefHeight);

        super.setActor(container);
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

        container.setFillParent(true);
        container.setHeight(getHeight() - b.getHeight());
        panelLayout.setActor(container);
        panelLayout.fill().align(container.getAlign());
        buttonGroup.setChecked(tabName);
    }

    public void resetCheckedTab() {
        if (tabs.size() > 0) {
            buttonGroup.uncheckAll();
            buttonGroup.getButtons().first().setChecked(true);
            panelLayout.setActor(tabs.get(0));
        }
    }

    @Override
    @Deprecated
    public Table getActor() {
        throw new UnsupportedOperationException("Do not use this!");
    }

    @Override
    @Deprecated
    public void setActor(Table actor) {
        throw new UnsupportedOperationException("Use TabbedPanel#addElement.");
    }

    protected TextButton.TextButtonStyle getButtonStyle() {
        return StyleHolder.getTextButtonStyle();
    }
}
