package eidolons.game.battlecraft.ai.advanced.machine.profile;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.advanced.machine.PriorityProfile;
import eidolons.game.battlecraft.ai.advanced.machine.train.AiTrainingCriteria.CRITERIA_TYPE_NUMERIC;
import eidolons.game.battlecraft.ai.advanced.machine.train.AiTrainingResult;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Writer;
import main.system.PathUtils;

/**
 * Created by JustMe on 8/3/2017.
 */
public class ProfileWriter {

    public static String root = "duel-club/ai-data/training/profiles";
    public static String dataRoot = "duel-club/ai-data/training/init-data";
    public static String separator = ",";
    public static String DEFAULT_CRITERIA = "default criteria.txt";
    public static String DEFAULT_PARAMETERS = "default parameters.txt";

    public static String getRoot() {
        return PathFinder.getXML_PATH() + root;
    }

    public static void generateDefaultDataFiles() {
        StringBuilder builder = new StringBuilder();
//        for (AI_TRAIN_PARAM param: AI_TRAIN_PARAM.values()){
//            param.g
//        }
        for (CRITERIA_TYPE_NUMERIC numeric : CRITERIA_TYPE_NUMERIC.values()) {
            builder.append(numeric.getDefaultValue()).append(separator);
        }
//        builder.append(numeric.getDefaultValue() + AiTrainingRunner.getDataInstanceSeparator());
//        for (CRITERIA_TYPE_BOOLEAN typeBoolean: CRITERIA_TYPE_BOOLEAN.values()){
//            builder.append(typeBoolean.getDefaultValue() + AiTrainingRunner.getSegmentSeparator());
//        }
        String path = PathUtils.buildPath(PathFinder.getXML_PATH(), dataRoot, DEFAULT_CRITERIA);
        XML_Writer.write(builder.toString(), path);
    }

    public static void save(PriorityProfile profile) {
        AiTrainingResult result = profile.getResult();

        Unit unit = result.getUnitStats().getUnit();
        String type = unit.getAI().getType().toString();
        String preset = result.getParameters().getDungeonData();
        String name = result.getParameters().getTraineeType() + ".profile";

        StringBuilder builder = new StringBuilder();
        profile.getConstants().stream().forEach(f -> {
            builder.append(f).append(separator);
        });
        String path = PathUtils.buildPath(root, type, preset, name);
        XML_Writer.write(builder.toString(), path);
    }
}
