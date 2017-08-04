package main.game.battlecraft.ai.advanced.machine.profile;

import main.data.xml.XML_Writer;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.advanced.machine.PriorityProfile;
import main.game.battlecraft.ai.advanced.machine.train.AiTrainingResult;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 8/3/2017.
 */
public class ProfileWriter {

    public static String root=",";
    public static String separator=",";

    public static void save(PriorityProfile profile) {
        AiTrainingResult result = profile.getResult();

        Unit unit = result.getUnitStats().getUnit();
        String type = unit.getAI().getType().toString();
        String preset = result.getParameters().getPresetPath();
        String name = result.getParameters().getTraineeType() + ".profile";

        StringBuilder content=new StringBuilder();
        profile.getConstants().stream().forEach(f->{
            content.append(f+separator);
        });
        String path = StringMaster.buildPath(root, type, preset, name);
        XML_Writer.write(content.toString(), path);
    }
}
