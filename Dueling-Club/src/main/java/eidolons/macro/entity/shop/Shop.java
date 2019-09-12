package eidolons.macro.entity.shop;

import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.scenario.hq.ShopInterface;
import eidolons.game.module.adventure.utils.SaveMasterOld;
import eidolons.macro.MacroGame;
import eidolons.macro.entity.town.Town;
import eidolons.macro.entity.town.TownPlace;
import main.content.CONTENT_CONSTS2.SHOP_LEVEL;
import main.content.CONTENT_CONSTS2.SHOP_MODIFIER;
import main.content.CONTENT_CONSTS2.SHOP_TYPE;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.MACRO_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.xml.XML_Formatter;
import main.data.xml.XML_Writer;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;

import java.util.Collection;

public class Shop extends TownPlace implements ShopInterface {
    ShopItemManager itemManager;
    private SHOP_TYPE shopType;
    private SHOP_LEVEL shopLevel;
    private SHOP_MODIFIER shopModifier;

    public Shop(MacroGame game, ObjType type, Ref ref, Town town) {
        super(game, type, ref);
        setTown(town);
        setParam(PARAMS.GOLD_COST_REDUCTION, RandomWizard.getRandomIntBetween(-25, 25));

    }
    /*
    confirm selling for balance
    or buying in credit?
     */

    public void sellItems(int i) {
        itemManager.sellItems(i);
    }

    public void stockItems(int i) {
        itemManager.stockItems(i);
    }

    @Override
    public void init() {
        super.init();
        itemManager = new ShopItemManager(this, getMaster());
        itemManager.init();
    }

    @Override
    public String getSaveData() {
        String s = "";
        for (PROPERTY p : getPropMap().keySet()) {
            if (SaveMasterOld.isPropSaved(p, getOBJ_TYPE_ENUM())) {
                s += XML_Writer.openXML(p.getName())
                 + XML_Formatter.formatStringForXmlNodeName(getProperty(p))
                 + XML_Writer.closeXML(p.getName());
            }
        }
        return s;
    }

    public void getIncome(float timeCoef) {
        itemManager.getIncome(timeCoef);
    }
    //TODO sell stock on time

    @Override
    public int getGold() {
        return getIntParam(PARAMS.GOLD);
    }

    public int getMinBalance() {
        return getIntParam(MACRO_PARAMS.MIN_BALANCE);
    }

    public int getMaxDebt() {
        return getIntParam(MACRO_PARAMS.MAX_DEBT);
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
        return itemManager.getItems();
    }

    @Override
    public void toBase() {
        super.toBase();
        if (initialized)
            setProperty(MACRO_PROPS.SHOP_ITEMS,
             ContainerUtils.constructContainer(DataManager
              .toStringList(getItems())));

    }

    public Integer buyItemFrom(DC_HeroItemObj t, Unit buyer) {
        return itemManager.buyItemFrom(t, buyer);
    }

    public Integer sellItemTo(DC_HeroItemObj t, Unit seller) {
        return itemManager.sellItemTo(t, seller);
    }

    public boolean canSellTo(DC_HeroItemObj item, Unit seller, boolean canUseDebt) {
        return itemManager.canSellTo(item, seller, canUseDebt);
    }

    public boolean canBuy(DC_HeroItemObj item, Unit seller, boolean canUseDebt) {
        return itemManager.canBuy(item, seller, canUseDebt);
    }

    public int getBalanceForBuy(DC_HeroItemObj item, Unit hero, boolean heroBuys) {
        return itemManager.getBalanceForBuy(item, hero, heroBuys);
    }

    public Integer getPrice(DC_HeroItemObj t, Unit unit, boolean buy) {
        return itemManager.getPrice(t, unit, buy);
    }

    public Integer getPrice(int price, Unit unit, boolean buy) {
        return itemManager.getPrice(price, unit, buy);
    }

    public void exited(Unit hero) {
        itemManager.exited(hero);
    }

    public int getDebt() {
        return itemManager.getPlayerDebt();
    }

    public void handleDebt(Unit hero) {
        itemManager.handleDebt(hero);
    }

    public int getReputation() {
        Integer rep = getTown().getReputation();
        rep += rep * getIntParam(MACRO_PARAMS.REPUTATION) / 100;
        if (rep != 0)
            return rep;
        return getIntParam(MACRO_PARAMS.REPUTATION);
    }

    public void enter(Unit unit) {
        itemManager.handleDebt(unit);
    }

    public static boolean isUnlimitedSize() {
        return false;
    }
}