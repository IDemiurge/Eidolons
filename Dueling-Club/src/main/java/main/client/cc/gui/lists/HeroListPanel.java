package main.client.cc.gui.lists;

import main.client.cc.CharacterCreator;
import main.client.cc.gui.misc.BorderChecker;
import main.content.properties.PROPERTY;
import main.entity.obj.Obj;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.type.ObjType;
import main.swing.generic.components.list.ListItem;
import main.swing.generic.components.panels.G_ListPanel;
import main.system.auxiliary.GuiManager;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.List;

public class HeroListPanel extends G_ListPanel<ObjType> implements ListSelectionListener,
        MouseListener {

    public static final int DEFAULT_LIST_SLOT_COUNT = CharacterCreator.STD_COLUMN_NUMBER;
    private static final BORDER DEFAULT_BORDER = null;
    private static final BORDER HIGHLIGHTED_BORDER = BORDER.HIGHLIGHTED;
    PROPERTY highlightedGroup;
    private DC_HeroObj hero;
    private boolean vertical;
    private boolean responsive;
    private BorderChecker borderChecker;
    private int columns = 0;
    private String emptyIcon = ImageManager.getAltEmptyListIcon();

    public HeroListPanel(String title, DC_HeroObj hero, boolean responsive, boolean v,
                         int rowCount, int obj_size, List<ObjType> data) {
        this(title, hero, responsive, v, rowCount, obj_size, data, 0);
    }

    public HeroListPanel(DC_HeroObj hero, boolean vertical, int rows, int columns,
                         List<ObjType> data) {
        this("", hero, true, vertical, rows, GuiManager.getSmallObjSize(), data, columns);
    }

    public HeroListPanel(String title, DC_HeroObj hero, boolean responsive, boolean v,
                         int rowCount, int obj_size, List<ObjType> data, int columns) {
        super(null, obj_size);
        this.hero = hero;
        this.obj = hero;
        this.vertical = v;
        this.setResponsive(responsive);
        this.initialized = true;
        this.rowsVisible = rowCount;
        this.columns = columns;
        this.data = data;
        setInts();
        initList();

        if (responsive) {
            getList().addListSelectionListener(this);
        }

        getList().setCellRenderer(new ListCellRenderer<ObjType>() {

            @Override
            public Component getListCellRendererComponent(JList<? extends ObjType> list,
                                                          ObjType value, int index, boolean isSelected, boolean cellHasFocus) {
                return getListCellComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        getList().setEmptyIcon(ImageManager.getDefaultEmptyListIcon());

        refresh();
        getList().addMouseListener(this);
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public Obj getObj() {
        return hero;
    }

    protected ListItem<ObjType> getListCellComponent(JList<? extends ObjType> list, ObjType value,
                                                     int index, boolean isSelected, boolean cellHasFocus) {
        ListItem<ObjType> comp = getDefaulListComp(value, isSelected, cellHasFocus, obj_size);
        if (value == null) {
            comp.setEmptyIcon(emptyIcon);
            comp.refresh();
            return comp;
        }
        try { // absolutely not here!!!
            BORDER border = getBorder(value);
            Image img = value.getIcon().getImage();
            if (border != null) {
                img = ImageManager.applyBorder(img, border);
            }
            if (isSelected) {
                img = ImageManager.applyBorder(img, HIGHLIGHTED_BORDER);
            }
            ImageIcon icon = new ImageIcon(img);
            comp.setIcon(icon);
        } catch (Exception e) {

        }
        // no tooltips now I guess... all for the best.

        // String r = null; //
        // "((((25)*100+25*(-10))/100)*100+((25)*100+25*(-10))/100*(-0))/100";
        // try {
        // r = hero.getGame().getRequirementsManager().check(hero, value);
        // } catch (Exception e) {
        // e.printStackTrace();
        // main.system.auxiliary.LogMaster
        // .log(1, "Failed to parse requirements for " + value);
        // }
        // if (r != null) {
        // String tooltip = r;
        // if (TextParser.checkHasRefs(r)) {
        // tooltip = TextParser
        // .parse(r, hero.getRef(), TextParser.TOOLTIP_PARSING_CODE);
        // if (tooltip.contains(InfoMaster.TOOLTIP_SEPARATOR)) // ||
        // // TextParser.isFormula()
        // try {
        // String formula = tooltip
        // .split(InfoMaster.TOOLTIP_SEPARATOR)[1];
        // String prefix = tooltip.replace(formula, "");
        // tooltip = prefix
        // + new Formula(formula).getInt(hero.getRef())
        // .toString();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // comp.setToolTipText(tooltip);
        // }

        return comp;
    }

    public ListItem<ObjType> getDefaulListComp(ObjType value, boolean isSelected,
                                               boolean cellHasFocus, int obj_size) {
        return new ListItem<>(value, isSelected, cellHasFocus, obj_size);
    }

    private BORDER getBorder(ObjType value) {
        // TODO check hero
        if (borderChecker == null) {
            return DEFAULT_BORDER;
        }
        try {
            return borderChecker.getBorder(value);
        } catch (Exception e) {
            e.printStackTrace();
            main.system.auxiliary.LogMaster.log(1, "Failed to parse requirements for " + value);
            return DEFAULT_BORDER;
        }
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    public void setInts() {
        if (!initialized) {
            return;
        }
        String w;
        String h;
        int x = !vertical ? rowsVisible : columns;
        int y = vertical ? rowsVisible : columns;
        // int x = vertical ? rowsVisible : columns; //correct for custom TODO
        // int y = !vertical ? rowsVisible : columns;
        minItems = columns * rowsVisible;
        w = "" + x * GuiManager.getSmallObjSize();
        h = y * GuiManager.getSmallObjSize() + "";

        layoutOrientation = (vertical) ? JList.HORIZONTAL_WRAP : JList.VERTICAL_WRAP;

        sizeInfo = "w " + w + ", h " + h;
        // panelSize = new Dimension(new Formula(w).getInt(),
        // new Formula(h).getInt());
    }

    public int getListSlotCount() {
        if (columns == 0) {
            return DEFAULT_LIST_SLOT_COUNT;
        }
        return columns;
    }

    @Override
    public Collection<ObjType> getData() {
        return data;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // TODO Auto-generated method stub

    }

    public BorderChecker getBorderChecker() {
        return borderChecker;
    }

    public void setBorderChecker(BorderChecker borderChecker) {
        this.borderChecker = borderChecker;
    }

    public boolean isResponsive() {
        return responsive;
    }

    public void setResponsive(boolean responsive) {
        this.responsive = responsive;
    }

    public String getEmptyIcon() {
        return emptyIcon;
    }

    public void setEmptyIcon(String emptyIcon) {
        this.emptyIcon = emptyIcon;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        ObjType item = getList().locationToItem(e.getPoint());
        getList().setSelectedValue(item, true);
        if (SwingUtilities.isRightMouseButton(e)) {
            hero.getGame().getToolTipMaster().addListItemTooltip(e, item);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

}
