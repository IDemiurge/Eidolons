package eidolons.macro.entity.shop;

import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.ItemFactory;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.core.EUtils;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.herocreator.logic.items.ItemGenerator;
import eidolons.game.module.herocreator.logic.items.ItemMaster;
import eidolons.libgdx.gui.menu.selection.town.shops.ShopPanel;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CONTAINER;
import eidolons.system.audio.DC_SoundMaster;
import main.content.CONTENT_CONSTS2.SHOP_TYPE;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.MACRO_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.handlers.EntityHandler;
import main.entity.handlers.EntityMaster;
import main.entity.type.ObjType;
import main.system.SortMaster;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.launch.Flags;
import main.system.math.MathMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.util.*;

/**
 * Created by JustMe on 11/10/2018.
 */
public class ShopItemManager extends EntityHandler<Shop> {
    protected static final boolean TEST_MODE = true;
    protected int playerDebt=0;
    protected List<DC_HeroItemObj> items;
    protected int goldToSpendPercentage = 100;
    protected int spareGoldPercentage = 20;
    protected Map<Integer, Integer> priceCache; //current session, fixed prices for sell/buy
    protected Map<Integer, Integer> priceCacheMax; //item has been in this shop; can't sell at higher price than it was bought for
    protected boolean initDone;

    public ShopItemManager(Shop entity, EntityMaster<Shop> entityMaster) {
        super(entity, entityMaster);
    }

    public int getPlayerDebt() {
        return playerDebt;
    }

    public int getBalanceForBuy(DC_HeroItemObj item, Unit hero, boolean heroBuys) {
        Integer price = getPrice(item, hero, heroBuys);
        return heroBuys ? hero.getGold() - price
         : getGold() - price;
    }

    public boolean canBuy(DC_HeroItemObj item, Unit buyer, boolean canUseDebt) {
        Integer price = getPrice(item, buyer, true);
        if (canUseDebt) {
            price -= getMaxDebt() - playerDebt;
        }
        return buyer.getGold() >= price;
    }

    public boolean canSellTo(DC_HeroItemObj item, Unit seller, boolean canUseDebt) {
        Integer price = getPrice(item, seller, false);
        if (canUseDebt) {
            price += getMinBalance()+ playerDebt;
        }
        return getGold() >= price;
    }

    private Integer getMinBalance() {
        int max = getEntity().getMinBalance();
        max+= max*(getEntity().getReputation())/100;
        return  max ;
    }

    private Integer getMaxDebt() {
        int max = getEntity().getMaxDebt();
        max+= max* getEntity().getIntParam(PARAMS.DEBT_MOD)/100;
        max+= max*(getEntity().getReputation())/100;
        return max;
    }

    public void stockItems(int spareGoldPercentage) {

        PROPERTY prop = getShopType().getFilterProp();
        int i = 0;

        String[] item_groups = ShopMaster.initItemGroups(getShopType());
        OBJ_TYPE itemsType = ShopMaster.getItemsType(getShopType());
        List<ObjType> basis = ItemGenerator.getTypesForShop(itemsType);

        for (String group : item_groups) {
            i++;
            goldToSpendPercentage = (100 - spareGoldPercentage - i * 5)
             / item_groups.length;

            List<ObjType> pool = ShopMaster.createItemPool(getEntity(), itemsType, basis, prop, group);
            // some params from Shop ObjType?
            int max = ShopMaster.getMaxItemsPerGroup(getEntity());
            Loop.startLoop(pool.size());
            while (!Loop.loopEnded() && !pool.isEmpty() && max > 0) {
                int randomListIndex = RandomWizard.getRandomIndex(pool);
                ObjType t = pool.get(randomListIndex);
                if (t == null) {
                    continue;
                } //TODO check first, then create!!!
                // MacroGame.getGame()!
                if (acquireItem(t)) {
                    max--; // second loop based on cheapest items?
                }
                if (!Shop.isUnlimitedSize()) {
                    if (items.size() > ShopPanel.COLUMNS_DEFAULT * ShopPanel.ROWS_DEFAULT) {
                        return;
                    }
                }
            }
        }

    }

    protected boolean acquireItem(ObjType t) {
        Integer cost = t.getIntParam(PARAMS.GOLD_COST);
        if (!TEST_MODE || initDone)
            if (cost > getIntParam(PARAMS.GOLD) * goldToSpendPercentage / 100) {
                return false;
            }
        DC_HeroItemObj item = createItem(t);
        itemBought(item, cost, null );
        return true;
    }

    protected DC_HeroItemObj createItem(ObjType t) {
        DC_HeroItemObj item = ItemFactory.createItemObj(t, DC_Player.NEUTRAL,
         DC_Game.game, new Ref(), false);

        item.getRef().setID(KEYS.THIS, item.getId());
        item.getRef().setID(KEYS.SOURCE, getId());
        //        TODO item.getRef().setID(KEYS.SHOP,  getId());

        switch (item.getOBJ_TYPE_ENUM()) {
            case WEAPONS:
                item.getRef().setID(KEYS.WEAPON, item.getId());
                break;
            case ARMOR:
                item.getRef().setID(KEYS.ARMOR, item.getId());
                break;
            case JEWELRY:
            case ITEMS:
                item.getRef().setID(KEYS.ITEM, item.getId());
                break;
        }


        return item;
    }

    protected void checkStockCommonItems(float timeCoef) {
        OBJ_TYPE itemsType = ShopMaster.getItemsType(getShopType());
        if (itemsType.equals(DC_TYPE.ITEMS)) {
            ObjType itemType = DataManager.getType(ItemMaster.FOOD, DC_TYPE.ITEMS);
            int n = Math.round(RandomWizard.getRandomFloat() *getIntParam(MACRO_PARAMS.SHOP_INCOME)* timeCoef / itemType.getIntParam(PARAMS.GOLD_COST));
            for (int i = 0; i < n; i++) {
                DC_HeroItemObj item = createItem(itemType);
                itemBought(item, 0, null );
            }
            //         TODO    itemType = DataManager.getType(ItemMaster.TORCH, DC_TYPE.ITEMS);
            //            List<ObjType> pool = constructPool(itemType);
            //
            //              n = Math.round(RandomWizard.getRandomFloat()* timeCoef / itemType.getIntParam(PARAMS.GOLD_COST));
            //            for (int i = 0; i < n; i++) {
            //                itemType= pool.getVar(i);
            //                DC_HeroItemObj item = createItem(itemType);
            //                itemBought(item , 0);
            //            }
        }
    }

    // generateCustomItems(); randomly based on the repertoire spectrum

    public Integer buyItemFrom(DC_HeroItemObj seller, Unit buyer) {
        if (!items.remove(seller))
            return null;
        // some items should be infinite though... perhaps based on shop level?
        Integer price = getPrice(seller, buyer, true);
        takesGold(price, buyer);

        priceCache.put(seller.getId(), price);
        return price;
    }

    public Integer sellItemTo(DC_HeroItemObj t, Unit seller) {
        Integer price = getPrice(t, seller, false);
        priceCache.put(t.getId(), price);

        int balanceChange = itemBought(t, price, seller);
        price -= balanceChange;
        return price;
    }

    protected int itemBought(DC_HeroItemObj itemObj, int cost, Unit hero) {
        items.add(itemObj);
        itemObj.setContainer(CONTAINER.SHOP);
        return givesGold(cost, hero );
    }


    protected void paysDebt(int paid, Unit hero) {
        hero.addParam(PARAMS.GOLD, paid);
        addParam(PARAMS.GOLD, -paid);
        playerDebt-= -paid;
    }
    protected int givesGold(int cost, Unit hero) {
        Integer gold = getGold();
        int balanceChange = 0;
        if (gold < cost) {
            if (hero != null) {
                if (playerDebt < 0) {
                    balanceChange = gold -  cost;
                }
                playerDebt +=gold -  cost;
                //notify
            }
            setParam(PARAMS.GOLD, 0);
        } else {
            if (hero != null) {
                if (playerDebt>0){
                    balanceChange = Math.min(cost, playerDebt);
                }
                playerDebt -=balanceChange;
                cost -= balanceChange;
            }
            getEntity().modifyParameter(PARAMS.GOLD, -cost);
        }
        if (hero!=null )
        {
            int paid = Math.min(gold, cost);
            hero.addParam(PARAMS.GOLD, paid);
        }
        return balanceChange;
    }
    protected void takesGold(int cost, Unit buyer) {
        takesGold(cost, buyer, false);
    }
        protected void takesGold(int cost, Unit buyer, boolean debt) {
        int paid = Math.min(cost, buyer.getIntParam(PARAMS.GOLD));
        buyer.modifyParameter(PARAMS.GOLD, -paid);
        getEntity().modifyParameter(PARAMS.GOLD, paid);
        if (debt){
            playerDebt +=-cost  ;
        } else
            playerDebt +=cost - paid  ;
    }

    public Integer getPrice(DC_HeroItemObj t, Unit unit, boolean buy) {
        Integer price = getPriceCache().get(t.getId());
        if (price == null) {
            price = t.getIntParam(PARAMS.GOLD_COST);
            price = getPrice(price, unit, buy);
            // suppose these are -20 / 30 reduction for shop / hero
            // if shop sells, it will be at 90%
        }
        return price;
    }

    public Integer getPrice(Integer price, Unit unit, boolean buy) {
        int i = 1;
        if (buy)
            i = -1;
        price = MathMaster.addFactor(price, i * getCostMod(unit));
        return price;
    }

    private int getCostMod(Unit unit) {
        Integer reduction = getIntParam(PARAMS.GOLD_COST_REDUCTION) ;
        reduction+= reduction*(getIntParam(MACRO_PARAMS.REPUTATION)-100) /100  ;
        reduction+= reduction*(getEntity().getTown().getReputation()-100)/100;

        reduction-=reduction*( unit.getIntParam(PARAMS.GOLD_COST_REDUCTION)) /100  ;

        return reduction;
    }

    public Map<Integer, Integer> getPriceCache() {
        return priceCache;
    }


    public void sellItems(int percentage) {
        int toSell = items.size() * percentage / 100;
        for (int i = 0; i < toSell; i++) {
            int n = RandomWizard.getRandomIndex(items);
            //            sellItemTo()
            items.remove(n);
        }
    }

    public void init() {
        if (!getProperty(MACRO_PROPS.SHOP_ITEMS).isEmpty()) {
            //preset items
            DataManager.toTypeList(ContainerUtils
              .openContainer(getProperty(MACRO_PROPS.SHOP_ITEMS)),
             C_OBJ_TYPE.ITEMS);
        }
        if (getIntParam(PARAMS.GOLD) == 0) {
            setParam(PARAMS.GOLD, ShopMaster.getBaseGold(getEntity()));
        }
        if (getIntParam(PARAMS.GOLD_MOD) == 0) {
            setParam(PARAMS.GOLD_MOD, ShopMaster.getBaseGoldCostMod(getEntity()), true);
        }
        if (getIntParam(MACRO_PARAMS.SHOP_INCOME) == 0) {
            setParam(MACRO_PARAMS.SHOP_INCOME,
             ShopMaster.getBaseGoldIncome(getEntity()), true);
        }
        initPriceCache();
        initItems();
        initDone = true;
    }

    private void initItems() {
        items = new ArrayList<>();
        if (!TEST_MODE)
            getIncome(1000);
        stockItems(spareGoldPercentage);
        if (TEST_MODE)
            getIncome(10);

        //randomize initial gold!
        if (!Flags.isMacro()) {
            Integer base = Math.max(200, getType().getIntParam(PARAMS.GOLD));
            addParam(PARAMS.GOLD, RandomWizard.getRandomIntBetween(
             base / 3 * 2, base * 3 / 2
            ));
        }
        if (isItemsSortedOnInit()) {
            Collections.sort(items, SortMaster.getObjSorterByExpression(item ->
             item.getIntParam(PARAMS.GOLD_COST)));
        }
    }

    private boolean isItemsSortedOnInit() {
        return true;
    }

    public void exited(Unit hero) {
        for (Integer id : priceCache.keySet()) {
            //       TODO save     addProperty(MACRO_PROPS.SHOP_CACHED_PRICES,
            //             VariableManager.getStringWithVariable(id , price));
            Integer price = priceCache.get(id);
            priceCacheMax.put(id, price);
        }
        addInterest(hero);
        getEntity().setParam(MACRO_PARAMS.BALANCE , playerDebt);
    }

    private void addInterest(Unit hero) {
        if (playerDebt >0){
            Integer interest = getEntity().getIntParam(MACRO_PARAMS.DEBT_INTEREST);
            interest+= interest*(hero).getIntParam(PARAMS.INTEREST_MOD)/100;
            interest= interest*getCostMod(hero)/100;
            playerDebt = playerDebt *interest/100;
        }


    }

    private void initPriceCache() {
        priceCacheMax = new HashMap<>();
        priceCache = new HashMap<>();
        for (String substring : ContainerUtils.openContainer(
         getProperty(MACRO_PROPS.SHOP_CACHED_PRICES))) {
            int id = Integer.valueOf(VariableManager.removeVarPart(substring));
            int price = Integer.valueOf(VariableManager.getVar(substring));
            priceCacheMax.put(id, price);
        }
    }

    public Collection<DC_HeroItemObj> getItems() {
        if (items == null) {
            initItems();
        }
        return items;
    }

    public void getIncome(float timeCoef) {
        addParam(PARAMS.GOLD, (int) (timeCoef * getIntParam(MACRO_PARAMS.SHOP_INCOME)));
        checkStockCommonItems(timeCoef);
    }


    public int getGold() {
        return getEntity().getGold();
    }

    public SHOP_TYPE getShopType() {
        return getEntity().getShopType();
    }

    public void handleDebt(Unit hero) {
        if (playerDebt ==0)
            return;
        int transferred = 0;
        boolean ok = false;
        boolean gives = false;
        if (playerDebt > 0) {
            transferred = Math.min(Math.abs(playerDebt), hero.getGold());
            takesGold(transferred, hero, true);
        } else {
            transferred = Math.min(Math.abs(playerDebt), getGold());
            paysDebt(transferred, hero);
            gives = true;
        }
        if (transferred>0){
            DC_SoundMaster.playStandardSound(STD_SOUNDS.NEW__GOLD);
        } else {
            return;
        }
        ok = playerDebt ==0;
        String message = ShopTransactions.getDebtHandleMessage(getEntity(),
         ok, gives, transferred, playerDebt);

        if (!ok) {
            if (!gives) {
                addInterest(hero);
                getEntity().getTown().reputationImpact(-15);
            }
        }
        //reputation impact?!

        EUtils.infoPopup(message, true, true);
    }
}
