package eidolons.game.battlecraft.logic.meta.universal;

import eidolons.macro.entity.shop.Shop;

import java.util.List;

/**
 * Created by JustMe on 5/8/2017.
 * <p>
 * create shops for each mission!
 */
public class ShopManager<E extends MetaGame> extends MetaGameHandler<E> {
    protected List<Shop> shops;

    public ShopManager(MetaGameMaster master) {
        super(master);
    }


    public void init() {

    }

    public List<Shop> getShops() {
        return shops;
    }
}
