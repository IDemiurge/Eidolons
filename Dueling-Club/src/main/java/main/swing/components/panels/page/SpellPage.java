package main.swing.components.panels.page;

import main.entity.active.DC_SpellObj;
import main.entity.obj.Obj;
import main.game.MicroGameState;
import main.swing.components.obj.SpellListItem;
import main.swing.generic.components.panels.G_ListPanel;
import main.system.auxiliary.GuiManager;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;

public class SpellPage extends G_ListPanel<DC_SpellObj> implements ListCellRenderer<DC_SpellObj> {
    public static final String EMPTY_SPELL = "UI\\EMPTY_SPELL.jpg";

    public SpellPage(Obj obj, MicroGameState state, int wrap, int pageSize, List<DC_SpellObj> spells) {
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
        getList().setCellRenderer(this);
        getList().setEmptyIcon(EMPTY_SPELL);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends DC_SpellObj> list,
                                                  DC_SpellObj value, int index, boolean isSelected, boolean cellHasFocus) {
        SpellListItem spellListItem = new SpellListItem(value, isSelected, cellHasFocus);
        // if (value == null)
        // return new JLabel(ImageManager.getIcon(SpellListItem.EMPTY_SPELL));
        return spellListItem;
    }

    @Override
    public void setInts() {
        if (!initialized) {
            return;
        }
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
    public Collection<DC_SpellObj> getData() {
        return data;
    }

    @Override
    protected boolean isScrollable() {
        return false;
    }

}
