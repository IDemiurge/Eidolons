package main.game.battlecraft.logic.meta.scenario.hq;

import main.content.CONTENT_CONSTS2.SHOP_LEVEL;
import main.content.CONTENT_CONSTS2.SHOP_MODIFIER;
import main.content.CONTENT_CONSTS2.SHOP_TYPE;

import java.util.List;

/**
 * Created by JustMe on 5/22/2017.
 */
public interface ShopInterface {
    public List<String> getTabs();
    public List<String> getItemSubgroups(String tabName);
    public List<String> getItems(String groupList);
    public String getName();
    public String getGold();
    public String getImagePath();

    SHOP_TYPE getShopType();

    SHOP_LEVEL getShopLevel();

    SHOP_MODIFIER getShopModifier();
}