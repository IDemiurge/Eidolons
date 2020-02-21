package main.level_editor.gui.menus;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import main.level_editor.LevelEditor;
import main.level_editor.gui.components.UiButton;
import main.level_editor.struct.campaign.Campaign;
import main.level_editor.struct.level.Floor;

public class FloorTabs extends VisButtonGroup {

    public void init(Campaign campaign){
//        campaign.getFloors().for
    }

    public void addFloor(String title, Floor floor){
        ButtonStyled.STD_BUTTON style= ButtonStyled.STD_BUTTON.TAB_HIGHLIGHT_COLUMN;
        new SmartButton(title, style,  ()-> LevelEditor.floorSelected(floor));
        new UiButton(title,   ()-> LevelEditor.floorSelected(floor));
    }
}
