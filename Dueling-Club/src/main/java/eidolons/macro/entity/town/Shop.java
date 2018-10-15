package eidolons.macro.entity.town;

import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.ItemFactory;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.meta.scenario.hq.ShopInterface;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.adventure.utils.SaveMasterOld;
import eidolons.game.module.herocreator.logic.items.ItemGenerator;
import eidolons.game.module.herocreator.logic.items.ItemMaster;
import eidolons.macro.MacroGame;
import main.content.CONTENT_CONSTS2.SHOP_LEVEL;
import main.content.CONTENT_CONSTS2.SHOP_MODIFIER;
import main.content.CONTENT_CONSTS2.SHOP_TYPE;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.content.enums.entity.ItemEnums.QUALITY_LEVEL;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.MACRO_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.xml.XML_Formatter;
import main.data.xml.XML_Writer;
import main.elements.conditions.PropCondition;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.log.LogMaster;
import main.system.entity.FilterMaster;
import main.system.math.MathMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Shop extends TownPlace implements ShopInterface {
    private static final int MAX_ITEM_GROUPS = 4;
    List<DC_HeroItemObj> items;
    private SHOP_TYPE shopType;
    private SHOP_LEVEL shopLevel;
    private SHOP_MODIFIER shopModifier;
    private int goldToSpendPercentage = 100;
    private int spareGoldPercentage = 20;

    public Shop(MacroGame game, ObjType type, Ref ref) {
        super(game, type, ref);
    }

    @Override
    public String getSaveData() {
        String s = "";
        for (PROPERTY p : propMap.keySet()) {
            if (SaveMasterOld.isPropSaved(p, getOBJ_TYPE_ENUM())) {
                s += XML_Writer.openXML(p.getName())
                 + XML_Formatter.formatStringForXmlNodeName(getProperty(p))
                 + XML_Writer.closeXML(p.getName());
            }
        }
        return s;
    }

    private void initItems() {
        if (!getProperty(MACRO_PROPS.SHOP_ITEMS).isEmpty()) {
            DataManager.toTypeList(ContainerUtils
              .openContainer(getProperty(MACRO_PROPS.SHOP_ITEMS)),
             C_OBJ_TYPE.ITEMS);
        }
        if (getIntParam(PARAMS.GOLD) == 0) {
            setParam(PARAMS.GOLD, ShopMaster.getBaseGold(this));
        }
        if (getIntParam(PARAMS.GOLD_MOD) == 0) {
            setParam(PARAMS.GOLD_MOD, ShopMaster.getBaseGoldCostMod(this), true);
        }

        if (getIntParam(MACRO_PARAMS.SHOP_INCOME) == 0) {
            setParam(MACRO_PARAMS.SHOP_INCOME,
             ShopMaster.getBaseGoldIncome(this), true);
        }
        items = new ArrayList<>();
        // addStandardItems(); then randomize
        PROPERTY prop = getShopType().getFilterProp();
        int i = 0;

        String[] item_groups = getShopType().getItem_groups();
        // Up to 4 item groups!
        if (item_groups.length > MAX_ITEM_GROUPS) {
            List<String> list = new ArrayList<>(Arrays.asList(item_groups));
            item_groups = new String[MAX_ITEM_GROUPS];
            int j = 0;
            while (j < MAX_ITEM_GROUPS) {
                String e = list.get(RandomWizard.getRandomIndex(list));
                list.remove(e);
                item_groups[j] = e;
                j++;
            }
        }
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
            goldToSpendPercentage = (100 - spareGoldPercentage - i * 5)
             / item_groups.length; //
            // some params from Shop ObjType?
            int max = ShopMaster.getMaxItemsPerGroup(this);
            Loop.startLoop(pool.size());
            while (!Loop.loopEnded() && !pool.isEmpty() && max > 0) {
                int randomListIndex = RandomWizard.getRandomIndex(pool);
                ObjType t = pool.get(randomListIndex);
                if (t == null) {
                    continue;
                } //TODO check first, then create!!!
                DC_HeroItemObj item = ItemFactory.createItemObj(t, DC_Player.NEUTRAL,
                 DC_Game.game, new Ref(), false); // MacroGame.getGame()!
                if (buyItem(item)) {
                    max--; // second loop based on cheapest items?
                }
            }
        }
    }

    // generateCustomItems(); randomly based on the repertoire spectrum

    private Collection<? extends ObjType> getSpecialItems(String group) {
        List<ObjType> list = new ArrayList<>();
        // getProperty(MACRO_PROPS.shop_special_items);
        // if (t.getp).equals(group) list.add(t);
        return list;
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
                        filtered.add(ItemGenerator.getOrCreateItemType(t,
                         material, q));
                    }
                }

            }
        }

        // getShopLevel().getMaxCostFactor();

        return filtered;
    }

    @Override
    public void newTurn() {
        // TODO sell/buy randomly from repertoire
        modifyParameter(PARAMS.GOLD, getIntParam(MACRO_PARAMS.SHOP_INCOME));
        LogMaster.log(LOG_CHANNEL.MACRO_DYNAMICS, getName() + " now has "
         + getIntParam(PARAMS.GOLD) + " " + PARAMS.GOLD.getName());

        modifyParameter(MACRO_PARAMS.SHOP_INCOME,
         getIntParam(MACRO_PARAMS.SHOP_INCOME_GROWTH));
        getType().modifyParameter(MACRO_PARAMS.SHOP_INCOME,
         getIntParam(MACRO_PARAMS.SHOP_INCOME_GROWTH));
        // perhaps shops should getOrCreate "Level Ups" eventually too :)
    }

    public Integer buyItemFrom(DC_HeroItemObj t) {
        items.remove(t);
        // some items should be infinite though... perhaps based on shop level?
        Integer price = t.getIntParam(PARAMS.GOLD_COST);
        modifyParameter(PARAMS.GOLD, price);
        return price;
    }

    public Integer sellItemTo(DC_HeroItemObj t) {
        Integer cost = t.getIntParam(PARAMS.GOLD_COST);
        cost = MathMaster.addFactor(cost, getIntParam(PARAMS.GOLD_MOD));
        buyItem(t, cost);
        return cost;
    }

    private boolean buyItem(DC_HeroItemObj t) {
        Integer cost = t.getIntParam(PARAMS.GOLD_COST);

        cost = MathMaster.addFactor(cost, getIntParam(PARAMS.GOLD_MOD));

        if (cost > getIntParam(PARAMS.GOLD) * goldToSpendPercentage / 100) {
            return false;
        }
        buyItem(t, cost);

        return true;
    }

    private void buyItem(DC_HeroItemObj type, int cost) {
        modifyParameter(PARAMS.GOLD, -cost);
        items.add(type);
        refreshGui();
    }

    private void refreshGui() {
    }


    @Override
    public List<String> getTabs() {
        return ContainerUtils.openContainer(getProperty(MACRO_PROPS.SHOP_ITEM_GROUPS));
    }

    @Override
    public List<String> getItemSubgroups(String tabName) {
        return null;
    }

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
        return DataManager.toTypeList(ContainerUtils.openContainer(getProperty(MACRO_PROPS.SHOP_ITEMS)),
         TYPE);
    }

    private boolean isFullRepertoire() {
        return false;
    }

    @Override
    public String getGold() {
        return getParam(PARAMS.GOLD);
    }


    public SHOP_TYPE getShopType() {
        if (shopType == null) {
            shopType = new EnumMaster<SHOP_TYPE>().retrieveEnumConst(
             SHOP_TYPE.class, getProperty(MACRO_PROPS.SHOP_TYPE));
            if (shopType == null) {
                shopType = SHOP_TYPE.MISC;
            }
        }
        return shopType;
    }

    public SHOP_LEVEL getShopLevel() {
        if (shopLevel == null) {
            shopLevel = new EnumMaster<SHOP_LEVEL>().retrieveEnumConst(
             SHOP_LEVEL.class, getProperty(MACRO_PROPS.SHOP_LEVEL));
            if (shopLevel == null) {
                shopLevel = SHOP_LEVEL.COMMON;
            }
        }
        return shopLevel;
    }

    public SHOP_MODIFIER getShopModifier() {
        if (shopModifier == null) {
            shopModifier = new EnumMaster<SHOP_MODIFIER>()
             .retrieveEnumConst(SHOP_MODIFIER.class,
              getProperty(MACRO_PROPS.SHOP_MODIFIER));
        }
        return shopModifier;
    }

    public Collection<DC_HeroItemObj> getItems() {
        if (items == null) {
            initItems();
        }
        return items;
    }

    @Override
    public void toBase() {
        super.toBase();
        setProperty(MACRO_PROPS.SHOP_ITEMS,
         ContainerUtils.constructContainer(DataManager
          .toStringList(getItems())));

    }

}
