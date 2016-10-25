package main.swing.frames;

import main.ability.InventoryManager;
import main.client.cc.CharacterCreator;
import main.client.cc.gui.lists.dc.InvListManager.OPERATIONS;
import main.client.cc.gui.misc.PoolComp;
import main.client.cc.gui.tabs.HeroItemTab;
import main.content.PROPS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.type.ObjType;
import main.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Dialog;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;

import java.awt.*;

public abstract class OperationWindow extends G_Dialog {
    protected static final String OK = "Done";
    protected static final String CANCEL = "Cancel";
    protected static final String INV = "Inventory";
    protected static final String OPERATION_TOOLTIP = " item actions left";
    protected G_Panel panel;
    protected DC_HeroObj heroModel;
    protected InventoryManager inventoryManager;
    protected DC_HeroObj hero;
    protected int nOfOperations;
    protected ObjType bufferedType;
    protected PoolComp operationsPool;
    protected DC_Obj cell;
    private String cachedValue;
    private String operationsData;

    public OperationWindow(InventoryManager inventoryManager, DC_HeroObj hero, Integer nOfOperations) {
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

    protected String getPoolTooltip() {
        return OPERATION_TOOLTIP;
    }

    public void init() {
        this.cell = (DC_Obj) hero.getGame().getCellByCoordinate(hero.getCoordinates());
        super.init();
        refresh();
    }

    public void refresh() {
        getComponent().setHero(heroModel);
        getComponent().refresh();
        inventoryManager.getInvListManager().setNumberOfOperations(getNumberOfOperations());
        operationsPool.setText(getPoolText());
    }

    public void open() {
        operationsData = "";
        cachedValue = cell.getProperty(PROPS.DROPPED_ITEMS);
        show();
    }

    public void done() {
        InventoryManager.updateType(getHero());
        WaitMaster.receiveInput(InventoryManager.OPERATION, true);
        CharacterCreator.getHeroManager().removeHero(heroModel);
        close();
    }

    public String getOperationsData() {
        if (operationsData == null)
            operationsData = "";
        return operationsData;
    }

    public void cancel() {
        cell.setProperty(PROPS.DROPPED_ITEMS, cachedValue);
        inventoryManager.resetHero(getHero(), bufferedType);
        WaitMaster.receiveInput(InventoryManager.OPERATION, false);
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

    protected String getPoolText() {
        return getNumberOfOperations() + " left";
    }

    public DC_HeroObj getHero() {
        return hero;
    }

    public void setHero(DC_HeroObj hero) {
        this.hero = hero;
        InventoryManager.updateType(hero);
        bufferedType = hero.getType();
        heroModel = hero;
        inventoryManager.getInvListManager().setHero(heroModel);
        CharacterCreator.getHeroManager().addHero(heroModel);
    }

    public int getNumberOfOperations() {
        return nOfOperations;
    }

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
