package eidolons.libgdx.screens.map.town.navigation;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.gui.menu.selection.DescriptionPanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.screens.map.town.navigation.data.Navigable;
import eidolons.macro.entity.MacroObj;

/**
 * Created by JustMe on 11/22/2018.
 */
public class NavigatedPlaceView extends TablePanelX {
    PlaceNavigationPanel navigationPanel;
    DescriptionPanel descriptionPanel;
    //dialogue space?

    public NavigatedPlaceView(MacroObj obj) {
        add(navigationPanel = new PlaceNavigationPanel(obj, this)).row();
        TablePanelX<Actor> lower = new TablePanelX<>();
//        toggleDialogueView();

        lower.add(descriptionPanel = new DescriptionPanel());
    }

    public void selected(Navigable navigable) {
        descriptionPanel.setText(navigable.getDescription());
    }
}
