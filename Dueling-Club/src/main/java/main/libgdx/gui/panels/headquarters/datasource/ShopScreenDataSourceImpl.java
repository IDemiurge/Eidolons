package main.libgdx.gui.panels.headquarters.datasource;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.meta.scenario.hq.ShopInterface;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.inventory.InventoryClickHandler;
import main.libgdx.gui.panels.dc.inventory.InventoryValueContainer;
import main.libgdx.gui.panels.dc.inventory.datasource.EquipDataSource;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryTableDataSource;
import main.libgdx.gui.panels.dc.inventory.datasource.QuickSlotDataSource;
import main.libgdx.gui.panels.headquarters.ShopValueContainerList;
import main.libgdx.gui.panels.headquarters.ShopTabbedPanel;
import main.libgdx.texture.TextureCache;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 5/21/2017.
 */
public class ShopScreenDataSourceImpl implements
 ShopDataSource,
 QuickSlotDataSource,
 InventoryTableDataSource,
 EquipDataSource
{
    ShopInterface shop;
    InventoryDataSource dataSource;
    public ShopScreenDataSourceImpl(ShopInterface shop, Unit unit) {
        this.shop = shop;
        dataSource = new InventoryDataSource(unit);
    }

    @Override
    public List<InventoryValueContainer> getQuickSlots() {
        return dataSource.getQuickSlots();
    }

    @Override
    public List<InventoryValueContainer> getInventorySlots() {
        return dataSource.getInventorySlots();
    }

    @Override
    public InventoryValueContainer mainWeapon() {
        return dataSource.mainWeapon();
    }

    @Override
    public InventoryValueContainer offWeapon() {
        return dataSource.offWeapon();
    }

    @Override
    public InventoryValueContainer armor() {
        return dataSource.armor();
    }

    @Override
    public InventoryValueContainer avatar() {
        return dataSource.avatar();
    }

    @Override
    public InventoryValueContainer amulet() {
        return dataSource.amulet();
    }

    @Override
    public List<InventoryValueContainer> rings() {
        return dataSource.rings();
    }

    public InventoryClickHandler getHandler() {
        return dataSource.getHandler();
    }

    public ClickListener getDoneHandler() {
        return dataSource.getDoneHandler();
    }

    public ClickListener getUndoHandler() {
        return dataSource.getUndoHandler();
    }

    public ClickListener getCancelHandler() {
        return dataSource.getCancelHandler();
    }

    public boolean isDoneDisabled() {
        return dataSource.isDoneDisabled();
    }

    public boolean isCancelDisabled() {
        return dataSource.isCancelDisabled();
    }

    public boolean isUndoDisabled() {
        return dataSource.isUndoDisabled();
    }

    public String getOperationsString() {
        return dataSource.getOperationsString();
    }

    @Override
    public ShopTabbedPanel getTabs() {
        ShopTabbedPanel tabs =  new ShopTabbedPanel();
        for (String tab : shop.getTabs()) {
            tabs.addTab(new ShopPage(getGroupLists(tab)), tab);
        }
        return tabs;
    }

    @Override
    public List<ShopValueContainerList> getGroupLists(String tabName) {
        return shop.getItemSubgroups(tabName).stream().map(
         s-> new ShopValueContainerList(getTextures(s))).
         collect(Collectors.toList());
    }

    @Override
    public List<ValueContainer> getTextures(String groupList)
    {
        return shop.getItems(groupList).stream().map(
         s-> new ValueContainer(TextureCache.getOrCreateR(s))).
         collect(Collectors.toList());
    }

    @Override
    public ValueContainer getName() {
        return new ValueContainer(shop.getName(),"");
    }

    @Override
    public ValueContainer getGold() {
        return new ValueContainer("Gold: ",shop.getGold());
    }

    @Override
    public ValueContainer getIcon() {
        return new ValueContainer(TextureCache.getOrCreateR(shop.getImagePath()), "");
    }
}
