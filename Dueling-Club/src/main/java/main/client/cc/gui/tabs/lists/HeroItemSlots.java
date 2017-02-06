package main.client.cc.gui.tabs.lists;

import main.client.cc.CharacterCreator;
import main.client.cc.gui.MainPanel;
import main.client.cc.gui.lists.ItemListManager;
import main.client.cc.gui.lists.dc.InvListManager;
import main.client.cc.gui.lists.dc.InvListManager.OPERATIONS;
import main.content.CONTENT_CONSTS.ITEM_SLOT;
import main.content.C_OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.Obj;
import main.game.battlefield.ArmorMaster;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.list.ListItem;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.MapMaster;
import main.system.auxiliary.StringMaster;
import main.system.graphics.MigMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

public class HeroItemSlots extends G_Panel implements MouseListener {

    private static final int WHITE_SPACES = 18;

    private DC_HeroObj hero;

    private Map<ITEM_SLOT, ListItem<Entity>> itemMap = new HashMap<>();
    private ItemListManager itemListManager;
    private int obj_size = GuiManager.getSmallObjSize();

    int ARMOR_OFFSET = obj_size * 2 / 3;

    public HeroItemSlots(DC_HeroObj hero, ItemListManager itemListManager) {
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

        for (ITEM_SLOT slot : ITEM_SLOT.values()) {

            ListItem<Entity> item = itemMap.get(slot);

            String pos = "";
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
                if (slot != ITEM_SLOT.ARMOR)

                {
                    boolean offhand = slot == ITEM_SLOT.OFF_HAND;
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
            item = new ListItem<Entity>(type, false, false, obj_size);
            item.addMouseListener(this);
            itemMap.put(slot, item);
        } else {
            item.setObj(type);
            item.refresh();
        }
        return item;
    }

    public void init() {
        for (ITEM_SLOT slot : ITEM_SLOT.values()) {
            Entity type = null;
            switch (slot) {
                case ARMOR:
                    type = getType(slot, OBJ_TYPES.ARMOR);
                    break;
                case MAIN_HAND:
                    type = getType(slot, OBJ_TYPES.WEAPONS);
                    break;
                case OFF_HAND:
                    type = getType(slot, OBJ_TYPES.WEAPONS);
                    break;
            }
            initItem(slot, type);
        }

    }

    private Entity getType(ITEM_SLOT slot, OBJ_TYPES TYPE) {
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

    public void setHero(DC_HeroObj hero2) {
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
            boolean armor = clickedItem == itemMap.get(ITEM_SLOT.ARMOR);
            if (armor) {
                if (!ArmorMaster.isArmorUnequipAllowed(hero)) {
                    SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
                    return;
                }
            }

            int n = armor ? 2 : 1;

            if (itemListManager instanceof InvListManager) {
                if (!((InvListManager) itemListManager).hasOperations(n)) {
                    SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
                    return;
                }
            }

            CharacterCreator.getHeroManager().removeSlotItem(
                    hero,
                    new MapMaster<ITEM_SLOT, ListItem<Entity>>().getKeyForValue(itemMap,
                            clickedItem));
            itemMap.remove(clickedItem);
            clickedItem.refresh();
            if (itemListManager instanceof InvListManager) {
                ((InvListManager) itemListManager).operationDone(n, OPERATIONS.UNEQUIP, clickedItem
                        .getValue().getName());

            }

        } else {
            for (ListItem<Entity> item : itemMap.values()) {
                if (item == null) {
                    continue;
                }
                item.setSelected(clickedItem == item);
                item.refresh();

            }
            SoundMaster.playStandardSound(STD_SOUNDS.CLICK);
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
