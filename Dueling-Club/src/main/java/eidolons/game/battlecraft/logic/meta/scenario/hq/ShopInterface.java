package eidolons.game.battlecraft.logic.meta.scenario.hq;

import main.content.CONTENT_CONSTS2.SHOP_LEVEL;
import main.content.CONTENT_CONSTS2.SHOP_MODIFIER;
import main.content.CONTENT_CONSTS2.SHOP_TYPE;

/**
 * Created by JustMe on 5/22/2017.
 */
public interface ShopInterface {

    String getName();

    int getGold();

    String getImagePath();

    SHOP_TYPE getShopType();

    SHOP_LEVEL getShopLevel();

    SHOP_MODIFIER getShopModifier();
}
