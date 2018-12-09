package eidolons.game.battlecraft.logic.meta.scenario.dialogue.ink.campaign;

import com.bladecoder.ink.runtime.Story;
import eidolons.macro.global.Campaign;
import main.data.filesys.PathFinder;
import main.system.auxiliary.data.FileManager;

/**
 * Created by JustMe on 12/7/2018.
 *
 * can we have more than one STORY object?
 *
 * if we can manage VARIABLES
 */
public class InkyCampaign {
    Campaign campaign;
    Story story;

    public void save(){
        try{
            String path= PathFinder.getCampaignSavePath() + getSaveName();
            FileManager.write(story.getState().toJson(),
             path);
        }catch(Exception e){main.system.ExceptionMaster.printStackTrace( e);}

    }

    private String getSaveName() {
        return  campaign.getName()+ getVersion();
    }

    private String getVersion() {
        return null;
    }

}
