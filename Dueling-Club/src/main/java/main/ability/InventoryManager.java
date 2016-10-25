package main.ability;

import main.client.cc.CharacterCreator;
import main.client.cc.DC_HeroManager;
import main.client.cc.HeroManager;
import main.client.cc.gui.lists.dc.InvListManager;
import main.content.CONTENT_CONSTS.ITEM_SLOT;
import main.content.CONTENT_CONSTS.WEAPON_CLASS;
import main.content.OBJ_TYPES;
import main.content.PROPS;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.entity.Ref.KEYS;
import main.entity.obj.*;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.swing.frames.InventoryWindow;
import main.swing.frames.OperationWindow;
import main.swing.frames.PickUpWindow;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import javax.swing.*;

public class InventoryManager {

    public static final WAIT_OPERATIONS OPERATION = WAIT_OPERATIONS.DIALOGUE_DONE;
    public static final PROPERTY[] INV_PROPS = {PROPS.QUICK_ITEMS, PROPS.INVENTORY,
            G_PROPS.MAIN_HAND_ITEM, G_PROPS.ARMOR_ITEM, G_PROPS.OFF_HAND_ITEM, PROPS.JEWELRY,};
    private InvListManager invListManager;
    private OperationWindow window;
    private HeroManager heroManager;

    public InventoryManager(DC_Game game) {
        heroManager = new DC_HeroManager(game);
        CharacterCreator.setDC_HeroManager(heroManager);
        setInvListManager(new InvListManager(game));
    }

    public static void updateType(DC_HeroObj hero) {
        hero.setType(new ObjType(hero.getType()));
        // TODO init type?
        ObjType type = hero.getType();
        hero.getGame().initType(type);
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

    public static void equipOriginalItems(DC_HeroObj to, Obj from) {
        for (Obj i : to.getGame().getDroppedItemManager().getDroppedItems(
                to.getGame().getCellByCoordinate(to.getCoordinates()))) {
            if (i.getRef().getSourceObj() == from) {
                ITEM_SLOT slot = null;

                if (i.getOBJ_TYPE_ENUM() == OBJ_TYPES.ARMOR) {
                    slot = ITEM_SLOT.ARMOR;
                } else {
                    if (from.getRef().getObj(KEYS.WEAPON) == i) {
                        slot = ITEM_SLOT.MAIN_HAND;
                    } else if (from.getRef().getObj(KEYS.OFFHAND) == i) {
                        slot = ITEM_SLOT.OFF_HAND;
                    }
                }

                if (slot == null)
                    if (i instanceof DC_WeaponObj) {

                        DC_WeaponObj weaponObj = (DC_WeaponObj) i;
                        if (weaponObj.getWeaponClass() == WEAPON_CLASS.MAIN_HAND_ONLY
                                || weaponObj.getWeaponClass() == WEAPON_CLASS.TWO_HANDED
                                || weaponObj.getWeaponClass() == WEAPON_CLASS.DOUBLE)
                            slot = ITEM_SLOT.MAIN_HAND;
                        else if (to.getMainWeapon() != null)
                            slot = ITEM_SLOT.OFF_HAND;
                        else
                            slot = ITEM_SLOT.MAIN_HAND;

                    }
                // ITEM_SLOT.MAIN_HAND;
                to.getGame().getDroppedItemManager().pickedUp(i);
                i.setRef(to.getRef());
                // equip() !
                if (slot == null)
                    to.getQuickItems().add((DC_QuickItemObj) i);
                else
                    to.equip((DC_HeroItemObj) i, slot);
            }
        }
    }

    public boolean showInvWindow(DC_HeroObj hero, Integer nOfOperations) {
        return showInvWindow(hero, nOfOperations, false);

    }

    public boolean showInvWindow(DC_HeroObj hero, Integer nOfOperations, boolean pickUp) {
        // if (window == null)
        window = (pickUp) ? new PickUpWindow(this, hero, nOfOperations) : new InventoryWindow(this,
                hero, nOfOperations);

        // else {
        // window.setHero(hero);
        // window.setNumberOfOperations(nOfOperations);
        // window.refresh();
        // }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                window.open();
            }
        });

        return (boolean) WaitMaster.waitForInput(OPERATION);
    }

    public InvListManager getInvListManager() {
        return invListManager;
    }

    public void setInvListManager(InvListManager invListManager) {
        this.invListManager = invListManager;
    }

    public void resetHero(DC_HeroObj hero, ObjType bufferedType) {

        hero.resetObjectContainers(true);

    }

    public OperationWindow getWindow() {
        return window;
    }

    public boolean isActive() {
        if (getWindow() == null)
            return false;
        if (getWindow().getFrame() == null)
            return false;
        return window.getFrame().isVisible();
    }

}
