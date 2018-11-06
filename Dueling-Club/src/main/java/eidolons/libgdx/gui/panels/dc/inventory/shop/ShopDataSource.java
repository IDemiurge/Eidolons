package eidolons.libgdx.gui.panels.dc.inventory.shop;

import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.dc.inventory.InvItemActor;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryFactory;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerClickHandler;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerDataSource;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerPanel.ITEM_FILTERS;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.EquipDataSource;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryTableDataSource;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.QuickSlotDataSource;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMasterDirect;
import eidolons.libgdx.gui.panels.headquarters.datasource.inv.ShopValueContainerList;
import eidolons.libgdx.texture.TextureCache;
import eidolons.macro.entity.town.Shop;
import main.system.auxiliary.data.ListMaster;

import java.util.*;
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
    private static Map<Shop, ShopClickHandler> handlerCache = new HashMap<>();
    Shop shop;
    InventoryDataSource invDataSource;
    private ITEM_FILTERS filter;
    private Set<DC_HeroItemObj> stash;

    public ShopDataSource(Shop shop, Unit unit) {
        super(shop, unit);
        this.shop = shop;
        stash = shop.getTown().getStash();
        items = new ArrayList<>(shop.getItems());
        invDataSource = new InventoryDataSource(unit){
            @Override
            protected HqDataMaster getDataMaster(Unit unit) {
                return HqDataMasterDirect.getOrCreateInstance( unit);
            }
        };
        handler = createClickHandler();
        factory = new InventoryFactory(handler);
        invDataSource.setFactory(factory);
    }

    @Override
    protected ContainerClickHandler createClickHandler() {
        if (shop == null) {
            return null;
        }
        ContainerClickHandler  handler = handlerCache.get(shop);
        if (handler == null) {
            handler = new ShopClickHandler(shop, unit);
            handlerCache.put(shop, (ShopClickHandler) handler);
        }
        return handler;
    }

    @Override
    public void setFilter(ITEM_FILTERS filter) {
        this.filter = filter;
    }

    public InventoryDataSource getInvDataSource() {
        return invDataSource;
    }

    @Override
    public List<InvItemActor> getQuickSlots() {
        return invDataSource.getQuickSlots();
    }

    @Override
    public List<InvItemActor> getInventorySlots() {
        return invDataSource.getInventorySlots();
    }

    public List<InvItemActor> getShopSlots() {
        ListMaster.fillWithNullElements(items
         , SIZE);
        return factory.getList(items, CELL_TYPE.CONTAINER);
    }

    public List<InvItemActor> getStashSlots() {
        ListMaster.fillWithNullElements(items
         , SIZE);
        return factory.getList(stash, CELL_TYPE.STASH);
    }

    @Override
    public InvItemActor mainWeapon() {
        return invDataSource.mainWeapon();
    }

    public Shop getShop() {
        return shop;
    }

    @Override
    public InvItemActor offWeapon() {
        return invDataSource.offWeapon();
    }

    @Override
    public InvItemActor armor() {
        return invDataSource.armor();
    }

    @Override
    public InvItemActor avatar() {
        return invDataSource.avatar();
    }

    @Override
    public InvItemActor amulet() {
        return invDataSource.amulet();
    }

    @Override
    public List<InvItemActor> rings() {
        return invDataSource.rings();
    }

    public ShopClickHandler getHandler() {
        return (ShopClickHandler) handler;
    }


    public List<ShopValueContainerList> getGroupLists(String tabName) {
        return shop.getItemSubgroups(tabName).stream().map(
         s -> new ShopValueContainerList(getTextures(s))).
         collect(Collectors.toList());
    }
    public String getPricesInfo() {
        return shop.getPrice(100, invDataSource.getUnit(), true)+
         "%";
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

    public int getPrice(DC_HeroItemObj model, CELL_TYPE cellType) {
        return shop.getPrice(model, invDataSource.getUnit(), cellType == CELL_TYPE.CONTAINER);
    }
}
