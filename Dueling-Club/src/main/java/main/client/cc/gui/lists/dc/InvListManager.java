package main.client.cc.gui.lists.dc;

import main.client.cc.CharacterCreator;
import main.client.cc.gui.lists.HeroListPanel;
import main.content.OBJ_TYPES;
import main.content.PROPS;
import main.content.properties.PROPERTY;
import main.entity.Entity;
import main.entity.obj.DC_HeroItemObj;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

/**
 * 2 properties?
 *
 * @author JustMe
 */
public class InvListManager extends DC_ItemListManager {
    public InvListManager(DC_Game game) {
        super(game, true);
    }

    public void processOperationCommand(String string) {
        for (String substring : StringMaster.openContainer(string)) {
            OPERATIONS operation = new EnumMaster<OPERATIONS>().retrieveEnumConst(OPERATIONS.class,
                    substring.split(StringMaster.PAIR_SEPARATOR)[0]);
            String typeName = (substring.split(StringMaster.PAIR_SEPARATOR)[1]);
            execute(operation, typeName);
        }

    }

    public void execute(OPERATIONS operation, String typeName) {
        DC_HeroObj unit = getHero();
        boolean alt = false;
        DC_HeroItemObj item = null;
        if (operation == OPERATIONS.PICK_UP) {
            item = unit.getGame().getDroppedItemManager().findDroppedItem(typeName,
                    unit.getCoordinates());
        }

        Boolean mode = null;
        if (operation == OPERATIONS.UNEQUIP_QUICK_SLOT) {
            mode = true;
        }
        if (operation == OPERATIONS.EQUIP || operation == OPERATIONS.EQUIP_QUICK_SLOT) {
            mode = false;
        }

        item = unit.findItem(typeName, mode);
        Entity type = item.getType();
        switch (operation) {
            case EQUIP:
                CharacterCreator.getHeroManager().addSlotItem(getHero(), type, alt);
                break;
            case DROP:
                unit.getGame().getDroppedItemManager().drop(item, unit);
                break;
            case EQUIP_QUICK_SLOT:
                // CharacterCreator.getHeroManager().addItem(unit, type,
                // OBJ_TYPES.ITEMS, PROPS.QUICK_ITEMS, true);
                CharacterCreator.getHeroManager().addQuickItem(unit, type);
                break;
            case PICK_UP:
                unit.getGame().getDroppedItemManager().pickUp(unit, type);
                break;
            case UNEQUIP:
                unit.unequip(item, false);
                break;
            case UNEQUIP_QUICK_SLOT:
                CharacterCreator.getHeroManager().removeQuickSlotItem(unit, type);
                break;
        }

    }

    @Override
    public boolean operationDone(OPERATIONS operation, String string) {
        return operationDone(1, operation, string);
    }

    protected boolean addType(ObjType type, HeroListPanel hlp, boolean alt) {
        if (!hasOperations()) {
            SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
            return false;
        }
        // String s = CharacterCreator.getHeroManager()
        // .checkRequirements(getHero(), type, false);
        // if (s != null) {
        // Err.info(s);
        // return false;
        // } TODO dynamic ATTR/MSTR reqs?
        int result = CharacterCreator.getHeroManager().addSlotItem(getHero(), type, alt);
        operationDone(result, OPERATIONS.EQUIP, type.getName());
        return true;
    }

    @Override
    public boolean operationDone(int n, OPERATIONS operation, String string) {
        boolean result = super.operationDone(n, operation, string);
        getHero().getGame().getInventoryManager().getWindow()
                .appendOperationData(operation, string);

        getHero().getGame().getInventoryManager().getWindow().setNumberOfOperations(
                numberOfOperations);
        return result;
    }

    @Override
    protected void removeType(Entity type, HeroListPanel hlp, PROPERTY p) {
        if (!hasOperations()) {
            SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
            return;
        }
        OPERATIONS operations = OPERATIONS.UNEQUIP;
        if (p == PROPS.INVENTORY) {
            CharacterCreator.getHeroManager().removeItem(getHero(), type, p, TYPE, true);
        } else {
            if (type.getOBJ_TYPE_ENUM() == OBJ_TYPES.JEWELRY) {
                CharacterCreator.getHeroManager().removeJewelryItem(getHero(), type);
            } else if (p == PROPS.QUICK_ITEMS) {
                CharacterCreator.getHeroManager().removeQuickSlotItem(getHero(), type);
                operations = OPERATIONS.UNEQUIP_QUICK_SLOT;
            }
        }
        operationDone(operations, type.getName());
    }

    public enum OPERATIONS {
        PICK_UP, DROP, UNEQUIP, UNEQUIP_QUICK_SLOT, EQUIP, EQUIP_QUICK_SLOT,
    }

}
