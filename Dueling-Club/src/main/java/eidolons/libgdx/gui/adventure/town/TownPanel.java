package eidolons.libgdx.gui.adventure.town;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import eidolons.libgdx.gui.panels.TabbedPanel;
import eidolons.libgdx.gui.panels.TablePanelX;

/**
 * Created by JustMe on 7/18/2018.
 *
 * Overlaid on the map, has:
 */
public class TownPanel extends TablePanelX{
    TabbedPanel tabs;

    Cell mainView;


    public enum TOWN_VIEWS {
        OVERVIEW,
        SHOP,
        QUEST,
        TAVERN,
        LIBRARY,
        ;
    }

}
