package main.client.cc.gui.tabs.operation;

import main.client.cc.gui.MainViewPanel.HERO_VIEWS;
import main.client.cc.gui.lists.ItemListManager;
import main.client.cc.gui.pages.HC_PagedListPanel;
import main.client.cc.gui.pages.HC_PagedListPanel.HC_LISTS;
import main.client.cc.gui.tabs.HeroItemTab;
import main.content.C_OBJ_TYPE;
import main.content.OBJ_TYPE;
import main.content.PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager.BORDER;

import java.util.List;

public class ItemSwapPanel extends HeroItemTab {

    private static final String TITLE = "";
    private DC_Obj obj;
    private List<ObjType> droppedItemData;
    private HC_PagedListPanel itemPool;
    private SwapItemManager swapManager;

    public ItemSwapPanel(DC_HeroObj hero, DC_Obj obj) {
        super(TITLE, null, hero);
        this.obj = obj;
        setSwapManager(new SwapItemManager(hero.getGame()));
        init();
    }

    @Override
    protected String getMainPosX() {
        return super.getMainPosX();
    }

    @Override
    protected String getMainPosY() {
        return "drop.y2+100";
    }

    protected void addComps() {
        Object pos = "id drop, pos " + super.getMainPosX() + " "
                + super.getMainPosY();
        add(itemPool, pos);
        super.addComps();
    }

    @Override
    protected void initData() {
        super.initData();
        droppedItemData = DataManager.toTypeList(StringMaster
                .openContainer(obj.getProperty(getPROP2())), getTYPE());

    }

    @Override
    public void refresh() {
        super.refresh();

    }

    public void resetComps() {
        super.resetComps();
        itemPool.setData(droppedItemData);
        itemPool.initPages();
        getItemManager().add(itemPool);
        itemPool.refresh();
    }

    @Override
    protected void initComps() {
        itemPool = new HC_PagedListPanel(HC_LISTS.INVENTORY, hero,
                getSwapManager(), droppedItemData, TITLE);
        getSwapManager().add(itemPool);
        super.initComps();
    }

    @Override
    protected ItemListManager getItemManager() {
        return getSwapManager();
    }

    protected boolean isReady() {
        return false;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return C_OBJ_TYPE.ITEMS;
    }

    @Override
    protected PROPERTY getPROP() {
        return PROPS.INVENTORY;
    }

    @Override
    protected PROPERTY getPROP2() {
        return PROPS.DROPPED_ITEMS;
    }

    @Override
    public BORDER getBorder(ObjType value) {
        return null;
    }

    @Override
    protected HERO_VIEWS getVIEW() {
        return null;
    }

    public SwapItemManager getSwapManager() {
        return swapManager;
    }

    public void setSwapManager(SwapItemManager swapManager) {
        this.swapManager = swapManager;
    }
}
