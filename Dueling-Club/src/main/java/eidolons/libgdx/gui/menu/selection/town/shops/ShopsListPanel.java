package eidolons.libgdx.gui.menu.selection.town.shops;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;

import java.util.List;

/**
 * Created by JustMe on 10/13/2018.
 */
public class ShopsListPanel extends ItemListPanel {

    protected NINE_PATCH getNinePatch() {
        return null ;
    }

    protected int getDefaultHeight() {
        int h=0;
        if (items!=null )
            h = items.size();
        return (int) (GdxMaster.getHeight()  /3 + h*GdxMaster.adjustHeight(100));
    }

    protected int getDefaultWidth() {
        return  (int) GdxMaster.adjustWidth(300);
    }

    @Override
    public void setItems(List<SelectableItemData> items) {
        super.setItems(items);
        setHeight(getDefaultHeight());
    }
}
