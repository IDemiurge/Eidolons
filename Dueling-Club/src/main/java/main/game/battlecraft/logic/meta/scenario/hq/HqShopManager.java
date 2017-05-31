package main.game.battlecraft.logic.meta.scenario.hq;

import main.content.PROPS;
import main.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import main.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.game.battlecraft.logic.meta.universal.ShopManager;

import java.util.LinkedList;

/**
 * Created by JustMe on 5/31/2017.
 */
public class HqShopManager extends ShopManager<ScenarioMeta> {
    public HqShopManager(MetaGameMaster master) {
        super(master);
    }



    @Override
    public void init() {
        shops = new LinkedList<>();
      getMetaGame().getMission().getPlace(). getProperty(PROPS.PLACE_SHOPS);
//        getMetaGame().getScenario().get


    }
}
