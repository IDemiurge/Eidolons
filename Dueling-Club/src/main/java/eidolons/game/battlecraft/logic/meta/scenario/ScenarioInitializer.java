package eidolons.game.battlecraft.logic.meta.scenario;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.MetaInitializer;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;

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
        if (type == null) {
            type = DataManager.getType("Hell", DC_TYPE.FLOORS);
            //DataManager.getRandomType( DC_TYPE.SCENARIOS);
        }

        if (isReverseLevels()) {
            type = new ObjType(type);
            type.reverseContainerProperty(PROPS.SCENARIO_MISSIONS);
        } else if (isShuffleLevels()) {
            type = new ObjType(type);
            type.shuffleContainerProperty(PROPS.SCENARIO_MISSIONS);
        }
        //     TODO better solution needed
        //   getMaster().setRngDungeon(type.getGroup().equalsIgnoreCase("Random"));
        return createMeta(new Scenario(type));
    }

    protected ScenarioMeta createMeta(Scenario scenario) {
        return new ScenarioMeta(scenario, master);
    }

    private boolean isReverseLevels() {
        return OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.REVERSE_LEVELS);
    }

    private boolean isShuffleLevels() {
        return OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.SHUFFLE_LEVELS);
    }

}
