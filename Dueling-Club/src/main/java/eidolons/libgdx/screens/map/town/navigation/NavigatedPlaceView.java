package eidolons.libgdx.screens.map.town.navigation;

import eidolons.libgdx.gui.menu.selection.DescriptionPanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.screens.map.town.navigation.data.Navigable;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 11/22/2018.
 */
public class NavigatedPlaceView extends TablePanelX {
    private final TablePanelX lower;
    PlaceNavigationPanel navigationPanel;
    DescriptionPanel descriptionPanel;
    //dialogue space?

    public NavigatedPlaceView() {
        add(navigationPanel = new PlaceNavigationPanel(this)).row();
        add(lower = new TablePanelX<>());

        lower.add(descriptionPanel = new DescriptionPanel());
//        DialogueView dialogView;
//        GuiEventManager.bind(GuiEventType.DIALOG_SHOW , p->
//        showDialogue());
        GuiEventManager.bind(GuiEventType.SHOW_NAVIGATION_PANEL, p ->
         {
//             navigationPanel.show((MacroObj) p.get());
         });

    }

    private void showDialogue() {
    }

    public void selected(Navigable navigable) {
        descriptionPanel.setText(navigable.getDescription());
    }
}
