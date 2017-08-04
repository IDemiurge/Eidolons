package main.game.battlecraft.ai.advanced.machine.profile;

import main.game.battlecraft.ai.advanced.machine.PriorityProfile;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.generic.AiMaster;
import main.system.auxiliary.data.FileManager;

import java.util.List;

/**
 * Created by JustMe on 8/3/2017.
 */
public class ProfileChooser extends AiHandler{

    public ProfileChooser(AiMaster master) {
        super(master);
    }

    public PriorityProfile chooseProfile(){
        // some function to evaluatate a profile's fitness for situation?
        PriorityProfile profile = null;
String folder = ProfileWriter.root;
        getUnit().getAI().getType();
        List<String> folders =FileManager.getFileNames(
         FileManager.getFilesFromDirectory(folder, true, false));
        getUnit().getType().getName();

        return profile;
    }
}
