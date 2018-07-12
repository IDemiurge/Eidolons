package eidolons.libgdx.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import eidolons.libgdx.StyleHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabbedPanel<T extends Actor> extends TablePanel<T> {
    protected HashMap<String, T> tabsToNamesMap;
    private List<T> tabs = new ArrayList<>();
    private String selectedTab;
    private ButtonGroup<Button> buttonGroup = new ButtonGroup<Button>() {
        @Override
        public void setChecked(String tabName) {
            super.setChecked(tabName);
            selectedTab = tabName;
        }
    };
    private Cell<T> panelLayout;
    private TablePanel<Button> buttonLayout;

    public TabbedPanel() {
        tabsToNamesMap = new HashMap<>();
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        pad(0, 10, 0, 10);
    }

    protected void clearTabs() {
        buttonLayout.clearChildren();
        tabsToNamesMap.clear();
    }

    private void initContainer() {
        buttonLayout = new TablePanel<>();
        add(buttonLayout).expand(0, 0).fill(0, 0).left();

        row();
        panelLayout = addTabCell();
    }

    protected Cell<T> addTabCell() {
        return addElement(null);
    }

    @Override
    public void clear() {
        super.clear();
        tabsToNamesMap.clear();
        buttonGroup.clear();
        tabs.clear();
        initContainer();
    }

    public String getSelectedTab() {
        return selectedTab;
    }

    public void addTab(T actor, String tabName) {
        TextButton b = new TextButton(tabName, getTabStyle());

        if (buttonLayout == null) {
            initContainer();
        }

        b.addListener(new InputListener() {
                          @Override
                          public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                              tabSelected(tabName);
                              return true;

                          }
                      }
        );

        buttonGroup.add(b);
        buttonLayout.add(b).left();

        tabs.add(actor);
        buttonGroup.setChecked(tabName);
        tabsToNamesMap.put(tabName, actor);
    }

    public void tabSelected(String tabName) {
        buttonGroup.setChecked(tabName);
        panelLayout.setActor(tabsToNamesMap.get(tabName));
    }

    public void resetCheckedTab() {
        if (tabs.size() > 0) {
            buttonGroup.uncheckAll();
            buttonGroup.getButtons().first().setChecked(true);
            panelLayout.setActor(tabs.get(0));
        }
    }

    protected TextButton.TextButtonStyle getTabStyle() {
        return
         StyleHolder.getDefaultTabStyle();
    }
}
