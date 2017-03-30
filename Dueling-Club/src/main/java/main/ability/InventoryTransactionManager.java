package main.ability;

import main.client.cc.CharacterCreator;
import main.client.cc.DC_HeroManager;
import main.client.cc.HeroManager;
import main.client.cc.gui.lists.dc.DC_InventoryManager;
import main.content.DC_TYPE;
import main.content.PROPS;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.Ref.KEYS;
import main.entity.item.DC_HeroItemObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.swing.frames.InventoryWindow;
import main.swing.frames.OperationWindow;
import main.swing.frames.PickUpWindow;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

public class InventoryTransactionManager {

    public static final WAIT_OPERATIONS OPERATION = WAIT_OPERATIONS.DIALOGUE_DONE;
    public static final PROPERTY[] INV_PROPS = {PROPS.QUICK_ITEMS, PROPS.INVENTORY,
     G_PROPS.MAIN_HAND_ITEM, G_PROPS.ARMOR_ITEM, G_PROPS.OFF_HAND_ITEM, PROPS.JEWELRY,};
    private DC_Game game;
    private OperationWindow window;
    private HeroManager heroManager;

    public InventoryTransactionManager(DC_Game game) {
        heroManager = new DC_HeroManager(game);
        CharacterCreator.setDC_HeroManager(heroManager);
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
         to.getGame().getCellByCoordinate(to.getCoordinates()))) {
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
                    if (i instanceof DC_WeaponObj) {

                        DC_WeaponObj weaponObj = (DC_WeaponObj) i;
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
                    to.getQuickItems().add((DC_QuickItemObj) i);
                } else {
                    to.equip((DC_HeroItemObj) i, slot);
                }
            }
        }
    }

    public boolean showInvWindow(Unit hero, Integer nOfOperations) {
        return showInvWindow(hero, nOfOperations, false);

    }

    public boolean showInvWindow(Unit hero, Integer nOfOperations, boolean pickUp) {
        // if (window == null)
        window = (pickUp) ? new PickUpWindow(this, hero, nOfOperations) : new InventoryWindow(this,
         hero, nOfOperations);
        window.open();


        // else {
        // window.setHero(hero);
        // window.setNumberOfOperations(nOfOperations);
        // window.refresh();
        // }
//        SwingUtilities.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
//                window.open();
//            }
//        });

        return (boolean) WaitMaster.waitForInput(OPERATION);
    }

    public DC_InventoryManager getInvListManager() {
        return game.getInventoryManager();
    }


    public void resetHero(Unit hero, ObjType bufferedType) {

        hero.resetObjectContainers(true);

    }

    public OperationWindow getWindow() {
        return window;
    }

    public boolean isActive() {
        if (getWindow() == null) {
            return false;
        }
        if (getWindow().getFrame() == null) {
            return false;
        }
        return window.getFrame().isVisible();
    }

}
