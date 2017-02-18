package main.client.cc.gui.tabs;

import main.client.cc.gui.MainPanel;
import main.client.cc.gui.MainViewPanel;
import main.client.cc.gui.MainViewPanel.HERO_VIEWS;
import main.client.cc.gui.lists.ItemListManager;
import main.client.cc.gui.misc.BorderChecker;
import main.client.cc.gui.pages.HC_PagedListPanel;
import main.client.cc.gui.pages.HC_PagedListPanel.HC_LISTS;
import main.client.cc.gui.views.HeroItemView;
import main.content.OBJ_TYPE;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;

import java.util.List;

public abstract class HeroItemTab extends HeroPanelTab implements BorderChecker {
    protected static final String LIST_ID = "mainlist";
    protected List<ObjType> data;
    protected HC_PagedListPanel pagedListPanel;

    // HC_LIST list

    public HeroItemTab(String title, MainViewPanel mvp, Unit hero) {
        super(title, mvp, hero);
        setPanelSize(MainPanel.SIZE);
        if (isReady()) {
            init();
        }
    }

    protected void init() {
        initData();
        initComps();
        addComps();
        new Thread(new Runnable() {
            public void run() {
                setBorderChecker();
            }
        }).start();
    }

    protected boolean isReady() {
        return true;
    }

    public void activate() {
        if (getItemManager() != null) {
            getItemManager().setHeroList(pagedListPanel);
        }
        resetComps();
    }

    protected void setBorderChecker() {
        pagedListPanel.setBorderChecker(this);
    }

    public void initView() {
        setLinkedView(getVIEW());
    }

    @Override
    public void refresh() {
        removeAll();
        initData();
        resetComps();
        addComps();
        revalidate();
        repaint();

    }

    public void resetComps() {
        if (isReinitDataRequired() || isDirty()) {
            initData();
        }

        pagedListPanel.setData(data);
        pagedListPanel.initPages();
        getItemManager().add(pagedListPanel);
        pagedListPanel.refresh();
        if (pagedListPanel.getParent() == null) {
            addComps();
        }
    }

    protected ItemListManager getItemManager() {
        return ((HeroItemView) mvp.getView(getVIEW())).getItemManager();
    }

    protected void initComps() {
        pagedListPanel = new HC_PagedListPanel(getTemplate(), hero,
                getItemManager(), data, title);

    }

    protected void addComps() {

        if (getItemManager() != null) {
            getItemManager().add(pagedListPanel);
        }

        // add(new JLabel(title), "id title, pos" + getMainPosX() + " "
        // + getMainPosY());
        add(pagedListPanel, "id " + LIST_ID + ", pos " + getMainPosX() + " "
                        + getMainPosY()
                // + "title.y2"
        );
    }

    protected HC_LISTS getTemplate() {
        return HC_LISTS.INVENTORY;
    }

    protected String getMainPosX() {
        return "@center_x-5";
    }

    protected boolean isVertical() {
        return true;
    }

    protected boolean isResponsive() {
        return true;
    }

    protected String getMainPosY() {
        return "0";
    }

    protected void initData() {
        data = DataManager.toTypeList(StringMaster.openContainer(hero
                .getProperty(getPROP())), getTYPE());
        // if (!ListMaster.isNotEmpty(data) && !game.isSimulation())
        // data = DataManager
        // .convertToTypeList(StringMaster.openContainer(hero
        // .getType().getProperty(getPROP())), getTYPE());
    }

    protected abstract HERO_VIEWS getVIEW();

    protected abstract OBJ_TYPE getTYPE();

    protected abstract PROPERTY getPROP();

    protected abstract PROPERTY getPROP2();

}
