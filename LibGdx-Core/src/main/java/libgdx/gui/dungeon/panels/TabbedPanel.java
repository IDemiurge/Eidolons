package libgdx.gui.dungeon.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import libgdx.StyleHolder;
import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.generic.btn.ButtonStyled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabbedPanel<T extends Actor> extends TablePanelX {
    protected HashMap<String, T> tabsToNamesMap;
    protected List<T> tabs = new ArrayList<>();
    protected String selectedTab;
    protected ButtonGroup<Button> buttonGroup = new ButtonGroup<Button>() {
        @Override
        public void setChecked(String tabName) {
            super.setChecked(tabName);
            selectedTab = tabName;
        }
    };
    protected TablePanelX contentTable;
    protected TablePanelX<Button> tabTable;
    protected Cell contentCell;

    public TabbedPanel() {
        tabsToNamesMap = new HashMap<>();
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        pad(0, 10, 0, 10);
    }

    protected void clearTabs() {
        tabTable.clearChildren();
        tabsToNamesMap.clear();
    }

    protected void initContainer() {
        tabTable = new TablePanelX<>();
        addTabTable();
        contentCell = createContentsCell();
        row();
    }

    protected Cell addTabTable() {
        Cell table = add(tabTable).expand(0, 0).fill(0, 0).align(getDefaultAlignment());
        table.row();
        return table;
    }

    protected int getDefaultAlignment() {
        return Align.left;
    }

    protected int getDefaultTabAlignment() {
        return Align.left;
    }

    protected Cell createContentsCell() {
        return addElement(contentTable = createContentsTable()).size(contentTable.getWidth(), contentTable.getHeight());
    }

    protected TablePanelX createContentsTable() {
        return new TablePanelX<>();
    }

    @Override
    public void clear() {
        super.clear();
        tabsToNamesMap.clear();
        buttonGroup.clear();
        tabs.clear();
        initContainer();
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
    }

    @Override
    protected void setUserObjectForChildren(Object userObject) {
        for (T t : tabsToNamesMap.values()) {
            t.setUserObject(userObject);
            if (t instanceof TablePanel) {
                try {
                    ((TablePanel) t).updateAct(0);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        }
    }

    public String getSelectedTab() {
        return selectedTab;
    }

    public void addTab(T actor, String tabName) {
        TextButton b = new SmartTextButton(tabName, getTabStyle(), () -> tabSelected(tabName), ButtonStyled.STD_BUTTON.TAB_HIGHLIGHT);

        if (tabTable == null) {
            initContainer();
        }

        buttonGroup.add(b);
        addTabActor(b);

        tabs.add(actor);
        buttonGroup.setChecked(tabName);
        tabsToNamesMap.put(tabName, actor);

        if (tabs.size() == 1) {
            try {
                tabSelected(tabName);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }

    protected Cell<TextButton> addTabActor(TextButton b) {
        return tabTable.add(b).align(getDefaultTabAlignment());
    }

    public void tabSelected(String tabName) {
        buttonGroup.setChecked(tabName);
        setDisplayedActor(tabsToNamesMap.get(tabName));
        tabTable.setZIndex(Integer.MAX_VALUE);
//        contentCell.getActor().setZIndex(0);
    }

    protected Cell setDisplayedActor(T t) {
        if (t instanceof TablePanel) {
            try {
                t.setUserObject(getUserObject());
                ((TablePanel) t).updateAct(0);
            } catch (Exception e) {
//                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        contentTable.clearChildren();
        return contentTable.add(t).size(contentTable.getWidth(), contentTable.getHeight());
    }

    public void resetCheckedTab() {
        if (tabs.size() > 0) {
            buttonGroup.uncheckAll();
            buttonGroup.getButtons().first().setChecked(true);
            setDisplayedActor(tabs.get(0));
        }
    }

    protected TextButton.TextButtonStyle getTabStyle() {
        return
                StyleHolder.getHqTabStyle();
    }
}
