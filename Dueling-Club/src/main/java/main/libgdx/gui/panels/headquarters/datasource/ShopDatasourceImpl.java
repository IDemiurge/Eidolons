package main.libgdx.gui.panels.headquarters.datasource;

import main.game.module.adventure.town.Shop;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.List;

/**
 * Created by JustMe on 5/21/2017.
 */
public class ShopDatasourceImpl implements ShotDatasource{
    Shop shop;

    public ShopDatasourceImpl(Shop shop) {
        this.shop = shop;
    }


    @Override
    public List<TablePanel> getTabs() {
        return null;
    }

    @Override
    public List<TablePanel> getGroupLists(String tabName) {
        return null;
    }

    @Override
    public List<ValueContainer> getTextures(String groupList) {
        return null;
    }

    @Override
    public ValueContainer getName() {
        return null;
    }

    @Override
    public ValueContainer getGold() {
        return null;
    }

    @Override
    public ValueContainer getIcon() {
        return null;
    }
}
