package main.game.logic.dungeon.editor.gui;

import main.entity.type.ObjType;
import main.game.logic.dungeon.editor.LevelEditor;
import main.swing.generic.components.list.CustomList;
import main.swing.generic.components.list.G_List;
import main.swing.generic.components.panels.G_ListPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.List;

public class PaletteList extends G_ListPanel<ObjType> implements ListSelectionListener,
        MouseListener {

    public PaletteList(List<ObjType> list) {
        super(list);
//        this.list.addMouseListener(this);
    }

    @Override
    public Collection<ObjType> getEmptyData() {
        return super.getEmptyData();
    }

    @Override
    protected G_List<ObjType> createList() {
        G_List<ObjType> customList = new G_List<ObjType>(data) {
            @Override
            public String getEmptyIcon() {
                return "ui/empty_list_item.jpg";
            }

        };
        customList.addListSelectionListener(this);
//        customList.addMouseListener(this);
        return customList;
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    protected void initList() {
        super.initList();
        list.addListSelectionListener(this);
    }

    public void setInts() {
        minItems = 50;
        rowsVisible = vertical ? 10 : 2;
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (list.getSelectedValue() == null) {
            return;
        }
        LevelEditor.getMainPanel().getPalette().setSelectedType(list.getSelectedValue());


        LevelEditor.getSimulation().setSelectedEntity(list.getSelectedValue());

        LevelEditor.setMouseAddMode(true);

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // LevelEditor.getWorkspaceManager().addTypeToActiveWorkspace(selectedType);
        // selectedTab;

        ObjType selectedValue = list.getSelectedValue();
        if (SwingUtilities.isRightMouseButton(e)) {
            data.remove(selectedValue);
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }
}
