package eidolons.game.battlecraft.ai.advanced.machine.train;

import eidolons.game.battlecraft.ai.advanced.machine.train.AiTrainingCriteria.CRITERIA_TYPE_NUMERIC;
import eidolons.game.battlecraft.rules.RuleKeeper.RULE_SCOPE;
import main.content.C_OBJ_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 7/31/2017.
 * Should define the whole preset that is being used for training
 * Used for initializing the game
 */
public class AiTrainingParameters {

    String partyData;
    String dungeonData; //TODO OR SAVE FILE!
    RULE_SCOPE ruleScope;
    Integer roundsMax;
    boolean deterministic;
    TRAINING_ENVIRONMENT environmentType;
    STANDARD_TRAINING_PARAMETERS preset = STANDARD_TRAINING_PARAMETERS.GWYN;
    private ObjType traineeType;

    public AiTrainingParameters(String[] args) {
        if (preset != null) {
            args = preset.args;
        }
        int i = 0;
        for (AI_TRAIN_PARAM sub : AI_TRAIN_PARAM.values()) {
            if (args.length <= i) break;
            try {
                switch (sub) {
                    case ENVIRONMENT_TYPE:
                        environmentType = TRAINING_ENVIRONMENT.valueOf(args[i]);
                        break;
                    case DUNGEON_DATA:
                        dungeonData = args[i];
                        break;
                    case TRAINEE_TYPE:
                        traineeType = DataManager.
                         getType(args[i], C_OBJ_TYPE.UNITS_CHARS);
                        break;
                    case PARTY_DATA:
                        partyData = args[i];
                        break;
                    case ROUND_LIMIT:
                        roundsMax = StringMaster.getInteger(args[i]);
                        break;
                    case RULE_SCOPE:
                        ruleScope = RULE_SCOPE.valueOf(args[i]);
                        break;
                }
                i++;
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }


    }

    public String getPartyData() {
        return partyData;
    }

    public void setPartyData(String partyData) {
        this.partyData = partyData;
    }

    public TRAINING_ENVIRONMENT getEnvironmentType() {
        return environmentType;
    }

    public Integer getRoundsMax() {
        return roundsMax;
    }

    public String getDungeonData() {
        if (dungeonData == null) {
            dungeonData = initPresetPath();
        }
        return dungeonData;
    }

    public void setDungeonData(String dungeonData) {
        this.dungeonData = dungeonData;
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
//    getDungeonData()
//        return DC_Game.game.getPlayer(true).getHeroObj().getType();
        return DataManager.
         getType("Pirate", C_OBJ_TYPE.UNITS_CHARS);
    }

    public enum AI_TRAIN_PARAM {
        ENVIRONMENT_TYPE,
        DUNGEON_DATA,
        TRAINEE_TYPE,
        PARTY_DATA,
        CRITERIA,
        ROUND_LIMIT,
        RULE_SCOPE
    }

    public enum STANDARD_TRAINING_CRITERIA {
        ASSASSIN(
         new CriteriaMod(TRAINING_CRITERIA_MODS.COWARDICE, 225)
        ),;
        CriteriaMod[] mods;

        STANDARD_TRAINING_CRITERIA(CriteriaMod... mods) {
            this.mods = mods;
        }
    }

    public enum STANDARD_TRAINING_PARAMETERS {
        GWYN("DUNGEON_LEVEL", "", "GWYN"),;
        String[] args;

        STANDARD_TRAINING_PARAMETERS(String... args) {
            this.args = args;
        }

    }

    public enum TRAINING_CRITERIA_MODS {
        AGGRO,
        COWARDICE(CRITERIA_TYPE_NUMERIC.DAMAGE_TAKEN, CRITERIA_TYPE_NUMERIC.DIED, CRITERIA_TYPE_NUMERIC.FALLEN_UNCONSCIOUS),
        EFFICIENT,;
        CRITERIA_TYPE_NUMERIC[] consts;

        TRAINING_CRITERIA_MODS(CRITERIA_TYPE_NUMERIC... consts) {
            this.consts = consts;
        }
    }

    public enum TRAINING_ENVIRONMENT {
        PRESET,
        SAVE,
        DUNGEON_LEVEL,
    }


}
