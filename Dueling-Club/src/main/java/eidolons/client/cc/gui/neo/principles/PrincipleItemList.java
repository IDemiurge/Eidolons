package eidolons.client.cc.gui.neo.principles;

import eidolons.client.cc.CharacterCreator;
import main.entity.Entity;
import main.swing.generic.components.panels.G_ListPanel;
import main.system.graphics.GuiManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.List;

public class PrincipleItemList extends G_ListPanel<Entity> implements ListSelectionListener {

    public PrincipleItemList(List<Entity> list) {
        super(list);
        initialized = true;
        setInts();
        initList();
        refresh();
    }

    @Override
    public void refresh() {
        if (!initialized) {
            return;
        }
        super.refresh();
    }

    @Override
    protected void initList() {
        if (!initialized) {
            return;
        }
        super.initList();
        getList().addListSelectionListener(this);
    }

    @Override
    public void setInts() {
        if (!initialized) {
            return;
        }
        sizeInfo = "h " + getCompHeight() + "!" + ", w " + getCompWidth() + "!";
        rowsVisible = getListSize() / getWrap();
        minItems = getListSize();
        layoutOrientation = JList.HORIZONTAL_WRAP;
    }

    public String getCompWidth() {
        return getWrap() + "*" + GuiManager.getSmallObjSize();
    }

    public String getCompHeight() {
        return getListSize() / getWrap() + "*" + GuiManager.getSmallObjSize();
    }

    public int getWrap() {
        return 1;
    }

    private int getListSize() {
        return 10;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        CharacterCreator.getPanel().getPrincipleViewComp().getDescriptionPanel().setType(
         getList().getSelectedValue().getType());

    }

}
