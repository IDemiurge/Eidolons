package eidolons.entity.mngr.item;

import eidolons.game.exploration.handlers.ExplorationMaster;

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

    public boolean operationDone(OPERATIONS operation) {
        if (ExplorationMaster.isExplorationOn()) {
            return true;
        }
        return operationDone(1, operation);
    }


    public boolean operationDone(int n, OPERATIONS operation) {
        setOperationsLeft(getOperationsLeft() - n);
        return hasOperations();
    }


    public boolean isFreeMode() {
        if (ExplorationMaster.isExplorationOn())
            return true;
        return freeMode;
    }

    public void setFreeMode(boolean freeMode) {
        this.freeMode = freeMode;
    }

    public enum OPERATIONS {
        PICK_UP, DROP, UNEQUIP, UNEQUIP_QUICK_SLOT, EQUIP,EQUIP_RESERVE, EQUIP_QUICK_SLOT, BUY, SELL,
        STASH, UNSTASH, DESTROY,
    }

}
