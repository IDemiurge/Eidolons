package main.level_editor.functions.io;

import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import main.level_editor.LevelEditor;
import main.level_editor.functions.LE_Handler;
import main.level_editor.functions.LE_Manager;
import main.level_editor.struct.campaign.Campaign;
import main.level_editor.struct.level.Floor;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_DataHandler extends LE_Handler {
    private Campaign campaign;

    public LE_DataHandler(LE_Manager manager) {
        super(manager);
    }

    public void openCampaign(String path) {

    }

    public void open(String floorName) {
        Campaign campaign=getDataHandler().getCampaign();
//        campaign.getFloorPath(floorName);
//        Floor floor = getDataHandler().loadFloor(floorName);
//        LevelEditor.floorSelected(floor);

//        ScreenData data = new ScreenData(SCREEN_TYPE.EDITOR, floorName);
//        data.setParam(new EventCallbackParam(floor));
//        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);

    }

    public void activateFloorTab(Floor floor) {

    }

    public Campaign getCampaign() {
        return campaign;
    }

}
