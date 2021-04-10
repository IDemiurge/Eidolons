package libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.ability.InventoryTransactionManager;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import libgdx.StyleHolder;
import libgdx.TiledNinePatchGenerator;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.panels.TablePanel;
import libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import libgdx.stage.Blocking;
import libgdx.stage.DragManager;
import libgdx.stage.StageWithClosable;
import libgdx.gui.generic.btn.ButtonStyled;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.FontMaster.FONT;
import main.system.threading.WaitMaster;

import static main.system.GuiEventType.*;

public class CombatInventory extends TablePanel implements Blocking {

    private final Cell<Actor> actionPointsText;
    private final Cell<Actor> weightText;
    private final Cell<Actor> slotsText;
    private final Cell<Actor> goldText;
    private final SymbolButton doneButton;
    private SymbolButton cancelButton;
    private SymbolButton undoButton;

    public CombatInventory() {
        TextureRegion textureRegion = new TextureRegion(
         TiledNinePatchGenerator.getOrCreateNinePatch(TiledNinePatchGenerator.NINE_PATCH.SAURON, TiledNinePatchGenerator.BACKGROUND_NINE_PATCH.PATTERN,
          570, 835));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);

        InventoryPanel inventoryPanel = new InventoryPanel();
        inventoryPanel.setBackground((Drawable) null);
        addElement(inventoryPanel);
        row();

        final TablePanel<Actor> info = new TablePanel<>();
        addElement(info).pad(0, 20, 20, 20);
        slotsText = info.addElement(null).left();
        weightText = info.addElement(null).center();
        goldText = info.addElement(null).right();
        info.setBackground(NinePatchFactory.getLightPanelDrawable());
        row();
        final TablePanel<Actor> lower = new TablePanel<>();
        addElement(lower).pad(0, 20, 20, 20);

        actionPointsText = lower.addElement(null).left();

        if (!HqDataMaster.isSimulationOff())
            lower.addElement(
             undoButton = new SymbolButton(ButtonStyled.STD_BUTTON.UNDO)).fill(false).expand(0, 0).right()
             .pad(20, 0, 20, 0);

        if (!HqDataMaster.isSimulationOff())
            lower.addElement(cancelButton = new SymbolButton(ButtonStyled.STD_BUTTON.CANCEL))
             .fill(false).expand(0, 0).right()
             .pad(20, 10, 20, 0);

        lower.add(doneButton = new SymbolButton(ButtonStyled.STD_BUTTON.OK))
         .fill(false).expand(0, 0).right()
         .pad(20, 10, 20, 10);

        bindListeners();

        setVisible(false);
    }

    @Override
    public StageWithClosable getStageWithClosable() {
        return (StageWithClosable) getStage();
    }

    private void bindListeners() {
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return true;
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                event.stop();
                return true;
            }
        });

        GuiEventManager.bind(TOGGLE_INVENTORY, (obj) -> {
            if (isVisible()) {
                show(null);
            } else {
                show(obj.get());
            }
        });
        GuiEventManager.bind(SHOW_INVENTORY, (obj) -> {
            Unit unit = (Unit) obj.get();
            show(new InventoryDataSource((unit)));
        });
        GuiEventManager.bind(UPDATE_INVENTORY_PANEL, (obj) -> {
            setUserObject(getUserObject());
        });
    }

    private void show(Object param) {
        if (param==null ){
            close(true);
            return;
        }
        if (param instanceof Boolean) {
            if (HqDataMaster.isSimulationOff()) {
                close(true);
            } else
                close((Boolean) param);
        } else {
            //                GuiEventManager.trigger(GuiEventType.GAME_PAUSED);
            if (!isVisible())
                open();
            setUserObject(param);
            initButtonListeners();
        }
    }


    private void initButtonListeners() {
        final InventoryDataSource source = getUserObject();
        doneButton.setRunnable(source.getDoneHandler());

        if (!HqDataMaster.isSimulationOff()) {
            cancelButton.setRunnable(source.getCancelHandler());
            undoButton.setRunnable(source.getUndoHandler());
        }
    }

    @Override
    public void clear() {
        super.clear();
        close();
    }

    @Override
    public void updateAct(float delta) {
        if (getUserObject() == null)
            return;
        super.updateAct(delta);

        final InventoryDataSource source = getUserObject();
        String header = "Free Mode";

        if (cancelButton != null) {
            cancelButton.setVisible(!ExplorationMaster.isExplorationOn());
        }
        if (!ExplorationMaster.isExplorationOn()) {
            header = "Operations:\n" +
             source.getOperationsString();
        }
        String on = DragManager.isOff() ? "OFF" : "ON";
        ValueContainer controls = new ValueContainer(
         StyleHolder.getSizedLabelStyle(FONT.MAIN, 1400),
         header,
         "\n**Drag'n'drop is [" +
          on +
          "]**\n" +
          "[Right click]: unequip or drop onto the ground\n" +
          "[Double left-click]: default equip/unequip \n" +
          "[Alt-Click]: equip weapon in quick slot \n");
        controls.setBackground(NinePatchFactory.getLightPanelDrawable());
        actionPointsText.setActor(controls
        );

        weightText.setActor(new ValueContainer(
         StyleHolder.getSizedLabelStyle(FONT.MAIN, 1800),
         "Weight: ", source.getWeightInfo()));

        goldText.setActor(new ValueContainer(
         StyleHolder.getSizedLabelStyle(FONT.MAIN, 1800),
         "Gold: ", source.getGoldInfo()));

        slotsText.setActor(new ValueContainer(
         StyleHolder.getSizedLabelStyle(FONT.MAIN, 1800),
         "Quick Slots: ", source.getSlotsInfo()));
        initButtonListeners();
    }

    @Override
    public InventoryDataSource getUserObject() {
        return (InventoryDataSource) super.getUserObject();
    }

    public void close() {
        close(HqDataMaster.isSimulationOff());
    }

    public void close(Boolean result) {
        if (result == null)
            result = false;

        if (!result)
            if (!HqDataMaster.isSimulationOff()) {
                result = true;
            }

        if (!result)
            GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT,
             "Inventory operations cancelled!");
        else {
            int x = getUserObject().getUnit().getX();
            int y = getUserObject().getUnit().getY();
            Eidolons.getGame().getDroppedItemManager().reset(x, y);
        }

        WaitMaster.receiveInput(InventoryTransactionManager.OPERATION, result);
        //        if (ExplorationMaster.isExplorationOn()) {
        //            GuiEventManager.trigger(GuiEventType.GAME_RESET );
        //        }
        //        setVisible(false);
        //        GuiEventManager.trigger(GuiEventType.GAME_RESUMED);
        getStageWithClosable().closeClosable(this);
    }


}
