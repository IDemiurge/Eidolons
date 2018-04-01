package eidolons.libgdx.gui.menu.selection.manual;

import eidolons.libgdx.gui.menu.selection.ItemInfoPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 12/5/2017.
 */
public class ManualDetails extends ItemInfoPanel {
    public ManualDetails(SelectableItemData item) {
        super(item);
    }

    protected String getEmptyImagePath() {
        return VISUALS.QUESTION.getImgPath();
    }

    protected String getEmptyImagePathFullSize() {
        return ImageManager.getEmptyUnitIconFullSizePath();
    }

}
