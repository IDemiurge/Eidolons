package eidolons.libgdx.gui.menu.selection.manual;

import eidolons.libgdx.GDX;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH_PADDING;
import eidolons.libgdx.gui.menu.selection.ItemInfoPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import main.swing.generic.components.G_Panel.VISUALS;

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

    @Override
    protected float getDescriptionWidth() {
        return GDX.width(800);
    }

    @Override
    protected float getDescriptionHeight() {
        return GDX.height(620);
    }

    @Override
    protected String getTitle() {
        return "";
    }

    protected String getEmptyImagePathFullSize() {
        return "";
    }
    @Override
    protected void afterLayout() {
        super.afterLayout();
        if (startButton != null) {
            startButton.setPosition(GDX.centerWidth(startButton),
             NINE_PATCH_PADDING.SAURON.bottom);
        }
    }
}
