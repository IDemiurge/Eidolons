package libgdx.gui.dungeon.menu.selection.manual;

import libgdx.GDX;
import libgdx.gui.dungeon.menu.selection.ItemInfoPanel;
import libgdx.gui.dungeon.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.TiledNinePatchGenerator;
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
             TiledNinePatchGenerator.NINE_PATCH_PADDING.SAURON.bottom);
        }
    }
}
