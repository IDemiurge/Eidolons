package libgdx.map.town.navigation;

import libgdx.gui.dungeon.menu.selection.DescriptionPanel;
import libgdx.gui.dungeon.panels.TablePanelX;
import libgdx.map.town.navigation.data.Navigable;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 11/22/2018.
 */
public class NavigatedPlaceView extends TablePanelX {
    PlaceNavigationPanel navigationPanel;
    DescriptionPanel descriptionPanel;
    //dialogue space?

    public NavigatedPlaceView() {
        add(navigationPanel = new PlaceNavigationPanel(this)).row();
        TablePanelX lower;
        add(lower = new TablePanelX<>());

        lower.add(descriptionPanel = new DescriptionPanel());
//        DialogueView dialogView;
//        GuiEventManager.bind(GuiEventType.DIALOG_SHOW , p->
//        showDialogue());
        GuiEventManager.bind(GuiEventType.SHOW_NAVIGATION_PANEL, p ->
         {
//             navigationPanel.show((MacroObj) p.getVar());
         });

    }

    private void showDialogue() {
    }

    public void selected(Navigable navigable) {
        descriptionPanel.setText(navigable.getDescription());
    }
}
