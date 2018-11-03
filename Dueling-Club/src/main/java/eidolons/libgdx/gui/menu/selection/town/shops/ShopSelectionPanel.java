package eidolons.libgdx.gui.menu.selection.town.shops;

import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.menu.selection.SelectableItemDisplayer;
import eidolons.libgdx.gui.menu.selection.town.PlaceSelectionPanel;
import eidolons.macro.entity.town.Shop;

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
        return new ShopPanel( );
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new ShopsListPanel();
    }
    @Override
    protected List<SelectableItemData> createListData() {
        Collection<Shop> shops = (Collection< Shop>) getUserObject();
        return shops.stream().map(shop -> {
            SelectableItemData item =
             new SelectableItemData(shop);
            return item;
        }).collect(Collectors.toList()) ;
    }

}
