package main.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.ButtonStyled;
import main.libgdx.gui.panels.dc.ButtonStyled.STD_BUTTON;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import main.system.GuiEventManager;

import static main.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.SHOW_INVENTORY;

public class InventoryWithAction extends TablePanel {
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
            if (param == null) {
                setVisible(false);
            } else {
                setVisible(true);
                setUserObject(param);
                initButtonListeners();
            }
        });
    }

    private void initButtonListeners() {
        final InventoryDataSource source = (InventoryDataSource) getUserObject();
        ButtonStyled button = (ButtonStyled) doneButton.getActor();
        button.getListeners().clear();
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
        setVisible(false);
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);

        final InventoryDataSource source = (InventoryDataSource) getUserObject();

        actionPointsText.setActor(new ValueContainer("Actions available:", source.getOperationsString()));
        initButtonListeners();
    }
}
