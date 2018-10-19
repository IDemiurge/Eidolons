package eidolons.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.QuickSlotDataSource;

import java.util.List;

public class InventoryQuickSlotPanel extends InventorySlotsPanel {


    public InventoryQuickSlotPanel() {
        super(1, 1);
        setBackground(NinePatchFactory.getLightPanelDrawable());
    }

    protected CELL_TYPE getCellType() {
        return CELL_TYPE.QUICK_SLOT;
    }

    @Override
    protected List<InvItemActor> getSlotActors() {
        List<InvItemActor> list = ((QuickSlotDataSource) getUserObject())
         .getQuickSlots();
        setCols(Math.min(8, Math.max(list.size() / 8 * 8, (list.size() - 1) % 8 + 1)));
        setRows((list.size()-1)/ 8+1);
        return list;
    }

    @Override
    public Cell add(Actor actor) {
       return super.add(actor).fill(0, 1).expand(0, 1).center();
    }


    //    @Override
//    public void afterUpdateAct(float delta) {
//        if (getUserObject() == null)
//            return;
//        clear();
//        super.afterUpdateAct(delta);
//        final List<InvItemActor> quickSlots =
//         ((QuickSlotDataSource) getUserObject()).getQuickSlots();
//
//        int maxLength = Math.min(8, quickSlots.size());
//
//        for (int i = 0; i < maxLength; i++) {
//            Actor actor = quickSlots.get(i);
//            if (actor == null) {
//                actor = new ValueContainer(TextureCache.getOrCreateR(
//                 Images.EMPTY_QUICK_ITEM ));
//            }
//
//            addElement(actor).fill(0, 1).expand(0, 1).center();
//        }
//    }
}
