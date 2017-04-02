package main.swing.frames;

import main.ability.InventoryTransactionManager;
import main.client.cc.CharacterCreator;
import main.client.cc.gui.lists.dc.DC_InventoryManager.OPERATIONS;
import main.client.cc.gui.misc.PoolComp;
import main.client.cc.gui.tabs.HeroItemTab;
import main.content.PROPS;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Dialog;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.StringMaster;
import main.system.graphics.ColorManager;
import main.system.graphics.GuiManager;
import main.system.threading.WaitMaster;

import java.awt.*;

public abstract class OperationWindow extends G_Dialog implements OperationDialog{
    protected static final String OK = "Done";
    protected static final String CANCEL = "Cancel";
    protected static final String INV = "Inventory";
    protected static final String OPERATION_TOOLTIP = " item actions left";
    protected G_Panel panel;
    protected Unit heroModel;
    protected InventoryTransactionManager inventoryManager;
    protected Unit hero;
    protected int nOfOperations;
    protected ObjType bufferedType;
    protected PoolComp operationsPool;
    protected DC_Obj cell;
    private String cachedValue;
    private String operationsData;

    public OperationWindow(InventoryTransactionManager inventoryManager, Unit hero, Integer nOfOperations) {
        this.setNumberOfOperations(nOfOperations);
        this.inventoryManager = inventoryManager;
        this.setHero(hero);
        init();
    }

    public void appendOperationData(OPERATIONS operation, String string) {
        operationsData = getOperationsData() + operation.toString() + StringMaster.PAIR_SEPARATOR
                + string + StringMaster.SEPARATOR;

    }

    protected abstract VISUALS getVisuals();

    protected abstract HeroItemTab getComponent();

    @Override
    public String getPoolTooltip() {
        return OPERATION_TOOLTIP;
    }

    public void init() {
        this.cell = (DC_Obj) hero.getGame().getCellByCoordinate(hero.getCoordinates());
        super.init();
        refresh();
    }
    @Override
    public void refresh() {
        getComponent().setHero(heroModel);
        getComponent().refresh();
        inventoryManager.getInvListManager().setOperationsLeft(getNumberOfOperations());
        operationsPool.setText(getPoolText());
    }

    @Override
    public void open() {
        operationsData = "";
        cachedValue = cell.getProperty(PROPS.DROPPED_ITEMS);
        show();
    }

    @Override
    public void done() {
        InventoryTransactionManager.updateType(getHero());
        WaitMaster.receiveInput(InventoryTransactionManager.OPERATION, true);
        CharacterCreator.getHeroManager().removeHero(heroModel);
        close();
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
        WaitMaster.receiveInput(InventoryTransactionManager.OPERATION, false);
        getFrame().setVisible(false);
        CharacterCreator.getHeroManager().removeHero(heroModel);
    }

    public Component createComponent() {
        panel = new G_Panel(getVisuals());
        operationsPool = new PoolComp(getPoolText(), getPoolTooltip(), false);
        //
        CustomButton okButton = new CustomButton(OK) {
            public void handleClick() {
                done();
            }

            protected boolean isMoreY() {
                return true;
            }
        };
        CustomButton cancelButton = new CustomButton(CANCEL) {
            protected boolean isMoreY() {
                return true;
            }

            public void handleClick() {
                cancel();
            }
        };

        panel.add(getComponent(), "id tab, pos " + GuiManager.PANEL_FRAME_WIDTH + " "
                + GuiManager.PANEL_FRAME_HEIGHT);
        panel.add(okButton, "@id ok, pos max_right-" + GuiManager.PANEL_FRAME_WIDTH + " max_top-"
                + GuiManager.PANEL_FRAME_HEIGHT);
        panel.add(cancelButton, "@id cancel, pos max_right-" + GuiManager.PANEL_FRAME_WIDTH
                + " ok.y2");
        panel.add(operationsPool, "@id op, pos max_right-" + GuiManager.PANEL_FRAME_WIDTH
                + " cancel.y2");
        panel.setBackground(ColorManager.BACKGROUND);
        return panel;
    }

    @Override
    public String getPoolText() {
        return getNumberOfOperations() + " left";
    }

    @Override
    public Unit getHero() {
        return hero;
    }

    @Override
    public void setHero(Unit hero) {
        this.hero = hero;
        InventoryTransactionManager.updateType(hero);
        bufferedType = hero.getType();
        heroModel = hero;
        inventoryManager.getInvListManager().setHero(heroModel);
        CharacterCreator.getHeroManager().addHero(heroModel);
    }

    @Override
    public int getNumberOfOperations() {
        return nOfOperations;
    }

    @Override
    public void setNumberOfOperations(int nOfOperations) {
        this.nOfOperations = nOfOperations;
        if (operationsPool != null) {
            operationsPool.setText(getPoolText());
            operationsPool.refresh();
        }
    }

    @Override
    protected boolean isReady() {
        return false;
    }

    @Override
    public boolean isCentered() {
        return true;
    }

    @Override
    public Dimension getSize() {
        return getVisuals().getSize();
    }

    @Override
    public String getTitle() {
        return null;
    }

}
