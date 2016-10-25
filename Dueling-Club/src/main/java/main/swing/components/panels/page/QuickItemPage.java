package main.swing.components.panels.page;

import main.entity.obj.DC_QuickItemObj;
import main.entity.obj.Obj;
import main.game.MicroGameState;
import main.swing.components.obj.QuickItemListItem;
import main.swing.generic.components.panels.G_ListPanel;
import main.system.auxiliary.GuiManager;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;

public class QuickItemPage extends G_ListPanel<DC_QuickItemObj> implements
        ListCellRenderer<DC_QuickItemObj> {

    public QuickItemPage(Obj obj, MicroGameState state, int wrap, int pageSize,
                         List<DC_QuickItemObj> spells) {
        super(spells, GuiManager.getSmallObjSize(), state);
        setObj(obj);
        this.wrap = wrap;
        this.minItems = pageSize;
        initialized = true;
        setInts();
        initList();
        refresh();
    }

    @Override
    public void refresh() {
        if (!initialized)
            return;
        super.refresh();
    }

    @Override
    protected void initList() {
        if (!initialized)
            return;
        super.initList();
        getList().setCellRenderer(this);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends DC_QuickItemObj> list,
                                                  DC_QuickItemObj value, int index, boolean isSelected, boolean cellHasFocus) {
        QuickItemListItem spellListItem = new QuickItemListItem(value, isSelected, cellHasFocus);
        // if (value == null)
        // return new JLabel(ImageManager.getIcon(SpellListItem.EMPTY_SPELL));
        return spellListItem;
    }

    @Override
    public void setInts() {
        if (!initialized)
            return;
        sizeInfo = "h " + getCompHeight() + "!" + ", w " + getCompWidth() + "!";
        rowsVisible = minItems / wrap;
        layoutOrientation = JList.HORIZONTAL_WRAP;
    }

    public String getCompHeight() {
        return minItems / wrap + "*" + GuiManager.getSmallObjSize();
    }

    public String getCompWidth() {
        return GuiManager.getSmallObjSize() + "*" + wrap;
    }

    @Override
    public Collection<DC_QuickItemObj> getData() {
        return data;
    }

    @Override
    protected boolean isScrollable() {
        return false;
    }
}
