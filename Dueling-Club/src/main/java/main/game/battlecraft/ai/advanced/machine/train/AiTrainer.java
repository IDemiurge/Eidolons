package main.game.battlecraft.ai.advanced.machine.train;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.battlecraft.ai.advanced.machine.PriorityProfile;
import main.game.battlecraft.ai.advanced.machine.train.AiTrainingParameters.TRAINING_ENVIRONMENT;
import main.game.core.game.DC_Game;
import main.game.core.game.GameFactory.GAME_SUBCLASS;
import main.game.core.launch.GameLauncher;
import main.game.core.state.Loader;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.test.frontend.ScenarioLauncher;

/**
 * Created by JustMe on 7/31/2017.
 */
public class AiTrainer {

    private AiTrainingParameters parameters;
    private AiTrainingCriteria criteria;
    private PriorityProfile profile;

    public static String getDefaultDungeonData() {
        ObjType type = DataManager.getType(ScenarioLauncher.DEFAULT, DC_TYPE.SCENARIOS);
        ObjType mission = DataManager.getType(StringMaster.openContainer(type.getProperty(
         PROPS.SCENARIO_MISSIONS)).get(0),
         DC_TYPE.MISSIONS);
        return mission.getProperty(PROPS.MISSION_FILE_PATH);
    }

    public static String getDefaultPartyData() {
        ObjType type = DataManager.getType(ScenarioLauncher.DEFAULT, DC_TYPE.SCENARIOS);
        return
         DataManager.getType(
          type.getProperty(PROPS.SCENARIO_PARTY), DC_TYPE.PARTY).getProperty(PROPS.MEMBERS)
//         .replace(";", ",")
         ;
    }

    public AiTrainingResult train(
     //type at coordinates? or for all of same type?
     PriorityProfile profile,
     AiTrainingCriteria criteria,
     AiTrainingParameters parameters) {
        this.parameters = parameters;
        this.criteria = criteria;
        this.profile = profile;
        ObjType trainee = parameters.getTraineeType();

        initTrainingParameters(parameters);
        runAiScenario(parameters.getDungeonData());
        DC_Game.game.getAiManager().getPriorityProfileManager().setPriorityProfile(trainee, profile);
        boolean result = (boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.GAME_FINISHED);
        return evaluateResult(profile, parameters, criteria);
    }

    private void initTrainingParameters(AiTrainingParameters parameters) {
//        loadGame(parameters.get)
        parameters.getRoundsMax();
        parameters.getTraineeType();
    }

    private void runAiScenario(String presetPath) {
        if (AiTrainingRunner.evolutionTestMode)
            return;
        RandomWizard.setAveraged(parameters.deterministic);
        if (parameters.getEnvironmentType() == TRAINING_ENVIRONMENT.PRESET) {
            new GameLauncher(GAME_SUBCLASS.TEST).launchPreset(presetPath);
        } else if (parameters.getEnvironmentType() == TRAINING_ENVIRONMENT.SAVE) {
            Loader.setPendingLoadPath(presetPath);
            DC_Game game = new GameLauncher(GAME_SUBCLASS.TEST).initDC_Game();
            game.start(true);
        } else if (parameters.getEnvironmentType() == TRAINING_ENVIRONMENT.DUNGEON_LEVEL) {

            GameLauncher a = new GameLauncher(GAME_SUBCLASS.SCENARIO);
            if (StringMaster.isEmpty(parameters.getPartyData())) {
                parameters.setPartyData(getDefaultPartyData());
            }
            a.PLAYER_PARTY = parameters.getPartyData();

            if (StringMaster.isEmpty(parameters.getDungeonData())) {
                parameters.setDungeonData(getDefaultDungeonData());
            }
            a.setDungeon(parameters.getDungeonData());
            DC_Game game = a.initGame();
            game.start(true);
        }

    }

    private AiTrainingResult evaluateResult(PriorityProfile profile, AiTrainingParameters parameters, AiTrainingCriteria criteria) {
        AiTrainingResult result = new AiTrainingResult(profile, parameters, criteria);
        try {
            result.construct();
        } catch (Exception e) {
            e.printStackTrace();
        }
        main.system.auxiliary.log.LogMaster.log(1,
         "unit Stats= " + result.getUnitStats().getStatMap()
         +"ally Stats= " + result.getAllyStats().getStatsMap()
         + "enemy Stats= " + result.getEnemyStats().getStatsMap());
        return result;
    }


}
