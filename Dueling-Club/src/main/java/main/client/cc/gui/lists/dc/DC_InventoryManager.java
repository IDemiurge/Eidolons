package main.client.cc.gui.lists.dc;

import main.client.cc.CharacterCreator;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.PROPS;
import main.content.values.properties.PROPERTY;
import main.elements.conditions.RequirementsManager;
import main.entity.Entity;
import main.entity.item.DC_HeroItemObj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

/**
 * 2 properties?
 *
 * @author JustMe
 */
public class DC_InventoryManager   {

    private   DC_Game game;
    protected Integer numberOfOperations;
    private Unit hero;
    private OBJ_TYPE TYPE;

    public DC_InventoryManager(DC_Game game) {
        this.TYPE =  DC_TYPE.ITEMS;
        this.game =game;
    }


   
    public boolean hasOperations(int n) {
        return getNumberOfOperations() >= n;
    }

   
    public boolean hasOperations() {
        return getNumberOfOperations() > 0;
    }

   
    public Integer getNumberOfOperations() {
        return numberOfOperations;
    }

   
    public void setNumberOfOperations(Integer numberOfOperations) {
        this.numberOfOperations = numberOfOperations;
    }


    public void processOperationCommand(String string) {
        for (String substring : StringMaster.openContainer(string)) {
            OPERATIONS operation = new EnumMaster<OPERATIONS>().retrieveEnumConst(OPERATIONS.class,
                    substring.split(StringMaster.PAIR_SEPARATOR)[0]);
            String typeName = (substring.split(StringMaster.PAIR_SEPARATOR)[1]);
            execute(operation, typeName);
        }

    }

    public boolean tryExecuteOperation(OPERATIONS operation, String typeName) {
        if (!canDoOperation(operation, typeName)) {
            return  false;
        }
        execute(operation, typeName);
        return true;
    }

    private boolean canDoOperation(OPERATIONS operation, String typeName) {
        Entity type = null;
        String s = CharacterCreator.getHeroManager()
         .checkRequirements(getHero(), type, RequirementsManager.NORMAL_MODE);
        // if (s != null) {



        return true;
    }

    private void execute(OPERATIONS operation, String typeName) {
        Unit unit = getHero();
        boolean alt = false;
        DC_HeroItemObj item;
        if (operation == OPERATIONS.PICK_UP) {
            item = unit.getGame().getDroppedItemManager().findDroppedItem(typeName,
             unit.getCoordinates()); // TODO finish
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


   
    public boolean operationDone(OPERATIONS operation, String string) {
        return operationDone(1, operation, string);
    }

   
    public boolean operationDone(int n, OPERATIONS operation, String string) {
        setNumberOfOperations(getNumberOfOperations() - n);
        boolean result =hasOperations();
        getHero().getGame().getInventoryTransactionManager().getWindow()
         .appendOperationData(operation, string);

        getHero().getGame().getInventoryTransactionManager().getWindow().setNumberOfOperations(
         numberOfOperations);
        return result;
    }

    public boolean addType(ObjType itemType,   boolean alt) {
        if (!hasOperations()) {
            SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
            return false;
        }
        int result = CharacterCreator.getHeroManager().addSlotItem(getHero(), itemType, alt);
        operationDone(result, OPERATIONS.EQUIP, itemType.getName());
        return true;
    }


    public void removeType(Entity item,  PROPERTY p) {
        if (!hasOperations()) {
            SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
            return;
        }
        OPERATIONS operations = OPERATIONS.UNEQUIP;
        if (p == PROPS.INVENTORY) {
            CharacterCreator.getHeroManager().removeItem(getHero(), item, p, TYPE, true);
        } else {
            if (item.getOBJ_TYPE_ENUM() == DC_TYPE.JEWELRY) {
                CharacterCreator.getHeroManager().removeJewelryItem(getHero(), item);
            } else if (p == PROPS.QUICK_ITEMS) {
                CharacterCreator.getHeroManager().removeQuickSlotItem(getHero(), item);
                operations = OPERATIONS.UNEQUIP_QUICK_SLOT;
            }
        }
        operationDone(operations, item.getName());
    }

   
    public Unit getHero() {
        if (hero==null )
            return game.getManager().getActiveObj();
            return hero;
    }

    public void setHero(Unit hero) {
        this.hero = hero;
    }

    public enum OPERATIONS {
        PICK_UP, DROP, UNEQUIP, UNEQUIP_QUICK_SLOT, EQUIP, EQUIP_QUICK_SLOT,
    }

}
