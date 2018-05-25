package eidolons.libgdx.gui.panels.dc.inventory;

import eidolons.libgdx.texture.Images;
import main.entity.Entity;

public interface InventoryClickHandler {
    //IDEA: FOR NON-COMBAT, DROP == SELL!
    boolean cellClicked(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                        boolean altClick, Entity cellContents);

    boolean itemDragAndDropped(CELL_TYPE cell_type,
                               Entity cellContents, Entity droppedItem
    );
    void singleClick(CELL_TYPE cell_type, Entity cellContents);

    void undoClicked();

    void doneClicked();

    void apply();

    void cancelClicked();

    boolean isUndoEnabled();

    boolean isDoneEnabled();

    boolean isCancelEnabled();

    Entity getDragged();

    void setDragged(Entity dragged);

    enum CELL_TYPE {
        WEAPON_MAIN("UI/components/dc/dialog/inv/empty slots/empty weapon main.png"),
        WEAPON_OFFHAND("UI/components/dc/dialog/inv/empty slots/empty weapon off.png"),
        ARMOR("UI/components/dc/dialog/inv/empty slots/empty armor.png"),
        AMULET("UI/components/dc/dialog/inv/empty slots/empty amulet.jpg"),
        RING("UI/components/dc/dialog/inv/empty slots/ring_empty_slot.png"),
        QUICK_SLOT(Images.EMPTY_QUICK_ITEM),
        INVENTORY(Images.EMPTY_ITEM);


        private String slotImagePath;

        CELL_TYPE(String slotImagePath) {
            this.slotImagePath = slotImagePath;
        }

        public String getSlotImagePath() {
            return slotImagePath;
        }
    }
}
