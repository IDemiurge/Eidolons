package main.level_editor.gui.menus;

import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import main.level_editor.backend.functions.structure.FloorManager;
import main.level_editor.gui.components.UiButton;
import main.level_editor.struct.campaign.Campaign;
import main.level_editor.struct.level.Floor;

public class FloorTabs extends VisButtonGroup {

    public void init(Campaign campaign){
//        campaign.getFloors().for
    }

    public void addFloor(String title, Floor floor){
        ButtonStyled.STD_BUTTON style= ButtonStyled.STD_BUTTON.TAB_HIGHLIGHT_COLUMN;
        new SmartButton(title, style,  ()-> FloorManager.floorSelected(floor));
        new UiButton(title,   ()-> FloorManager.floorSelected(floor));
    }
}
