package eidolons.libgdx.gui.panels.dc.inventory.shop;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.scenario.hq.ShopInterface;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryFactory;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryValueContainer;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerClickHandler;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerDataSource;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerPanel.ITEM_FILTERS;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.EquipDataSource;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryTableDataSource;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.QuickSlotDataSource;
import eidolons.libgdx.gui.panels.headquarters.datasource.inv.ShopValueContainerList;
import eidolons.libgdx.texture.TextureCache;
import eidolons.macro.entity.town.Shop;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 5/21/2017.
 */
public class ShopDataSource extends ContainerDataSource
 implements
 QuickSlotDataSource,
 InventoryTableDataSource,
 EquipDataSource {
    private static final int SIZE = 24;
    ShopInterface shop;
    InventoryDataSource invDataSource;
    private ITEM_FILTERS filter;

    @Override
    public void setFilter(ITEM_FILTERS filter) {
        this.filter = filter;
    }

    public ShopDataSource(Shop shop, Unit unit) {
        super(shop, unit);
        this.shop = shop;
        items = new ArrayList<>(shop.getItems());
        invDataSource = new InventoryDataSource(unit);
        handler = new ShopClickHandler(shop, unit);
        factory = new InventoryFactory(handler);
        invDataSource.setFactory(factory);
    }

    public InventoryDataSource getInvDataSource() {
        return invDataSource;
    }

    @Override
    public List<InventoryValueContainer> getQuickSlots() {
        return invDataSource.getQuickSlots();
    }

    @Override
    public List<InventoryValueContainer> getInventorySlots() {
        ListMaster.fillWithNullElements(items
         , SIZE);
        return factory.getList(items, CELL_TYPE.CONTAINER);
    }

    @Override
    public InventoryValueContainer mainWeapon() {
        return invDataSource.mainWeapon();
    }

    @Override
    public InventoryValueContainer offWeapon() {
        return invDataSource.offWeapon();
    }

    @Override
    public InventoryValueContainer armor() {
        return invDataSource.armor();
    }

    @Override
    public InventoryValueContainer avatar() {
        return invDataSource.avatar();
    }

    @Override
    public InventoryValueContainer amulet() {
        return invDataSource.amulet();
    }

    @Override
    public List<InventoryValueContainer> rings() {
        return invDataSource.rings();
    }

    public ContainerClickHandler getHandler() {
        return handler;
    }


    public boolean isDoneDisabled() {
        return invDataSource.isDoneDisabled();
    }

    public boolean isCancelDisabled() {
        return invDataSource.isCancelDisabled();
    }

    public boolean isUndoDisabled() {
        return invDataSource.isUndoDisabled();
    }

    public String getOperationsString() {
        return invDataSource.getOperationsString();
    }

//    @Override
//    public ShopTabbedPanel getTabs() {
//        ShopTabbedPanel tabs = new ShopTabbedPanel();
//        for (String tab : shop.getTabs()) {
//            tabs.addTab(new ShopPage(getGroupLists(tab)), tab);
//        }
//        return tabs;
//    }

    public List<ShopValueContainerList> getGroupLists(String tabName) {
        return shop.getItemSubgroups(tabName).stream().map(
         s -> new ShopValueContainerList(getTextures(s))).
         collect(Collectors.toList());
    }

    public List<ValueContainer> getTextures(String groupList) {
        return shop.getItems(groupList).stream().map(
         s -> new ValueContainer(TextureCache.getOrCreateR(s))).
         collect(Collectors.toList());
    }

    public ValueContainer getName() {
        return new ValueContainer(shop.getName(), "");
    }

    public ValueContainer getGold() {
        return new ValueContainer("Gold: ", shop.getGold());
    }

    public ValueContainer getIcon() {
        return new ValueContainer(TextureCache.getOrCreateR(shop.getImagePath()), "");
    }
}
