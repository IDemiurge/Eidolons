package main.swing.components.panels;

import main.entity.active.DC_SpellObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.DC_HeroObj;
import main.game.DC_GameManager;
import main.game.MicroGameState;
import main.swing.components.obj.SpellListItem;
import main.swing.generic.components.panels.G_ListPanel;
import main.system.auxiliary.GuiManager;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * horizontal list? rules - random refresh? draw as cards? select before battle?
 *
 * @author Regulus
 */
public class DC_SpellPanel extends G_ListPanel<DC_SpellObj> implements
        ListCellRenderer<DC_SpellObj>, Comparator<DC_SpellObj> {

    private boolean rdy = false;
    private boolean autosort = true;

    public DC_SpellPanel(MicroGameState state) {
        super(state);
        rdy = true;
        getList().setCellRenderer(this);
        setObj(state.getGame().getPlayer(true).getHeroObj());
        super.refresh();
    }

    @Override
    public void refresh() {
        if (obj == null) {
            obj = state.getGame().getManager().getActiveObj();
        }

        // setObj(state.getGame().getPlayer(true).getHeroObj());
        if (obj != null) {
            super.refresh();
        }

    }

    @Override
    public Collection<DC_SpellObj> getData() {
        if (!(obj instanceof DC_HeroObj)) {
            return getEmptyData();
        }
        DC_GameManager manager = (DC_GameManager) state.getManager();
        List<DC_SpellObj> spellbook = new LinkedList<>(
                manager.getSpells((DC_HeroObj) obj));
        // if (autosort)
        // Collections.sort(spellbook, this);
        return spellbook;

    }

    @Override
    public void setInts() {
        sizeInfo = "h " + getPanelHeight() + "!" + ", w " + getPanelWidth()
                + "!";
        vpolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;
        rowsVisible = 6;
        minItems = 6;
        hpolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        layoutOrientation = JList.VERTICAL;
    }

    public int getPanelHeight() {
        return  5+6*  GuiManager.getSmallObjSize();
    }

    public int getPanelWidth() {
        return GuiManager.getSmallObjSize()  +4+9/5*
                + GuiManager.SCROLL_BAR_WIDTH;
    }

    public Collection<DC_SpellObj> getSpellObjs() {
        return data;
    }

    @Override
    public boolean isInitialized() {
        return rdy;
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends DC_SpellObj> list, DC_SpellObj value, int index,
            boolean isSelected, boolean cellHasFocus) {
        SpellListItem item = new SpellListItem(value, isSelected, cellHasFocus);
        // if (value != null)
        // value.setListItem(item);
        return item;
    }

    public void highlightsOff() {
        for (DC_SpellObj spell : data) {
            if (spell != null) {
                spell.setHighlighted(false);
            }
        }
        refresh();
    }

    public void highlight(Set<Obj> set) {
        for (DC_SpellObj spell : getSpellObjs()) {
            if (set.contains(spell)) {
                spell.setHighlighted(true);
            }

        }
        refresh();
    }

    @Override
    public int compare(DC_SpellObj o1, DC_SpellObj o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1.isPrepared() == o2.isPrepared()) {
            return closeComparison(o1, o2);
        }
        return (o1.isPrepared()) ? -1 : 1;
    }

    private int closeComparison(DC_SpellObj o1, DC_SpellObj o2) {
        if (o1.isHighlighted() == o2.isHighlighted()) {
            return closerComparison(o1, o2);
        }
        return (o1.isHighlighted()) ? -1 : 1;
    }

    private int closerComparison(DC_SpellObj o1, DC_SpellObj o2) {
        if (o1.getCircle() == o2.getCircle()) {
            return (o1.getEssenceCost() < o2.getEssenceCost()) ? -1 : 1;
        }
        return (o1.getCircle() < o2.getCircle()) ? -1 : 1;
    }
}
