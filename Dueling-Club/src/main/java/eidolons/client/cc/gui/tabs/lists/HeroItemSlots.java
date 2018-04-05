package eidolons.client.cc.gui.tabs.lists;

import eidolons.client.cc.CharacterCreator;
import eidolons.client.cc.gui.MainPanel;
import eidolons.client.cc.gui.lists.ItemListManager;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.combat.damage.ArmorMaster;
import eidolons.system.audio.DC_SoundMaster;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.list.ListItem;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.graphics.GuiManager;
import main.system.graphics.MigMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

public class HeroItemSlots extends G_Panel implements MouseListener {

    private static final int WHITE_SPACES = 18;

    private Unit hero;

    private Map<ITEM_SLOT, ListItem<Entity>> itemMap = new HashMap<>();
    private ItemListManager itemListManager;
    private int obj_size = GuiManager.getSmallObjSize();

    int ARMOR_OFFSET = obj_size * 2 / 3;

    public HeroItemSlots(Unit hero, ItemListManager itemListManager) {
        super("flowy");
        this.hero = hero;
        this.itemListManager = itemListManager;
        init();
        addComps();
    }

    public ListItem<Entity> getItem(ITEM_SLOT slot) {
        return itemMap.get(slot);
    }

    private void addComps() {
        Boolean posSwitch = null;

        for (ITEM_SLOT slot : ItemEnums.ITEM_SLOT.values()) {

            ListItem<Entity> item = itemMap.get(slot);

            String pos;
            if (posSwitch == null) {
                pos = "@pos 32 0";
                posSwitch = true;
            } else if (posSwitch) {
                pos = "@pos " + MigMaster.getCenteredPosition(getSlotPanelWidth(), 64) + " 0";
                posSwitch = false;
            } else {
                pos = "@pos " + (getSlotPanelWidth() - 96) + " 0";
            }

            if (item == null) {
                if (slot != ItemEnums.ITEM_SLOT.ARMOR)

                {
                    boolean offhand = slot == ItemEnums.ITEM_SLOT.OFF_HAND;
                    if (hero.getNaturalWeapon(offhand) != null) {
                        item = initItem(slot, hero.getNaturalWeapon(offhand).getType());
                    }

                }
            }
            if (item != null) {
                add(item, pos);
            }
        }

    }

    private int getSlotPanelWidth() {
        return MainPanel.MAIN_PANEL_WIDTH - 64;
    }

    public ListItem<Entity> initItem(ITEM_SLOT slot, Entity type) {
        if (type == null) {
            itemMap.remove(slot);
            // Display empty item?
            return null;
        }
        ListItem<Entity> item = itemMap.get(slot);
        if (item == null) {
            item = new ListItem<>(type, false, false, obj_size);
            item.addMouseListener(this);
            itemMap.put(slot, item);
        } else {
            item.setObj(type);
            item.refresh();
        }
        return item;
    }

    public void init() {
        for (ITEM_SLOT slot : ItemEnums.ITEM_SLOT.values()) {
            Entity type = null;
            switch (slot) {
                case ARMOR:
                    type = getType(slot, DC_TYPE.ARMOR);
                    break;
                case MAIN_HAND:
                    type = getType(slot, DC_TYPE.WEAPONS);
                    break;
                case OFF_HAND:
                    type = getType(slot, DC_TYPE.WEAPONS);
                    break;
            }
            initItem(slot, type);
        }

    }

    private Entity getType(ITEM_SLOT slot, DC_TYPE TYPE) {
        if (hero.getItem(slot) != null) {
            return hero.getItem(slot);
        }
        String string = hero.getProperty(slot.getProp());
        Entity type = DataManager.getType(string, C_OBJ_TYPE.SLOT_ITEMS);

        if (type == null) {
            if (StringMaster.isInteger(string)) {
                Obj obj = hero.getGame().getObjectById(StringMaster.getInteger(string));
                if (obj != null) {
                    type = obj.getType();
                } else {
                    type = hero.getGame().getTypeById(StringMaster.getInteger(string));
                }
            }
        }
        return type;
    }

    @Override
    public void refresh() {
        if (hero.isDirty()) {
            init();
            removeAll();
            addComps();
        }
    }

    public void setHero(Unit hero2) {
        hero = hero2;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        ListItem<Entity> clickedItem = (ListItem<Entity>) e.getSource();
        if (clickedItem == null) {
            return;
        }
        clickedItem.setSelected(false);
        clickedItem.refresh();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        ListItem<Entity> clickedItem = (ListItem<Entity>) e.getSource();

        if (e.getClickCount() > 1 || SwingUtilities.isRightMouseButton(e)) {
            // TODO remove item
            boolean armor = clickedItem == itemMap.get(ItemEnums.ITEM_SLOT.ARMOR);
            if (armor) {
                if (!ArmorMaster.isArmorUnequipAllowed(hero)) {
                    DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
                    return;
                }
            }

            int n = armor ? 2 : 1;

//            if (itemListManager instanceof DC_InventoryManager) {
//                if (!((DC_InventoryManager) itemListManager).hasOperations(n)) {
//                    DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
//                    return;
//                }
//            }

            CharacterCreator.getHeroManager().removeSlotItem(
             hero,
             new MapMaster<ITEM_SLOT, ListItem<Entity>>().getKeyForValue(itemMap,
              clickedItem));
            itemMap.remove(clickedItem);
            clickedItem.refresh();
//            if (itemListManager instanceof DC_InventoryManager) {
//                ((DC_InventoryManager) itemListManager).operationDone(n, OPERATIONS.UNEQUIP, clickedItem
//                        .getValue().getName());
//
//            }

        } else {
            for (ListItem<Entity> item : itemMap.values()) {
                if (item == null) {
                    continue;
                }
                item.setSelected(clickedItem == item);
                item.refresh();

            }
            DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK);
            itemListManager.typeSelected(clickedItem.getValue(), null);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO drag and drop!

    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

}
