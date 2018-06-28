package eidolons.game.battlecraft.logic.meta.scenario.hq;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.ShopManager;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;

/**
 * Created by JustMe on 5/31/2017.
 */
public class HqShopManager extends ShopManager<ScenarioMeta> {
    public HqShopManager(MetaGameMaster master) {
        super(master);
    }


    @Override
    public void init() {
        shops = new ArrayList<>();
        for (String substring : StringMaster.open(getMetaGame().getMission().getMissionLocation().getProperty(PROPS.PLACE_SHOPS))) {
            HqShop shop = new HqShop(DataManager.getType(substring, DC_TYPE.SHOPS));
            shops.add(shop);
        }
    }
}
