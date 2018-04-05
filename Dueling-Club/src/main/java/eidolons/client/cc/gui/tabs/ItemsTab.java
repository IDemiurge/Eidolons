package eidolons.client.cc.gui.tabs;

import eidolons.client.cc.gui.MainViewPanel;
import eidolons.client.cc.gui.MainViewPanel.HERO_VIEWS;
import eidolons.client.cc.gui.lists.ItemListManager;
import eidolons.client.cc.gui.lists.dc.DC_InventoryManager;
import eidolons.client.cc.gui.misc.PoolComp;
import eidolons.client.cc.gui.tabs.lists.HeroItemSlots;
import eidolons.client.cc.gui.tabs.lists.JewelrySlots;
import eidolons.client.cc.gui.tabs.lists.QuickItemList;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import main.content.OBJ_TYPE;
import main.content.values.properties.PROPERTY;
import main.data.ability.construct.AbilityConstructor;
import main.entity.type.ObjType;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager.BORDER;

public class ItemsTab extends HeroItemTab {
    private static final String WEIGHT = PARAMS.C_CARRYING_WEIGHT.getName();
    private static final String POOL = "Total weight";
    private static final String QUICK_ID = "quickSlots";
    private static final String SLOTS_ID = "slots";
    private static final String JEWELRY_ID = "JEWELRY_ID";
    private HeroItemSlots slots;
    private PoolComp poolComp;
    private QuickItemList quickSlots;
    private JewelrySlots jewelrySlots;

    public ItemsTab(Unit hero) {
        this(null, hero);
    }

    public ItemsTab(MainViewPanel mvp, Unit hero) {
        super("Inventory", mvp, hero);
        if (hero.getGame().isSimulation()) {
            getItemManager().addRemoveList(pagedListPanel); // TODO
        }
    }

    public ItemsTab(Unit hero, DC_InventoryManager invListManager) {
        this(null, hero);

    }

    @Override
    protected ItemListManager getItemManager() {
//        if (game.isSimulation())
        return super.getItemManager();
//        return game.getInventoryTransactionManager().getInvListManager(); for swing
    }

    public void refresh() {
        super.refresh();
        updatePoolComp();
    }

    @Override
    public void activate() {
        AbilityConstructor.constructActives(hero);
        super.activate();
    }

    @Override
    protected void addComps() {

        addItemSlots();
        super.addComps();
        addJewelrySlots();
        addQuickSlots();
        addWeightPool();
        // list.addMouseListener(this);

    }


    private void addItemSlots() {
        if (slots == null) {
            slots = new HeroItemSlots(hero, getItemManager());
        } else {
            slots.refresh();
        }

        add(slots, "id " + SLOTS_ID + ", pos 0 0");

    }

    @Override
    public void setHero(Unit hero) {
        super.setHero(hero);
        jewelrySlots.setHero(hero);
        slots.setHero(hero);
        quickSlots.setHero(hero);
    }

    private void addJewelrySlots() {
        if (jewelrySlots == null) {
            jewelrySlots = new JewelrySlots(hero, getItemManager());
        } else {
            jewelrySlots.refresh();
        }

        int y = GuiManager.getFullObjSize();
        // if (actionPanelVisible)
        // y+=ACTION_PANEL_OFFSET_Y;
        add(jewelrySlots, "id " + JEWELRY_ID + ", @pos center_x " + y);
    }

    @Override
    protected String getMainPosY() {
        return QUICK_ID + ".y2";
    }

    private void addQuickSlots() {
        if (quickSlots == null) {
            quickSlots = new QuickItemList(hero, getItemManager());
        } else {
            quickSlots.refresh();
        }

        add(quickSlots, "id " + QUICK_ID + ", @pos center_x " + JEWELRY_ID + ".y2-15");
    }

    private void addWeightPool() {
        initPoolComp();
        add(poolComp, "pos @center_x-7 " + LIST_ID + ".y2 ");
    }

    protected void updatePoolComp() {
        if (poolComp == null) {
            initPoolComp();
        }
        poolComp.setText(getWeightString());

    }

    private String getWeightString() {
        return hero.getParam(WEIGHT) + "" + "/" + hero.getParam(PARAMS.CARRYING_CAPACITY) + "lb";
    }

    protected void initPoolComp() {
        poolComp = new PoolComp(getWeightString());
        poolComp.setVisuals(VISUALS.POOL);
        poolComp.setToolTipText(POOL);
        updatePoolComp();
    }

    @Override
    protected boolean isResponsive() {
        return super.isResponsive();
    }

    @Override
    protected boolean isVertical() {
        return true;
    }

    @Override
    public BORDER getBorder(ObjType value) {
        return null;
    }

    @Override
    protected HERO_VIEWS getVIEW() {
        return HERO_VIEWS.SHOP;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return null;
    }

    @Override
    protected PROPERTY getPROP() {
        return PROPS.INVENTORY;
    }

    @Override
    protected PROPERTY getPROP2() {
        return PROPS.QUICK_ITEMS;
    }


}
