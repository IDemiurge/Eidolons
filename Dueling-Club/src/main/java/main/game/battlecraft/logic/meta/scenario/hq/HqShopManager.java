package main.game.battlecraft.logic.meta.scenario.hq;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import main.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.game.battlecraft.logic.meta.universal.ShopManager;
import main.system.auxiliary.StringMaster;

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
for(String substring: StringMaster.open( getMetaGame().getMission().getPlace(). getProperty(PROPS.PLACE_SHOPS))){
    HqShop shop = new HqShop(DataManager.getType(substring, DC_TYPE.SHOPS));
    shops.add(shop);
}
    }
}
