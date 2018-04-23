package eidolons.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.ability.InventoryTransactionManager;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.herocreator.CharacterCreator;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SymbolButton;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.stage.Closable;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.SHOW_INVENTORY;
import static main.system.GuiEventType.UPDATE_INVENTORY_PANEL;

public class CombatInventory extends TablePanel implements Closable {
    private InventoryPanel inventoryPanel;

    private Cell<Actor> actionPointsText;
    private SymbolButton doneButton;
    private SymbolButton cancelButton;
    private SymbolButton undoButton;

    public CombatInventory() {
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

        lower.addElement(undoButton = new SymbolButton(STD_BUTTON.UNDO))
                .fill(false).expand(0, 0).right()
                .pad(20, 0, 20, 0);

        lower.addElement(cancelButton = new SymbolButton(STD_BUTTON.CANCEL))
                .fill(false).expand(0, 0).right()
                .pad(20, 10, 20, 0);

        lower.add(doneButton = new SymbolButton(STD_BUTTON.OK))
         .fill(false).expand(0, 0).right()
         .pad(20, 10, 20, 10);

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
                GuiEventManager.trigger(GuiEventType.GAME_PAUSED );
                if (!isVisible())
                    open();
                setUserObject(param);
                initButtonListeners();
            }
        });
        GuiEventManager.bind(UPDATE_INVENTORY_PANEL, (obj) -> {
            setUserObject(getUserObject());
        });
    }


    private void initButtonListeners() {
        final InventoryDataSource source = (InventoryDataSource) getUserObject();
       doneButton.setRunnable(source.getDoneHandler());
        cancelButton.setRunnable(source.getCancelHandler());
        undoButton.setRunnable(source.getUndoHandler());
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
            source.getCancelHandler().run();
        } else {

        }
        setVisible(false);
        GuiEventManager.trigger(GuiEventType.GAME_RESUMED );
    }

    public void close() {
        close(null);
    }
}
