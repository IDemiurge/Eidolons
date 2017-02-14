package main.client.cc.gui.neo.choice;

import main.client.cc.gui.neo.choice.PagedSelectionPanel.SelectionPage;
import main.content.OBJ_TYPE;
import main.entity.Entity;
import main.entity.obj.unit.DC_HeroObj;
import main.swing.components.PagedOptionsComp;
import main.swing.components.buttons.CustomButton;
import main.swing.components.panels.page.info.DC_PagedInfoPanel;
import main.swing.components.panels.page.info.element.TextCompDC;
import main.swing.generic.components.G_Panel;
import main.swing.listeners.ListChooserSortOptionListener.SORT_TEMPLATE;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.ListMaster;
import main.system.graphics.MigMaster;

import java.awt.*;
import java.util.List;

/*
 * will there be any CV's that I don't want to have heroPanel or infoPanel or both?
 * 
 * Dungeon Choice
 * Middle Hero Choice
 */

public abstract class ChoiceView<E> extends G_Panel {
    protected static final String BACK_BUTTON_POS = "@id back, pos pages.x-width ip.y2";
    protected static final String OK_BUTTON_POS = "@id ok, pos ip.x2+width ip.y2";
    protected static final String IP_POS = "id ip, pos pages.x2 pages.y";

    protected static final int DEFAULT_ITEM_SIZE = 128;
    protected static final int DEFAULT_PAGE_SIZE = 24;
    protected static final int DEFAULT_COLUMNS = 4;

    protected PagedSelectionPanel<E> pages;
    protected ChoiceSequence sequence;
    protected int index = -1;
    protected List<E> data;
    protected OBJ_TYPE TYPE;
    protected DC_PagedInfoPanel infoPanel;
    protected DC_HeroObj hero;

    protected CustomButton backButton;
    protected CustomButton okButton;
    protected String sorterOption;
    protected String filterOption;
    protected PagedOptionsComp<SORT_TEMPLATE> sortOptionsComp;
    protected PagedOptionsComp filterOptionsComp;

    public ChoiceView(ChoiceSequence sequence, DC_HeroObj hero) {
        this(null, null, sequence, hero);
    }

    public ChoiceView(List<E> data, OBJ_TYPE TYPE, ChoiceSequence sequence, DC_HeroObj hero) {
        setPanelSize(GuiManager.DEF_DIMENSION);
        this.hero = hero;
        this.TYPE = TYPE;
        this.sequence = sequence;
        this.data = data;
        if (isReady()) {
            init();

        }
    }

    protected void reinit() {
        data = null;
        init();
    }

    protected void init() {
        removeAll();
        if (!ListMaster.isNotEmpty(data)) {
            initData();
        }
        addControls();
        addHeader();
        if (isInfoPanelNeeded()) {
            addInfoPanels();
        }
        addSortOptionComp();
        addFilterOptionComp();
        addSelectionPages();
        addBackgroundComps();
        revalidate();
    }

    public boolean isInfoPanelNeeded() {
        return false;
    }

    @Override
    protected boolean isAutoZOrder() {
        return true;
    }

    protected void addBackgroundComps() {
        VISUALS bg = getBackgroundVisuals();
        if (bg != null) {
            String POS = "id pages, pos " + getPagePosX() + "-" + getSelectionBgOffsetX() + " "
                    + getPagePosY() + "-" + getSelectionBgOffsetY();
            add(new G_Panel(bg), POS);
        }

    }

    protected VISUALS getBackgroundVisuals() {
        return VISUALS.CHOICE_VIEW_BG;
    }

    protected String getSelectionBgOffsetX() {
        return "50";
    }

    protected String getSelectionBgOffsetY() {
        return "40";
    }

    protected void addHeader() {
        add(new TextCompDC(null, getInfo()) {
            protected Font getDefaultFont() {
                return FontMaster.getFont(FONT.AVQ, getInfoFontSize(), Font.PLAIN);
            }

            ;
        }, getInfoHeaderPosition());

    }

    protected String getInfoHeaderPosition() {
        int offset = getBackgroundVisuals() == null ? 45 : 20;
        return "@pos center_x max_top-" + offset;
    }

    protected float getInfoFontSize() {
        return 28;
    }

    protected boolean isReady() {
        return true;
    }

    protected abstract void initData();

    protected void sortData() {
    }

    protected void addSortOptionComp() {
    }

    protected void addFilterOptionComp() {
    }

    protected void addFilterOptionListener() {
    }

    protected Class<?> getFilterOptionClass() {
        return null;
    }

    protected void addSelectionPages() {
        pages = createSelectionComponent();
        pages.setData(data);
        pages.refresh();
        String string = getBackgroundVisuals() == null ? "pages" : "pagesComp";
        String PAGE_POS = "id " + string + ", pos " + getPagePosX() + " " + getPagePosY();
        add(pages, PAGE_POS);
    }

    protected String getPagePosY() {
        return ""
                + MigMaster.getCenteredPosition((int) getPanelSize().getHeight(), pages
                .getPanelHeight());
    }

    protected int getPagePosX() {
        return MigMaster
                .getCenteredPosition((int) getPanelSize().getWidth(), pages.getPanelWidth());
    }

    protected PagedSelectionPanel<E> createSelectionComponent() {
        return new PagedSelectionPanel<>(this, getPageSize(), getItemSize(), getColumnsCount(),
                isVertical());
    }

    protected boolean isVertical() {
        return true;
    }

    protected void addInfoPanels() {
        Entity entity = null;
        if (data.size() > 0) {
            if (data.get(0) instanceof Entity) {
                entity = (Entity) data.get(0);
            }
        }
        infoPanel = new DC_PagedInfoPanel(entity);
        add(infoPanel, IP_POS);
        infoPanel.refresh();
    }

    public E getSelectedItem() {
        return data.get(getSelectedIndex());
    }

    public void itemIndexSelected(int i) {
        this.setIndex(i);
        if (i < 0 || data.size() <= i) {
            infoPanel.select(null);
            return;
        }
        E e = data.get(getSelectedIndex());
        if (e instanceof Entity && infoPanel != null) {
            infoPanel.select((Entity) e);
        }
        if (!isOkBlocked()) {
            okButton.setVisuals(VISUALS.FORWARD);
        } else {
            okButton.setVisuals(VISUALS.FORWARD_BLOCKED);
        }

    }

    public void itemSelected(E i) {
        itemIndexSelected(data.indexOf(i));
    }

    public boolean isOkBlocked() {
        return getSelectedIndex() < 0;
    }

    protected void ok() {
        // if (!isOkBlocked())
        sequence.selected(getSelectedIndex());
    }

    protected abstract void applyChoice();

    protected void back() {
        sequence.back();
    }

    protected void addControls() {
        backButton = new CustomButton(VISUALS.BACK) {
            public void handleClick() {
                back();
            }
        };
        add(backButton, getBackButtonPos());
        okButton = new CustomButton(VISUALS.FORWARD_BLOCKED) {
            @Override
            public boolean isEnabled() {
                return !isOkBlocked();
            }

            public void handleClick() {
                ok();
            }
        };
        add(okButton, getOkButtonPos());
    }

    protected String getOkButtonPos() {
        if (infoPanel == null) {
            return "@id ok, pos pages.x2+width "
                    + MigMaster.getCenteredHeight(okButton.getVisuals().getHeight());
        }
        return OK_BUTTON_POS;
    }

    protected String getBackButtonPos() {
        if (infoPanel == null) {
            return "@id back, pos pages.x-width "
                    + MigMaster.getCenteredHeight(backButton.getVisuals().getHeight());
        }
        return BACK_BUTTON_POS;
    }

    protected int getItemSize() {
        return DEFAULT_ITEM_SIZE;
    }

    protected int getPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    protected int getColumnsCount() {
        return DEFAULT_COLUMNS;
    }

    public List<E> getData() {
        return data;
    }

    public void activate() {
        if (!isReady()) {
            init();
        } else {
            initData();
        }
    }

    public int getSelectedIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public abstract String getInfo();

    public boolean checkBlocked(int index) {
        if (index == -1) {
            return true;
        }
        if (data.get(index) == null) {
            return true;
        }
        return checkBlocked(data.get(index));
    }

    public boolean checkBlocked(E e) {
        return false;
    }

    public PagedSelectionPanel<E> getPages() {
        return pages;
    }

    public void setSequence(ChoiceSequence choiceSequence) {
        this.sequence = choiceSequence;
    }

    public String getSorterOption() {
        return sorterOption;
    }

    public void setSorterOption(String string) {
        this.sorterOption = string;
        sortData();
        pages.setDirty(true);
        pages.setData(data);
        pages.refresh();
        // reinit();
    }

    public String getFilterOption() {
        return filterOption;
    }

    public void setFilterOption(String string) {
        SelectionPage page = (SelectionPage) pages.getCurrentComponent();
        this.filterOption = string;
        initData();
        sortData();

        pages = createSelectionComponent();
        pages.setDirty(true);
        pages.setData(data);
        pages.refresh();
        init();
        page = (SelectionPage) pages.getCurrentComponent();

        pages.revalidate();
        pages.repaint();
        page.revalidate();
        page.repaint();
        page.getList().repaint();
    }

}
