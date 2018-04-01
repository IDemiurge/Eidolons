package eidolons.game.battlecraft.logic.meta.scenario.hq;

import main.content.CONTENT_CONSTS2.SHOP_LEVEL;
import main.content.CONTENT_CONSTS2.SHOP_MODIFIER;
import main.content.CONTENT_CONSTS2.SHOP_TYPE;

import java.util.List;

/**
 * Created by JustMe on 5/22/2017.
 */
public interface ShopInterface {
    List<String> getTabs();

    List<String> getItemSubgroups(String tabName);

    List<String> getItems(String groupList);

    String getName();

    String getGold();

    String getImagePath();

    SHOP_TYPE getShopType();

    SHOP_LEVEL getShopLevel();

    SHOP_MODIFIER getShopModifier();
}
