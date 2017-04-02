package main.libgdx.gui.panels.dc.inventory;

import main.entity.Entity;

public interface InventoryClickHandler {
    //IDEA: FOR NON-COMBAT, DROP == SELL!
    boolean cellClicked(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                        boolean altClick, Entity cellContents);

    boolean itemDragAndDropped(CELL_TYPE cell_type,
                               Entity cellContents, Entity droppedItem
    );

    void undoClicked();

    void doneClicked();

    void cancelClicked();

    boolean isUndoEnabled();

    boolean isDoneEnabled();

    boolean isCancelEnabled();

    public enum CELL_TYPE {
        WEAPON_MAIN("UI/components/2017/dialog/inv/empty slots/empty weapon main.png"),
        WEAPON_OFFHAND("UI/components/2017/dialog/inv/empty slots/empty weapon off.png"),
        ARMOR("UI/components/2017/dialog/inv/empty slots/empty armor.png"),
        AMULET("UI/components/2017/dialog/inv/empty slots/empty amulet.jpg"),
        RING("UI/components/2017/dialog/inv/empty slots/ring_empty_slot.png"),
        QUICK_SLOT("UI/components/2017/dialog/inv/empty slots/empty_pack.jpg"),
        INVENTORY("UI/components/2017/dialog/inv/empty slots/empty_pack.jpg");


        private String slotImagePath;

        CELL_TYPE(String slotImagePath) {
            this.slotImagePath = slotImagePath;
        }

        public String getSlotImagePath() {
            return slotImagePath;
        }
    }
}
