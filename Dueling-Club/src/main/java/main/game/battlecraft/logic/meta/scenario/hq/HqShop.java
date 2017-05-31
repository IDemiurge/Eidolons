package main.game.battlecraft.logic.meta.scenario.hq;

import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.entity.FilterMaster;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 5/22/2017.
 */
public class HqShop extends LightweightEntity implements ShopInterface{

    public HqShop(ObjType type) {
        super(type);
    }

    @Override
    public List<String> getTabs() {
        return  StringMaster.openContainer(getProperty(MACRO_PROPS.SHOP_ITEM_GROUPS) );
    }

    @Override
    public List<String> getItemSubgroups(String tabName) {
        return null;
    }

    // why not return objTypes? strings are fallible...
    // why not OBJECTS? is it hard? is it better?
    @Override
    public List<String> getItems(String groupList) {
        if (isFullRepertoire()){
            //filters!
        }
         List<String> list = new LinkedList<>();
        for (DC_TYPE TYPE : C_OBJ_TYPE.ITEMS.getTypes()) {
            List<ObjType> items = getItems(TYPE);
            items.removeIf(item -> item == null);
            FilterMaster.filterByPropJ8(items, TYPE.getGroupingKey().getName(), groupList);
            items.forEach(item -> list.add(item.getName()));
        }
        //sort
        return list;
    }

    public List<ObjType> getItems(DC_TYPE TYPE) {
        return DataManager.toTypeList(StringMaster.openContainer(getProperty(MACRO_PROPS.SHOP_ITEMS)),
         TYPE) ;
    }
    private boolean isFullRepertoire() {
        return false;
    }

    @Override
    public String getGold() {
        return getParam(PARAMS.GOLD);
    }

    @Override
    public String getIcon() {
        return getImagePath();
    }
}
