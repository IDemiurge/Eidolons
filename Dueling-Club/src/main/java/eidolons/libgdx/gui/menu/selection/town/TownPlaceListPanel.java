package eidolons.libgdx.gui.menu.selection.town;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;

import java.util.List;

/**
 * Created by JustMe on 10/25/2018.
 */
public class TownPlaceListPanel extends ItemListPanel {

    private boolean disabled;

    protected NINE_PATCH getNinePatch() {
        return null;
    }

    @Override
    public boolean isBlocked(SelectableItemData item) {
        if (disabled)
            return true;
        return super.isBlocked(item);
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    protected int getDefaultHeight() {
        int h = 0;
        if (items != null)
            h = items.size();
        return (int) (GdxMaster.getHeight() / 3 + h * GdxMaster.adjustHeight(100));
    }

    protected int getDefaultWidth() {
        return (int) GdxMaster.adjustWidth(300);
    }

    @Override
    public void setItems(List<SelectableItemData> items) {
        super.setItems(items);
        setHeight(getDefaultHeight());
        //TODO should not be necessary....
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
    }
}