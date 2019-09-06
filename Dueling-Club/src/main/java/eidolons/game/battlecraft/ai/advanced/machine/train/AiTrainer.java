package eidolons.game.battlecraft.ai.advanced.machine.train;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.ai.advanced.machine.PriorityProfile;
import eidolons.game.battlecraft.ai.advanced.machine.train.AiTrainingParameters.TRAINING_ENVIRONMENT;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.GameFactory.GAME_SUBCLASS;
import eidolons.game.core.launch.GameLauncher;
import eidolons.game.core.state.Loader;
import eidolons.libgdx.launch.ScenarioLauncher;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 7/31/2017.
 */
public class AiTrainer {

    private static final int MAX_ATTEMPTS = 25;
    private AiTrainingParameters parameters;
    private AiTrainingCriteria criteria;
    private PriorityProfile profile;
    private ObjType trainee;

    public static String getDefaultDungeonData() {
        ObjType type = DataManager.getType(ScenarioLauncher.DEFAULT, DC_TYPE.SCENARIOS);
        ObjType mission = DataManager.getType(ContainerUtils.openContainer(type.getProperty(
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
        trainee = parameters.getTraineeType();

        initTrainingParameters(parameters);
        int i = 0;
        while (i < MAX_ATTEMPTS) {
            try {
                return runAiScenario(parameters.getDungeonData());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                i++;
            }
        }

        return null;
    }

    private void initTrainingParameters(AiTrainingParameters parameters) {
//        loadGame(parameters.getVar)
        parameters.getRoundsMax();
        parameters.getTraineeType();
    }

    private AiTrainingResult runAiScenario(String presetPath) {
        if (!AiTrainingRunner.evolutionTestMode) {

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
        DC_Game.game.getAiManager().getPriorityProfileManager().setPriorityProfile
         (trainee, profile);
        boolean result = (boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.GAME_FINISHED);
        if (!result) {
            throw new RuntimeException();
        }
        return evaluateResult(profile, parameters, criteria);
    }

    private AiTrainingResult evaluateResult(PriorityProfile profile, AiTrainingParameters parameters, AiTrainingCriteria criteria) {
        AiTrainingResult result = new AiTrainingResult(profile, parameters, criteria);
        try {
            result.construct();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        main.system.auxiliary.log.LogMaster.log(1,
         "unit Stats= " + result.getUnitStats().getStatMap()
          + "ally Stats= " + result.getAllyStats().getStatsMap()
          + "enemy Stats= " + result.getEnemyStats().getStatsMap());
        return result;
    }


}
