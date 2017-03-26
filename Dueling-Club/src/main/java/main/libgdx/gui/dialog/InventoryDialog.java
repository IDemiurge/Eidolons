package main.libgdx.gui.dialog;

import com.badlogic.gdx.scenes.scene2d.Group;
import main.ability.InventoryManager;
import main.client.cc.CharacterCreator;
import main.client.cc.gui.misc.PoolComp;
import main.content.PROPS;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.swing.frames.OperationDialog;
import main.system.threading.WaitMaster;

/**
 * Created by JustMe on 1/6/2017.
 */
public class InventoryDialog extends Group implements OperationDialog{

    protected Unit heroModel;
    protected InventoryManager inventoryManager;
    protected Unit hero;
    protected int nOfOperations;
    protected ObjType bufferedType;
    protected PoolComp operationsPool;
    protected DC_Obj cell;
    private String cachedValue;
    private String operationsData;

//    InventoryPanel inventoryPanel;

    public void refresh() {
//        inventoryPanel.setHero(heroModel);
//        inventoryPanel.refresh();
        inventoryManager.getInvListManager().setNumberOfOperations(getNumberOfOperations());
        operationsPool.setText(getPoolText());
    }

    @Override
    public void open() {
        operationsData = "";
        cachedValue = cell.getProperty(PROPS.DROPPED_ITEMS);
        setVisible(true);
    }


    @Override
    public void done() {
        InventoryManager.updateType(getHero());
        WaitMaster.receiveInput(InventoryManager.OPERATION, true);
        CharacterCreator.getHeroManager().removeHero(heroModel);
        setVisible(false);
    }

    @Override
    public String getOperationsData() {
        if (operationsData == null) {
            operationsData = "";
        }
        return operationsData;
    }

    @Override
    public void cancel() {
        cell.setProperty(PROPS.DROPPED_ITEMS, cachedValue);
        inventoryManager.resetHero(getHero(), bufferedType);
        WaitMaster.receiveInput(InventoryManager.OPERATION, false);
        setVisible(false);
        CharacterCreator.getHeroManager().removeHero(heroModel);
    }


    @Override
    public String getPoolTooltip() {
        return null;
    }



    @Override
    public String getPoolText() {
        return null;
    }

    @Override
    public Unit getHero() {
        return null;
    }

    @Override
    public void setHero(Unit hero) {

    }

    @Override
    public int getNumberOfOperations() {
        return 0;
    }

    @Override
    public void setNumberOfOperations(int nOfOperations) {

    }
}
