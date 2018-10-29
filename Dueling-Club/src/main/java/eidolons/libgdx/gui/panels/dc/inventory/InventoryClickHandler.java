package eidolons.libgdx.gui.panels.dc.inventory;

import eidolons.libgdx.texture.Images;
import main.entity.Entity;

public interface InventoryClickHandler {
    //IDEA: FOR NON-COMBAT, DROP == SELL!
    boolean cellClicked(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                        boolean altClick, Entity cellContents, boolean ctrlClick);

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

    enum CONTAINER {
        INVENTORY,
        STASH,
        SHOP,
        CONTAINER, QUICK_SLOTS,
        EQUIPPED,
        UNASSIGNED,
    }
    enum CELL_TYPE {
        WEAPON_MAIN(Images.EMPTY_WEAPON_MAIN),
        WEAPON_OFFHAND(Images.EMPTY_WEAPON_OFFHAND),
        ARMOR(Images.EMPTY_ARMOR),
        AMULET(Images.EMPTY_AMULET),
        RING(Images.EMPTY_RING),
        QUICK_SLOT(Images.EMPTY_QUICK_ITEM),
        INVENTORY(Images.EMPTY_ITEM),
        CONTAINER(Images.EMPTY_LIST_ITEM),
        STASH(Images.EMPTY_LIST_ITEM);


        private String slotImagePath;

        CELL_TYPE(String slotImagePath) {
            this.slotImagePath = slotImagePath;
        }

        public String getSlotImagePath() {
            return slotImagePath;
        }
    }
}
