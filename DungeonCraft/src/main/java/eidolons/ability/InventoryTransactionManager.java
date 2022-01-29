package eidolons.ability;

import eidolons.content.PROPS;
import eidolons.entity.item.HeroItem;
import eidolons.entity.item.handlers.DC_InventoryManager;
import eidolons.entity.item.QuickItem;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.unit.Unit;
import eidolons.game.core.game.DC_Game;
import main.content.DC_TYPE;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

public class InventoryTransactionManager {

    public static final WAIT_OPERATIONS OPERATION = WAIT_OPERATIONS.DIALOGUE_DONE;
    public static final PROPERTY[] INV_PROPS = {PROPS.QUICK_ITEMS, PROPS.INVENTORY,
     G_PROPS.MAIN_HAND_ITEM, G_PROPS.ARMOR_ITEM, G_PROPS.OFF_HAND_ITEM, PROPS.JEWELRY,};
    private final DC_Game game;
    private boolean active;

    public InventoryTransactionManager(DC_Game game) {
        this.game = game;
    }

    public static void updateType(Unit hero) {
        hero.setType(new ObjType(hero.getType()));

        ObjType type = hero.getType();
        hero.getGame().initType(type);// TODO init type?
        for (PROPERTY prop : INV_PROPS) {

            type.copyValue(prop, hero);
            // String property = hero.getProperty(prop);for (String item :
            // StringMaster.openContainer(property)) {
            // if (hero.getGame().getObjectById(StringMaster.getInteger(item))
            // != null) {
            // String string = hero.getGame()
            // .getObjectById(StringMaster.getInteger(item))
            // .getId().toString();
            // // .getName();
            // if (prop.isContainer())
            // type.addProperty(prop, string);
            // else
            // type.setProperty(prop, string);
            // }
            // }
            // type.setProperty(prop, property);
        }
    }

    public static void equipOriginalItems(Unit to, Obj from) {
        for (Obj i : to.getGame().getDroppedItemManager().getDroppedItems(
         to.getGame().getCell(to.getCoordinates()))) {
            if (i.getRef().getSourceObj() == from) {
                ITEM_SLOT slot = null;

                if (i.getOBJ_TYPE_ENUM() == DC_TYPE.ARMOR) {
                    slot = ItemEnums.ITEM_SLOT.ARMOR;
                } else {
                    if (from.getRef().getObj(KEYS.WEAPON) == i) {
                        slot = ItemEnums.ITEM_SLOT.MAIN_HAND;
                    } else if (from.getRef().getObj(KEYS.OFFHAND) == i) {
                        slot = ItemEnums.ITEM_SLOT.OFF_HAND;
                    }
                }

                if (slot == null) {
                    if (i instanceof WeaponItem) {

                        WeaponItem weaponObj = (WeaponItem) i;
                        if (weaponObj.getWeaponClass() == ItemEnums.WEAPON_CLASS.MAIN_HAND_ONLY
                         || weaponObj.getWeaponClass() == ItemEnums.WEAPON_CLASS.TWO_HANDED
                         || weaponObj.getWeaponClass() == ItemEnums.WEAPON_CLASS.DOUBLE) {
                            slot = ItemEnums.ITEM_SLOT.MAIN_HAND;
                        } else if (to.getMainWeapon() != null) {
                            slot = ItemEnums.ITEM_SLOT.OFF_HAND;
                        } else {
                            slot = ItemEnums.ITEM_SLOT.MAIN_HAND;
                        }

                    }
                }
                // ITEM_SLOT.MAIN_HAND;
                to.getGame().getDroppedItemManager().pickedUp(i);
                i.setRef(to.getRef());
                // equip() !
                if (slot == null) {
                    to.getQuickItems().add((QuickItem) i);
                } else {
                    to.equip((HeroItem) i, slot);
                }
            }
        }
    }


    public DC_InventoryManager getInvListManager() {
        return game.getInventoryManager();
    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
