package main.libgdx.gui.panels.headquarters.datasource;

import main.game.battlecraft.logic.meta.scenario.hq.ShopInterface;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import main.libgdx.gui.panels.headquarters.ShopListPanel;
import main.libgdx.gui.panels.headquarters.ShopTabbedPanel;
import main.libgdx.texture.TextureCache;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 5/21/2017.
 */
public class ShopDatasourceImpl implements ShotDatasource
{
    ShopInterface shop;
    InventoryDataSource dataSource;
    public ShopDatasourceImpl(ShopInterface shop) {
        this.shop = shop;
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
    public List<ShopListPanel> getGroupLists(String tabName) {
        return shop.getItemSubgroups(tabName).stream().map(
         s-> new ShopListPanel(getTextures(s))).
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
