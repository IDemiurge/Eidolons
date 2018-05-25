package eidolons.entity.item;

import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.herocreator.HeroManager;
import main.entity.Entity;

/**
 * 2 properties?
 *
 * @author JustMe
 */
public class DC_InventoryManager {

    protected Integer operationsLeft = 0;
    protected Integer operationsPool = 0;
    private boolean freeMode;

    public DC_InventoryManager() {

    }

    public boolean hasOperations() {
        if (isFreeMode())
            return true;
        return getOperationsLeft() > 0;
    }


    public Integer getOperationsLeft() {
        return operationsLeft;
    }


    public void setOperationsLeft(Integer operationsLeft) {
        this.operationsLeft = operationsLeft;
    }

    public Integer getOperationsPool() {
        return operationsPool;
    }

    public void setOperationsPool(Integer operationsPool) {
        this.operationsPool = operationsPool;
        setOperationsLeft(operationsPool);
        setFreeMode(false);
    }


    public boolean canDoOperation(OPERATIONS operation, Entity type, Object arg) {
        if (arg == null) {
            switch (operation) {
                case EQUIP:
                    return false;
            }
        }
        switch (operation) {
            case EQUIP_QUICK_SLOT:
                if (!HeroManager.isQuickItem(type)){
                    return false;
                }
        }
        if (!hasOperations() && !ExplorationMaster.isExplorationOn()) {
            return false;
        }

//        String s = CharacterCreator.getHeroManager()
//         .checkRequirements(getHero(), type, RequirementsManager.NORMAL_MODE);
        // if (s != null) {
        return true;
    }


    public boolean operationDone(OPERATIONS operation) {
        if (ExplorationMaster.isExplorationOn()) {
            return true;
        }
        return operationDone(1, operation);
    }


    public boolean operationDone(int n, OPERATIONS operation) {
        setOperationsLeft(getOperationsLeft() - n);
        boolean result = hasOperations();
        return result;
    }


    public boolean isFreeMode() {
        return freeMode;
    }

    public void setFreeMode(boolean freeMode) {
        this.freeMode = freeMode;
    }

    public enum OPERATIONS {
        PICK_UP, DROP, UNEQUIP, UNEQUIP_QUICK_SLOT, EQUIP, EQUIP_QUICK_SLOT,
    }

}
