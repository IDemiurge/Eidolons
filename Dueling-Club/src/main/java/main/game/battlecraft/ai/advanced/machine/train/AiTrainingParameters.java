package main.game.battlecraft.ai.advanced.machine.train;

import main.content.C_OBJ_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.battlecraft.rules.RuleMaster.RULE_SCOPE;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 7/31/2017.
 * Should define the whole preset that is being used for training
 * Used for initializing the game
 */
public class AiTrainingParameters {

    Integer roundsMax;
    String presetPath; //TODO OR SAVE FILE!
    RULE_SCOPE ruleScope;
    boolean deterministic;
    private ObjType traineeType;

    public AiTrainingParameters(String[] arg) {
        int i = 0;
        for (AI_TRAIN_PARAM sub : AI_TRAIN_PARAM.values()) {
            if (arg.length <= i) break;
            try {
                switch (sub) {
                    case PRESET_PATH:
                        presetPath = arg[i];
                        break;
                    case TRAINEE_TYPE:
                        traineeType = DataManager.
                         getType(arg[i], C_OBJ_TYPE.UNITS_CHARS);
                        break;
                    case ROUND_LIMIT:
                        roundsMax = StringMaster.getInteger(arg[i]);
                        break;
                    case RULE_SCOPE:
                        ruleScope = RULE_SCOPE.valueOf(arg[i]);
                        break;
                }
                i++;
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }


    }

    public Integer getRoundsMax() {
        return roundsMax;
    }

    public String getPresetPath() {
        if (presetPath == null) {
            presetPath = initPresetPath();
        }
        return presetPath;
    }

    private String initPresetPath() {
        return "ai.xml";
    }

    public RULE_SCOPE getRuleScope() {
        return ruleScope;
    }

    public boolean isDeterministic() {
        return deterministic;
    }

    public ObjType getTraineeType() {
        if (traineeType == null) {
            traineeType = initDefaultType();
        }
        return traineeType;
    }

    public void setTraineeType(ObjType traineeType) {
        this.traineeType = traineeType;
    }

    private ObjType initDefaultType() {
//    getPresetPath()
//        return DC_Game.game.getPlayer(true).getHeroObj().getType();
        return DataManager.
         getType("Pirate", C_OBJ_TYPE.UNITS_CHARS);
    }

    public enum AI_TRAIN_PARAM {
        PRESET_PATH,
        TRAINEE_TYPE,
        ROUND_LIMIT,
        RULE_SCOPE;
    }

    public enum STANDARD_TRAINING_PARAMETERS {
        DEMO_BATTLE(""),;
        String presetPath;

        STANDARD_TRAINING_PARAMETERS(String presetPath) {
            this.presetPath = presetPath;
        }
    }
}
