package eidolons.libgdx.gui.menu.selection.town.shops;

import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.menu.selection.SelectableItemDisplayer;
import eidolons.libgdx.gui.menu.selection.town.PlaceSelectionPanel;
import eidolons.macro.entity.shop.Shop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/18/2018.
 */
public class ShopSelectionPanel extends PlaceSelectionPanel {

    public ShopSelectionPanel() {
        //set size from parent
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        update();
    }

    @Override
    protected void setUserObjectForChildren(Object userObject) {
        listPanel.setUserObject(userObject);
    }

    @Override
    public void update() {
        getInfoPanel().update();
    }

    @Override
    public ShopPanel getInfoPanel() {
        return (ShopPanel) super.getInfoPanel();
    }

    protected String getTitle() {
        return "Available Shops";
    }

    @Override
    protected SelectableItemDisplayer createInfoPanel() {
        return new ShopPanel();
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new ShopsListPanel();
    }

    @Override
    protected List<SelectableItemData> createListData() {
        Collection<Shop> shops = (Collection<Shop>) getUserObject();
        if (shops == null) {
             main.system.auxiliary.log.LogMaster.log(1,"<<<<<<< Null UserObj on shops! " +this);
            return new ArrayList<>();
        }
        return shops.stream().map(shop -> {
            return new SelectableItemData(shop);
        }).collect(Collectors.toList());
    }

}
