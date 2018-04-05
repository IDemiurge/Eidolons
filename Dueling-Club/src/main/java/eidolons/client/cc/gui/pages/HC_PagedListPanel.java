package eidolons.client.cc.gui.pages;

import eidolons.client.cc.gui.lists.HeroListPanel;
import eidolons.client.cc.gui.lists.ItemListManager;
import eidolons.client.cc.gui.misc.BorderChecker;
import eidolons.entity.obj.unit.Unit;
import main.content.C_OBJ_TYPE;
import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.values.parameters.PARAMETER;
import main.entity.type.ObjType;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagePanel;
import main.system.graphics.ColorManager;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.graphics.MigMaster;
import main.system.images.ImageManager;

import java.awt.*;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HC_PagedListPanel extends G_PagePanel<ObjType> {
    // TODO SWITCH PER LIST_TEMPLATE!
    private static final int OFFSET = 5;
    private static final int V_OFFSET_Y = 20;
    private static final int H_OFFSET_Y = 48;
    private static final int V_OFFSET_X = 23;
    private HC_LISTS list_type;
    private Unit hero;
    private ItemListManager itemListManager;
    private BorderChecker borderChecker;
    private String listName;
    private String emptyIcon;
    private OBJ_TYPE TYPE;
    private Image img;

    public HC_PagedListPanel(HC_LISTS list_type, Unit hero, ItemListManager itemListManager,
                             List<ObjType> data, String listName) {
        super(list_type.getTemplate().getPageSize(), list_type.getTemplate().isVertical(),
         list_type.getTemplate().getControlsVersion());
        this.data = data;
        this.listName = listName;
        this.hero = hero;
        this.itemListManager = itemListManager;
        this.visuals = list_type.getTemplate().getVisuals();
        this.list_type = list_type;
        initPages();
        refresh();
    }

    @Override
    public void refresh() {
        super.refresh();
        setFont(FontMaster.getFont(getFontType(), getFontSize(), getFontStyle()));
    }

    protected FONT getFontType() {
        // return FontMaster.getAltFontType();
        if (list_type == HC_LISTS.VENDOR) {

        }
        return FONT.MAIN;
    }

    protected int getFontStyle() {
        return Font.PLAIN;
    }

    protected float getFontSize() {
        if (list_type == HC_LISTS.VENDOR) {

        }
        return 16;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(getFont());
        g.setColor(ColorManager.GOLDEN_WHITE);
        if (TYPE != null) {
            if (list_type == HC_LISTS.VENDOR) {
                if (img == null) {
                    PARAMETER mastery = ContentManager.getPARAM(listName);
                    if (mastery == null) {
                        mastery = ContentManager.getMastery(listName);
                    }
                    if (TYPE instanceof C_OBJ_TYPE) {

                    } else {
                        switch ((DC_TYPE) TYPE) {
                            case SPELLS:
                            case SKILLS:
                                img = ImageManager.getValueIcon(mastery);
                                break;
                            case CLASSES:
                                // ?
                        }
                    }
                    int y = getTextY() - 5;
                    if (img != null) {
                        g.drawImage(img, 12, y, 28, 28, null);
                        g.drawImage(img, getWidth() - img.getWidth(null) - 12, y, 28, 28, null);
                        g.drawImage(img, (getWidth() - img.getWidth(null)) / 2, y, null);
                    }
                }
            }
        }
        if (getTextY() >= 0) {
            g.drawString(listName, getTextX(), getTextY());
        } else {
            setToolTipText(listName);
        }

    }

    private int getTextY() {
        // switch (list_type.getTemplate()){
        switch (list_type) {
            case INVENTORY:
                // return 45;
                return -1;
            case MEMORIZED:
                return getPanelHeight() - 20;
            case QUICK_ITEMS:
                // return 40;
                return -1;
            case SPELLBOOK:
                return 45;
            case JEWELRY:
                return -1;
            case SKILL:
                return 36;
            case VENDOR:
                return 20;
            case VERBATIM:
                return getPanelHeight() - 20;
        }
        return 0;
    }

    private int getTextX() {
        return MigMaster.getCenteredTextPosition(listName, getFont(), getPanelWidth());
    }

    @Override
    @Transient
    public Dimension getPreferredSize() {
        return new Dimension(getPanelWidth(), getPanelHeight());
    }

    // @Override
    // protected boolean isComponentAfterControls() {
    // return !vertical;
    // }
    @Override
    public int getPanelHeight() {
        return visuals.getHeight();
    }

    @Override
    public int getPanelWidth() {
        return visuals.getWidth() + OFFSET * 3 / 2;
    }

    protected String getCompDisplacementY() {
        if (vertical) {
            return "0";
        }
        return "" + getOffsetY();
    }

    private int getOffsetY() {
        if (list_type == HC_LISTS.JEWELRY) {
            return V_OFFSET_Y / 2;
        }
        int offset = 51;
        if (list_type == HC_LISTS.QUICK_ITEMS) {

        }
        return offset;
    }

    protected String getCompDisplacementX() {
        if (vertical) {
            return V_OFFSET_X + "";
        }
        if (list_type == HC_LISTS.JEWELRY) {
            return "13";
        }
        return "15";
    }

    @Override
    protected int getArrowOffsetX2() {
        if (vertical) {
            return getArrowOffsetX();
        } else {
            return -2 * arrowWidth - OFFSET / 2;
        }
    }

    @Override
    protected int getArrowOffsetY() {
        if (vertical) {
            return V_OFFSET_Y;
        }

        return (visuals == VISUALS.H_LIST_2_8) ? visuals.getHeight() / 16
         : -visuals.getHeight() / 40;
    }

    @Override
    protected int getArrowOffsetX() {
        return 0;
        // new Formula(getCompDisplacementX() + "-" + arrowWidth).getInt();
    }

    @Override
    protected int getArrowOffsetY2() {
        if (vertical) {
            return getPanelHeight() - V_OFFSET_Y - arrowHeight;
        }
        return getArrowOffsetY();
    }

    @Override
    protected G_Component createPageComponent(List<ObjType> data) {
        HeroListPanel list = new HeroListPanel(hero, list_type.getTemplate().isVertical(),
         list_type.getTemplate().getRows(), list_type.getTemplate().getColumns(), data);
        if (getEmptyIcon() != null) {
            list.setEmptyIcon(getEmptyIcon());
        }
        if (borderChecker != null) {
            list.setBorderChecker(borderChecker);
        }
        // if (list_type.isRemovable()) {
        // itemListManager.setPROP2(getPROP());
        // itemListManager.addRemoveList(list);
        // }
        return list;
    }

    // @Override
    // protected boolean alwaysAddControls() {
    // return false;
    // }

    @Override
    protected boolean isReinitOnRefresh() {
        return false;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    protected void resetData() {
        pageData = getPageData();
    }

    @Override
    protected List<List<ObjType>> getPageData() {
        return splitList(data);
    }

    public void setBorderChecker(BorderChecker borderChecker) {
        this.borderChecker = borderChecker;
        if (borderChecker != null) {
            for (HeroListPanel list : getLists()) {
                list.setBorderChecker(borderChecker);
            }
        }

    }

    public List<HeroListPanel> getLists() {
        ArrayList<HeroListPanel> ArrayList = new ArrayList<>();
        ArrayList.addAll(Arrays.asList(pages.toArray(new HeroListPanel[pages.size()])));
        return ArrayList;
    }

    public HeroListPanel getCurrentList() {
        return (HeroListPanel) getCurrentComponent();
    }

    public String getEmptyIcon() {
        return emptyIcon;
    }

    public void setEmptyIcon(String emptyIcon) {
        this.emptyIcon = emptyIcon;
    }

    public void setTYPE(OBJ_TYPE TYPE) {
        this.TYPE = TYPE;

    }

    public enum HC_LISTS {
        INVENTORY(LIST_TEMPLATES.HORIZONTAL_2_6),
        QUICK_ITEMS(LIST_TEMPLATES.HORIZONTAL_1_6),
        SPELLBOOK(LIST_TEMPLATES.HORIZONTAL_2_6),
        MEMORIZED(LIST_TEMPLATES.VERTICAL_2_4),
        VERBATIM(LIST_TEMPLATES.VERTICAL_2_4),
        VENDOR(LIST_TEMPLATES.HORIZONTAL_2_8),
        JEWELRY(LIST_TEMPLATES.HORIZONTAL_1_5_NARROW),
        SKILL(LIST_TEMPLATES.HORIZONTAL_2_5_NARROW),;
        private LIST_TEMPLATES template;

        HC_LISTS(LIST_TEMPLATES template) {
            this.setTemplate(template);
        }

        public LIST_TEMPLATES getTemplate() {
            return template;
        }

        public void setTemplate(LIST_TEMPLATES template) {
            this.template = template;
        }
    }

    public enum LIST_TEMPLATES {
        VERTICAL_2_4(4, 2, true, VISUALS.V_LIST_2_4), // alas, it's inverse for
        // vertical
        HORIZONTAL_1_6(1, 6, false, VISUALS.H_LIST_1_6),
        HORIZONTAL_1_6_NARROW(1, 6, false, VISUALS.H_LIST_1_6_NARROW),
        HORIZONTAL_2_8(2, 8, false, VISUALS.H_LIST_2_8),
        HORIZONTAL_2_6(2, 6, false, VISUALS.H_LIST_2_6),
        HORIZONTAL_1_5(1, 5, false, VISUALS.H_LIST_1_5),
        HORIZONTAL_1_5_NARROW(1, 5, false, VISUALS.H_LIST_1_5_NARROW),
        HORIZONTAL_2_5_NARROW(2, 5, false, VISUALS.H_LIST_2_5_NARROW);
        private int pageSize;
        private boolean vertical;
        private VISUALS visuals;
        private int controlsVersion = 3;
        private int x;
        private int y;

        LIST_TEMPLATES(int x, int y, boolean vertical, VISUALS V) {
            this.x = x;
            this.y = y;
            this.pageSize = x * y;
            this.vertical = vertical;
            this.visuals = V;
        }

        public int getColumns() {
            return y;
        }

        public int getRows() {
            return x;
        }

        public int getControlsVersion() {
            return controlsVersion;
        }

        public synchronized int getPageSize() {
            return pageSize;
        }

        public synchronized boolean isVertical() {
            return vertical;
        }

        public synchronized VISUALS getVisuals() {
            return visuals;
        }

    }
}
