package main.game.battlecraft.logic.meta.scenario.hq;

import main.client.cc.logic.items.ItemGenerator;
import main.client.cc.logic.items.ItemMaster;
import main.content.CONTENT_CONSTS2.SHOP_LEVEL;
import main.content.CONTENT_CONSTS2.SHOP_MODIFIER;
import main.content.CONTENT_CONSTS2.SHOP_TYPE;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.content.enums.entity.ItemEnums.QUALITY_LEVEL;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.elements.conditions.PropCondition;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.game.module.adventure.town.ShopMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.entity.FilterMaster;
import main.system.math.MathMaster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by JustMe on 5/22/2017.
 */
public class HqShop extends LightweightEntity implements ShopInterface {

    private static final int MAX_ITEM_GROUPS = 6;
    List<ObjType> items;
    private SHOP_TYPE shopType;
    private SHOP_LEVEL shopLevel;
    private SHOP_MODIFIER shopModifier;
    private int goldToSpend = 100;
    private int spareGold = 20;


    public HqShop(ObjType type) {
        super(type);
        initItems();
    }

    @Override
    public List<String> getTabs() {
        return StringMaster.openContainer(getProperty(PROPS.SHOP_ITEM_GROUPS));
    }

    @Override
    public List<String> getItemSubgroups(String tabName) {
        return null;
    }

    // why not return objTypes? strings are fallible...
    // why not OBJECTS? is it hard? is it better?
    @Override
    public List<String> getItems(String groupList) {
        if (isFullRepertoire()) {
            //filters!
        }
        List<String> list = new ArrayList<>();
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
        return DataManager.toTypeList(StringMaster.openContainer(getProperty(PROPS.SHOP_ITEMS)),
         TYPE);
    }

    private boolean isFullRepertoire() {
        return false;
    }

    @Override
    public String getGold() {
        return getParam(PARAMS.GOLD);
    }


    //                                  <><><><><>


    public void sellItem(ObjType t, int price) {
        items.remove(t);
        // some should be infinite though... perhaps based on shop level?
        modifyParameter(PARAMS.GOLD, price);

    }

    public boolean buyItem(ObjType t) {
        Integer cost = t.getIntParam(PARAMS.GOLD_COST);

        cost = MathMaster.addFactor(cost, getIntParam(PARAMS.GOLD_MOD));

        if (cost > getIntParam(PARAMS.GOLD) * goldToSpend / 100) {
            return false;
        }
        buyItem(t, cost);

        return true;
    }

    public void buyItem(ObjType type, int cost) {
        modifyParameter(PARAMS.GOLD, -cost);
        items.add(type);
    }

    public Collection<? extends ObjType> getItems() {
        if (items == null) {
            try {
                initItems();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        return items;
    }

//    @Override
//    public void toBase() {
//        super.toBase();
//        setProperty(PROPS.SHOP_ITEMS,
//         StringMaster.constructContainer(DataManager
//          .toStringList(getItems())));
//
//    }

    private boolean isRandomized() {
        return false;
    }

    private boolean isPreset() {
        return false;
    }

    private void initItems() {

        items = new ArrayList<>();
        if (isPreset()) {
            items = DataManager.toTypeList(StringMaster
              .openContainer(getProperty(PROPS.SHOP_ITEMS)),
             C_OBJ_TYPE.ITEMS);
            return;
        }
        List<ObjType> templates = DataManager.toTypeList(StringMaster
          .openContainer(getProperty(PROPS.SHOP_ITEM_TEMPLATES)),
         C_OBJ_TYPE.ITEMS);
        items = getItemsFromTemplates(templates);

        if (getShopType() == null)
            return;
        // addStandardItems(); then randomize
        PROPERTY prop = getShopType().getFilterProp();
        int i = 0;
        String[] item_groups = getShopType().getItem_groups();

        for (String group : item_groups) {
            List<ObjType> pool;
            if (prop == null) {
                pool = DataManager.toTypeList(DataManager
                  .getTypesSubGroupNames(C_OBJ_TYPE.ITEMS, group),
                 C_OBJ_TYPE.ITEMS);
            } else {
                pool = ItemGenerator.getBaseTypes(C_OBJ_TYPE.ITEMS);
                FilterMaster.filter(pool, new PropCondition(prop, group));
            }
            pool = constructPool(pool);

            pool.addAll(getSpecialItems(group));

            i++;
            if (!isRandomized()) {
                for (ObjType item : pool) {
                    buyItem(item, 0);
                }
//buyAll();
                continue;
            }
            goldToSpend = (100 - spareGold - i * 5) / item_groups.length; //
            // some params from Shop ObjType?
            Loop.startLoop(ShopMaster.getMaxItemsPerGroup(this));
            while (!Loop.loopEnded() && !pool.isEmpty()) {
                int randomListIndex = RandomWizard.getRandomListIndex(pool);
                ObjType t = pool.get(randomListIndex);
                if (t == null) {
                    continue;
                }
                if (!buyItem(t)) {
                    break; // second loop based on cheapest items?
                }
            }
        }
    }


    // generateCustomItems(); randomly based on the repertoire spectrum

    private Collection<? extends ObjType> getSpecialItems(String group) {
        List<ObjType> list = new ArrayList<>();
        // getProperty(PROPS.shop_special_items);
        // if (t.getp).equals(group) list.add(t);
        return list;
    }

    private List<ObjType> getItemsFromTemplates(List<ObjType> templates) {
        List<ObjType> generated = new ArrayList<>();
        for (ObjType t : templates) {
            for (MATERIAL material : ItemEnums.MATERIAL.values()) {
                for (QUALITY_LEVEL q : ShopMaster.getQualityLevels(this)) {
                    if (!ShopMaster.checkMaterialAllowed(this, material)) {
                        continue;
                    }
                    generated.add(ItemGenerator.getGeneratedItem(t,
                     material, q));
                }
            }
        }
        return generated;
    }

    private List<ObjType> constructPool(List<ObjType> pool) {

        // TODO AND WHAT ABOUT NON-SLOT ITEMS?
        List<ObjType> filtered = new ArrayList<>();
        for (MATERIAL material : ItemEnums.MATERIAL.values()) {
            if (ShopMaster.checkMaterialAllowed(this, material)) {
                for (ObjType t : pool) {
                    if (!ItemMaster.checkMaterial(t, material.getGroup())) {
                        continue;
                    }
                    for (QUALITY_LEVEL q : ShopMaster.getQualityLevels(this)) {
                        filtered.add(ItemGenerator.getGeneratedItem(t,
                         material, q));
                    }
                }

            }
        }

        // getShopLevel().getMaxCostFactor();

        return filtered;
    }

    @Override
    public SHOP_TYPE getShopType() {
        if (shopType == null) {
            shopType = new EnumMaster<SHOP_TYPE>().retrieveEnumConst(
             SHOP_TYPE.class, getProperty(PROPS.SHOP_TYPE));
        }
        return shopType;
    }

    @Override
    public SHOP_LEVEL getShopLevel() {
        if (shopLevel == null) {
            shopLevel = new EnumMaster<SHOP_LEVEL>().retrieveEnumConst(
             SHOP_LEVEL.class, getProperty(PROPS.SHOP_LEVEL));
        }
        return shopLevel;
    }

    @Override
    public SHOP_MODIFIER getShopModifier() {
        if (shopModifier == null) {
            shopModifier = new EnumMaster<SHOP_MODIFIER>()
             .retrieveEnumConst(SHOP_MODIFIER.class,
              getProperty(PROPS.SHOP_MODIFIER));
        }
        return shopModifier;
    }

}
