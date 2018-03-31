package eidolons.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.ability.InventoryTransactionManager;
import eidolons.client.cc.CharacterCreator;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.gui.panels.dc.ButtonStyled;
import eidolons.libgdx.gui.panels.dc.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.panels.dc.TablePanel;
import eidolons.libgdx.gui.panels.dc.ValueContainer;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.stage.Closable;
import main.system.GuiEventManager;
import main.system.threading.WaitMaster;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.SHOW_INVENTORY;

public class InventoryWithAction extends TablePanel implements Closable {
    private InventoryPanel inventoryPanel;

    private Cell<Actor> actionPointsText;
    private Cell<Actor> doneButton;
    private Cell<Actor> cancelButton;
    private Cell<Actor> undoButton;

    public InventoryWithAction() {
        TextureRegion textureRegion = new TextureRegion(getOrCreateR("UI/components/inventory_background.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);

        inventoryPanel = new InventoryPanel();
        inventoryPanel.setBackground((Drawable) null);
        addElement(inventoryPanel);
        row();

        final TablePanel<Actor> lower = new TablePanel<>();
        addElement(lower).pad(0, 20, 20, 20);

        actionPointsText = lower.addElement(null).left();

//        undoButton = lower.addElement(new ButtonStyled(STD_BUTTON.UNDO))
//                .fill(false).expand(0, 0).right()
//                .pad(20, 0, 20, 0).size(50, 50);

//        cancelButton = lower.addElement(new ButtonStyled(STD_BUTTON.CANCEL))
//                .fill(false).expand(0, 0).right()
//                .pad(20, 10, 20, 0).size(50, 50);

        doneButton = lower.addElement(new ButtonStyled(STD_BUTTON.OK))
         .fill(false).expand(0, 0).right()
         .pad(20, 10, 20, 10).size(50, 50);

        bindListeners();

        setVisible(false);
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

        GuiEventManager.bind(SHOW_INVENTORY, (obj) -> {
            final Object param = obj.get();
            if (param instanceof Boolean) {
                close((Boolean) param);
            } else {
                if (!isVisible())
                    open();
                setUserObject(param);
                initButtonListeners();
            }
        });
    }


    private void initButtonListeners() {
        final InventoryDataSource source = (InventoryDataSource) getUserObject();
        ButtonStyled button = (ButtonStyled) doneButton.getActor();
        button.clearListeners();
        button.addListener(source.getDoneHandler());
//        button.setDisabled(source.isDoneDisabled());

//        button = (ButtonStyled) cancelButton.getActor();
//        button.addListener(source.getCancelHandler());
//        button.setDisabled(source.isCancelDisabled());

//        button = (ButtonStyled) undoButton.getActor();
//        button.addListener(source.getUndoHandler());
//        button.setDisabled(source.isUndoDisabled());
    }

    @Override
    public void clear() {
        super.clear();

        doneButton.getActor().clearListeners();
        cancelButton.getActor().clearListeners();
        undoButton.getActor().clearListeners();
        close();
    }

    @Override
    public void updateAct(float delta) {
        if (getUserObject() == null)
            return;
        super.updateAct(delta);

        final InventoryDataSource source = (InventoryDataSource) getUserObject();
        if (ExplorationMaster.isExplorationOn()) {
            actionPointsText.setActor(new ValueContainer("Free Mode", ""));
        } else {
            actionPointsText.setActor(new ValueContainer("Actions available:",
             source.getOperationsString()));
        }
        initButtonListeners();
    }


    public void close(Boolean result) {
        if (result == null)
            result = false;
        InventoryDataSource source = (InventoryDataSource) getUserObject();
        WaitMaster.receiveInput(InventoryTransactionManager.OPERATION, result);
        CharacterCreator.getHeroManager().removeHero(source.getUnit());
        if (!ExplorationMaster.isExplorationOn()) {
            source.getCancelHandler().cancel();
        } else {

        }
        setVisible(false);
    }

    public void close() {
        close(null);
    }
//    public void open() {
//        if (getStage() instanceof StageWithClosable) {
//            ((StageWithClosable) getStage()).closeDisplayed();
//            ((StageWithClosable) getStage()).setDisplayedClosable(this);
//        }
//        setVisible(true);
//    }
}
