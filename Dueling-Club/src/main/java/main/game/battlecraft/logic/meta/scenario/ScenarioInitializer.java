package main.game.battlecraft.logic.meta.scenario;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.meta.MetaGameMaster;
import main.game.battlecraft.logic.meta.MetaInitializer;

/**
 * Created by JustMe on 5/12/2017.
 */
public class ScenarioInitializer extends MetaInitializer<ScenarioMeta> {
    public ScenarioInitializer(MetaGameMaster master) {
        super(master);
    }


    @Override
    public ScenarioMeta initMetaGame(String data) {
        ObjType type = DataManager.getType(data, DC_TYPE.SCENARIOS);
        return new ScenarioMeta(
         new Scenario(type), master );
    }

}
