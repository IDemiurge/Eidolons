package main.game.battlecraft.logic.meta.universal;

import main.game.battlecraft.logic.meta.scenario.hq.ShopInterface;

import java.util.List;

/**
 * Created by JustMe on 5/8/2017.
 *
 * create shops for each mission!
 *
 */
public class ShopManager<E extends MetaGame> extends MetaGameHandler<E> {
    protected List<ShopInterface> shops;

    public ShopManager(MetaGameMaster master) {
        super(master);
    }


    public void init(){

    }
    public List<ShopInterface> getShops(){
        return shops;
    }
}