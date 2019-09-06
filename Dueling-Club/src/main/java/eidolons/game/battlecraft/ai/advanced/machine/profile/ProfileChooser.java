package eidolons.game.battlecraft.ai.advanced.machine.profile;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.advanced.machine.PriorityProfile;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import main.content.enums.system.AiEnums.*;
import main.system.auxiliary.data.FileManager;

import java.util.List;

/**
 * Created by JustMe on 8/3/2017.
 */
public class ProfileChooser extends AiHandler {

    public Class[] profileClasses = {
     CHARACTER_TYPE.class,
     INCLINATION_TYPE.class,
     IMPULSE_TYPE.class,
     META_GOAL_TYPE.class,
     ORDER_PRIORITY_MODS.class,
     BEHAVIOR_MODE.class,
    };

    public ProfileChooser(AiMaster master) {
        super(master);
    }
    //TODO IDEA: merge multiple profiles?

    public String getProfileType(UnitAI ai) {
        switch (getPriorityProfileManager().getGroup()) {
            case UNIT_SPECIFIC:
                return ai.getUnit().getName();
        }
//        if (ai.getCurrentBehavior()!=null ){
//            return ai.getCurrentBehavior().toString();
//        }
//        if (ai.getCurrentOrder()!=null ){
//            return ai.getCurrentOrder(). getType().toString();
//        }
//        if (ai.getMetaGoals()!=null ){
//            return ai.getMetaGoals().getVar(0). getType().toString();
//        }
//        if (ai.getInclinations()!=null ){
//            return ai.getInclinations().getVar(0).toString();
//        }
//        if (ai.getImpulse()!=null ){
//            return ai.getImpulse().toString();
//        }
//        if (ai.getCharacterType()!=null ){
//            return ai.getCharacterType().toString();
//        }

        return ai.getType().toString();
    }

    //profile grouping:
    public PriorityProfile chooseProfile(String role) {
        // some function to evaluatate a profile's fitness for situation?
        // it is assumed that the profile written in the folder is optimal
        PriorityProfile profile = null;
        String folder = ProfileWriter.root;

        getUnit().getAI().getType();
        List<String> folders = FileManager.getFileNames(
         FileManager.getFilesFromDirectory(folder, true, false));
        getUnit().getType().getName();

        //try to find unit-specific profile
        return profile;
    }

    public enum AI_PROFILE_GROUP {
        UNIT_SPECIFIC,
        AI_TYPE,
        RPG,
        ORDER,
        BEHAVIOR,

    }
}
