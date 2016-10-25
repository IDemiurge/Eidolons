package main.client.cc.gui.lists;

import main.client.cc.gui.pages.HC_PagedListPanel;
import main.client.dc.Launcher;
import main.content.OBJ_TYPE;
import main.content.properties.MACRO_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.elements.Filter;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.game.logic.macro.MacroGame;
import main.game.logic.macro.town.Shop;
import main.game.logic.macro.town.Town;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.StringMaster;

import java.util.*;

public class ShopListsPanel extends VendorListsPanel {

    public ShopListsPanel(DC_HeroObj hero, OBJ_TYPE TYPE, PROPERTY prop,
                          boolean responsive, boolean showAll, ItemListManager manager) {
        super(hero, TYPE, prop, responsive, showAll, manager);
        refresh();
    }

    public ShopListsPanel(DC_HeroObj hero, OBJ_TYPE TYPE, PROPERTY prop,
                          boolean responsive, boolean showAll, ItemListManager manager,
                          Filter<ObjType> typeFilter) {
        super(hero, TYPE, prop, responsive, showAll, manager, typeFilter);
        refresh();
    }

    // to be used
    public Map<String, HC_PagedListPanel> initListMap(String tabName) {
        if (!Launcher.getMainManager().isMacroMode())
            return super.initListMap(tabName);
        if (getTown() == null) {
            return new HashMap<>();
        }
        Map<String, HC_PagedListPanel> map = new XLinkedMap<>();
        List<ObjType> items = new LinkedList<>();
        Shop shop = getShop(tabName);
        shop.setVendorPanel(this);
        for (ObjType t : shop.getItems()) {
            items.add(t);
        }
        for (String group : getListGroup(tabName)) {
            List<ObjType> data = new LinkedList<>();
            for (ObjType t : items) {
                if (checkType(t, group, shop))
                    data.add(t);
            }
            if (!data.isEmpty())
                putList(group, data, map);
        }
        // sorting?
        return map;
    }

    private boolean checkType(ObjType t, String group, Shop shop) {
        // if (!shop.isSubgrouping())
        if (t.getGroupingKey().equalsIgnoreCase(group))
            return true;
        if (t.getSubGroupingKey().equalsIgnoreCase(group))
            return true;
        if (t.getProperty(shop.getShopType().getFilterProp()).equalsIgnoreCase(
                group))
            return true;

        return false;
    }

    protected void addTab(String title) {
        if (!Launcher.getMainManager().isMacroMode()) {
            super.addTab(title);
            return;
        }

        G_Panel comp = new G_Panel();
        tabMap.put(title, comp);

        tabs.addTab(title, getShop(title).getImagePath(), comp);
    }

    private Shop getShop(String tabName) {
        return getTown().getShop(tabName);
    }

    @Override
    protected List<String> getTabGroups() {
        if (!Launcher.getMainManager().isMacroMode())
            return super.getTabGroups();
        if (getTown() == null)
            return new LinkedList<>();
        return DataManager.toStringList(getTown().getShops());
    }

    protected List<String> getListGroup(String tabName) {
        if (!Launcher.getMainManager().isMacroMode())
            return super.getListGroup(tabName);
        Shop shop = getTown().getShop(tabName);
        if (shop.getProperty(MACRO_PROPS.SHOP_ITEM_GROUPS).isEmpty()) {
            List<String> list = new LinkedList<>();
            for (String s : Arrays.asList(shop.getShopType().getItem_groups())) {
                list.add(StringMaster.getWellFormattedString(s));
            }
            return list;
        }
        return StringMaster.openContainer(shop
                .getProperty(MACRO_PROPS.SHOP_ITEM_GROUPS));
    }

    public Town getTown() {
        return (MacroGame.getGame().getPlayerParty().getTown());
    }

}
