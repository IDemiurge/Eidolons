package eidolons.entity.item;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler;
import main.entity.Entity;

/**
 * 2 properties?
 *
 * @author JustMe
 */
public class DC_InventoryManager {

    protected Integer operationsLeft = 0;
    protected Integer operationsPool = 0;
    private DC_Game game;
    private Unit hero;
    private InventoryClickHandler clickHandler;

    public DC_InventoryManager(DC_Game game) {
        this.game = game;
    }

    public boolean hasOperations() {
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
    }


    public boolean canDoOperation(OPERATIONS operation, Entity type) {
        if (ExplorationMaster.isExplorationOn()) {
            return true;
        }

        if (!hasOperations()) {
            return false;
        }

//        String s = CharacterCreator.getHeroManager()
//         .checkRequirements(getHero(), type, RequirementsManager.NORMAL_MODE);
        // if (s != null) {
        return true;
    }


    public boolean operationDone(OPERATIONS operation ) {
        return operationDone(1, operation );
    }


    public boolean operationDone(int n, OPERATIONS operation ) {
        setOperationsLeft(getOperationsLeft() - n);
        boolean result = hasOperations();
        return result;
    }


    public Unit getHero() {
        if (hero == null) {
            return game.getManager().getActiveObj();
        }
        return hero;
    }

    public enum OPERATIONS {
        PICK_UP, DROP, UNEQUIP, UNEQUIP_QUICK_SLOT, EQUIP, EQUIP_QUICK_SLOT,
    }

}
